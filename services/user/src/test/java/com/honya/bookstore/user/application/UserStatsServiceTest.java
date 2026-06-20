package com.honya.bookstore.user.application;

import com.honya.bookstore.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserStatsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserStatsService service = new UserStatsService(userRepository);

    @Test
    void totalUsersReturnsRepositoryCount() {
        when(userRepository.count()).thenReturn(42L);

        assertEquals(42L, service.totalUsers());
    }
}
