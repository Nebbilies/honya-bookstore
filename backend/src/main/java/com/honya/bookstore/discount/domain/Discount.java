package com.honya.bookstore.discount.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "discounts", schema = "discount")
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
    @CollectionTable(name = "discount_condition_value", schema = "discount", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "condition_value")
    private List<String> conditionValue;
}