package com.honya.bookstore.user.infrastructure.persistence;

import com.honya.bookstore.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}