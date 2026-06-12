package com.honya.bookstore.user.api;

import com.honya.bookstore.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatsApiAdapter implements UserStatsApi {

    private final UserRepository userRepository;

    @Override
    public long totalUsers() {
        return userRepository.count();
    }
}
