package com.honya.bookstore.order.web.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
}