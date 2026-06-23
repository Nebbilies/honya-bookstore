package com.honya.bookstore.checkout.web.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
}
