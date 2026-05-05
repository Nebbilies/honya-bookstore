package com.honya.bookstore.user.application;

import com.honya.bookstore.user.domain.User;
import java.util.List;

public interface UserService {
    User getUserById(String id);
    List<User> getAllUsers();
    void deleteUser(String id);
    // Note: Keycloak handles most user creation/auth, so we focus on reading/syncing here.
}