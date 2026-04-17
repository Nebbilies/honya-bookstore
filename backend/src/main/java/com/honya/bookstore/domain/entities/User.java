package com.honya.bookstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id; // Maps to Keycloak's UUID
}