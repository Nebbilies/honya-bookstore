package com.honya.bookstore.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PackageNormalizationTest {

    @Test
    void userTypeMustLiveUnderUserFeaturePackage() throws Exception {
        Class<?> userClass = Class.forName("com.honya.bookstore.user.User");
        assertEquals("com.honya.bookstore.user", userClass.getPackageName());
    }

    @Test
    void legacyUserPackageMustNotExist() {
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.honya.bookstore.domain.entity.User"));
    }
}
