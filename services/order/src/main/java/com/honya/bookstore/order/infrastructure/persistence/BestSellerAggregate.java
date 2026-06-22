package com.honya.bookstore.order.infrastructure.persistence;

public interface BestSellerAggregate {
    String getTitle();
    String getAuthor();
    Long getTotalSold();
}
