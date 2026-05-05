package com.honya.bookstore.architecture;

import com.honya.bookstore.BookstoreApplication;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ModuleBoundaryEnforcementTest {

    @Test
    void modulithStructureMustBeValid() {
        ApplicationModules.of(BookstoreApplication.class).verify();
    }

    @Test
    void inboundAdaptersMustNotDependOnPersistence() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.honya.bookstore");

        assertNoDependencyFromPackagesToPackages(
                classes,
                List.of(".controller", ".web"),
                List.of(".repo", ".infrastructure.persistence")
        );
    }

    @Test
    void crossModuleAccessMustUseApiPackagesOnly() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.honya.bookstore");
        List<String> modules = List.of("article", "cart", "catalog", "discount", "order", "review", "ticket", "user");

        for (String sourceModule : modules) {
            for (String targetModule : modules) {
                if (!sourceModule.equals(targetModule)) {
                    assertNoInternalDependency(
                            classes,
                            "com.honya.bookstore." + sourceModule,
                            "com.honya.bookstore." + targetModule,
                            "com.honya.bookstore." + targetModule + ".api"
                    );
                }
            }
        }
        assertNoDependency(classes, "com.honya.bookstore.catalog", "com.honya.bookstore.order.outbox");
        assertNoDependency(classes, "com.honya.bookstore.cart", "com.honya.bookstore.order.outbox");
    }

    private void assertNoDependencyFromPackagesToPackages(JavaClasses classes, List<String> sourceMarkers, List<String> targetMarkers) {
        for (JavaClass source : classes) {
            if (sourceMarkers.stream().noneMatch(source.getPackageName()::contains)) {
                continue;
            }

            for (Dependency dependency : source.getDirectDependenciesFromSelf()) {
                String target = dependency.getTargetClass().getPackageName();
                assertFalse(targetMarkers.stream().anyMatch(target::contains),
                        source.getName() + " depends on persistence type " + dependency.getTargetClass().getName());
            }
        }
    }

    private void assertNoInternalDependency(JavaClasses classes, String sourcePackage, String targetPackage, String allowedTargetPackage) {
        for (JavaClass source : classes) {
            if (!source.getPackageName().startsWith(sourcePackage)) {
                continue;
            }

            for (Dependency dependency : source.getDirectDependenciesFromSelf()) {
                String target = dependency.getTargetClass().getName();
                assertFalse(target.startsWith(targetPackage) && !target.startsWith(allowedTargetPackage),
                        source.getName() + " depends on internal module type " + target);
            }
        }
    }

    private void assertNoDependency(JavaClasses classes, String sourcePackage, String targetPackage) {
        for (JavaClass source : classes) {
            if (!source.getPackageName().startsWith(sourcePackage)) {
                continue;
            }

            for (Dependency dependency : source.getDirectDependenciesFromSelf()) {
                String target = dependency.getTargetClass().getName();
                assertFalse(target.startsWith(targetPackage),
                        source.getName() + " depends on forbidden module type " + target);
            }
        }
    }
}
