package com.honya.bookstore.user.application;

import com.honya.bookstore.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final UserRepository userRepository;

    public long totalUsers() {
        return userRepository.count();
    }
}
