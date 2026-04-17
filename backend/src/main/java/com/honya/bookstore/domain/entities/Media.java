package com.honya.bookstore.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "media")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String url;
    private String altText;
}