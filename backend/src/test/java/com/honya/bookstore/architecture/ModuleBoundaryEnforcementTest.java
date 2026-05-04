package com.honya.bookstore.architecture;

import com.honya.bookstore.BookstoreApplication;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ModuleBoundaryEnforcementTest {

    @Test
    void modulithStructureMustBeValid() {
        ApplicationModules.of(BookstoreApplication.class).verify();
    }

    @Test
    void controllersMustNotDependOnRepositories() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.honya.bookstore");

        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repo..");

        rule.check(classes);
    }

    @Test
    void crossModuleAccessMustUseApiPackagesOnly() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.honya.bookstore");

        assertNoInternalDependency(classes, "com.honya.bookstore.catalog", "com.honya.bookstore.order", "com.honya.bookstore.order.api");
        assertNoInternalDependency(classes, "com.honya.bookstore.cart", "com.honya.bookstore.order", "com.honya.bookstore.order.api");
        assertNoInternalDependency(classes, "com.honya.bookstore.order", "com.honya.bookstore.catalog", "com.honya.bookstore.catalog.api");
        assertNoInternalDependency(classes, "com.honya.bookstore.order", "com.honya.bookstore.cart", "com.honya.bookstore.cart.api");
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
}
