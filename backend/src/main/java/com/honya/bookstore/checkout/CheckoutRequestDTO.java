package com.honya.bookstore.checkout;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
}
