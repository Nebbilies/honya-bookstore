package com.honya.bookstore.domain.entity;

import com.honya.bookstore.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "discounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Discount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private DiscountType type;
    private Double value;

    @Enumerated(EnumType.STRING)
    private DiscountCategory category;

    @Enumerated(EnumType.STRING)
    private DiscountOperator operator;

    @ElementCollection
    private List<String> conditionValue;
}