package com.honya.bookstore.service;

import com.honya.bookstore.domain.entity.User;
import java.util.List;

public interface UserService {
    User getUserById(String id);
    List<User> getAllUsers();
    void deleteUser(String id);
    // Note: Keycloak handles most user creation/auth, so we focus on reading/syncing here.
}