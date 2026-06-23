package com.honya.bookstore.order.domain;

import com.honya.bookstore.order.domain.*; // Assuming you moved the enums into an 'enums' subfolder
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "\"order\"")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false)
public class Order extends AbstractAggregateRoot<Order> implements Persistable<UUID> {
    // app-assigned ID
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String paymentUrl;
    private String paymentTransactionNo;
    private OffsetDateTime paidAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private OrderProvider provider;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean isPaid;
    private Integer totalAmount;

    @Column(name = "user_id")
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // Drives Spring Data's persist-vs-merge choice instead of the "id == null" default.
    // App-assigned id is never null, so without this save() would merge() (SELECT-then-INSERT).
    // True until the row is first persisted or loaded → save() picks persist() → plain INSERT.
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Transient
    @Builder.Default
    private boolean placed = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    /**
     * Places this order: assigns identity and links its items. Does not emit
     * OrderPlaced — that happens on confirm(), once the order is committed
     * (immediately for COD, after payment for online providers).
     */
    public void place(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;

        if (this.items != null) {
            this.items.forEach(item -> item.setOrder(this));
        }
    }

    /**
     * Confirms this order and registers the OrderPlacedDomainEvent once.
     * Spring Data publishes registered events during repository.save(this);
     * an internal listener relays them to the outbox.
     */
    public void confirm() {
        if (this.placed) {
            return;
        }

        List<OrderPlacedDomainEvent.Line> lines = List.of();
        if (this.items != null) {
            lines = this.items.stream()
                    .map(item -> new OrderPlacedDomainEvent.Line(item.getBook().getId(), item.getQuantity()))
                    .toList();
        }

        registerEvent(new OrderPlacedDomainEvent(this.id, this.userId, lines));
        this.placed = true;
    }
}