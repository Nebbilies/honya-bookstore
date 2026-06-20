package com.honya.bookstore.user.web;

import com.honya.bookstore.security.StaffOrAdmin;
import com.honya.bookstore.user.application.UserStatsService;
import com.honya.bookstore.user.web.dto.UserStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User stats", description = "User metrics for the CMS dashboard")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    @Operation(summary = "Total users", description = "Total number of registered users")
    @StaffOrAdmin
    @GetMapping("/stats/total")
    public ResponseEntity<UserStatsResponse> totalUsers() {
        return ResponseEntity.ok(new UserStatsResponse(userStatsService.totalUsers()));
    }
}
