package com.honya.bookstore.checkout;

import com.honya.platform.resilience.HonyaResilience;
import com.honya.platform.security.HonyaResourceServerSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.honya.bookstore")
@EnableScheduling
@Import({HonyaResourceServerSecurity.class, HonyaResilience.class})
public class CheckoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckoutApplication.class, args);
    }
}
