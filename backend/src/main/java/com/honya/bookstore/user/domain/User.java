package com.honya.bookstore.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "\"user\"")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id; // Maps to Keycloak's UUID
}