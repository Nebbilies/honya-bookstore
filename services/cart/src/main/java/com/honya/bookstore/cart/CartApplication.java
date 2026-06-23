package com.honya.bookstore.cart;

import com.honya.platform.resilience.HonyaResilience;
import com.honya.platform.security.HonyaResourceServerSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "com.honya.bookstore")
@Import({HonyaResourceServerSecurity.class, HonyaResilience.class})
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
