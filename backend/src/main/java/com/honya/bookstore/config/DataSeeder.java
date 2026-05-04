package com.honya.bookstore.config;

import com.honya.bookstore.cart.api.CartApi;
import com.honya.bookstore.catalog.api.CatalogSeedApi;
import com.honya.bookstore.catalog.api.CatalogSeedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CatalogSeedApi catalogSeedApi;
    private final CartApi cartApi;

    @Override
    public void run(String... args) {
        CatalogSeedResult result = catalogSeedApi.seedDefaultCatalogIfEmpty();

        if (!result.seeded()) {
            System.out.println("Database already contains data. Skipping seed data setup.");
            return;
        }

        System.out.println("Default catalog data seeded.");
        System.out.println("Creating a sample cart...");

        String dummyUserId = UUID.randomUUID().toString();

        cartApi.addItemToCart(dummyUserId, result.narutoBookId(), 2);
        cartApi.addItemToCart(dummyUserId, result.duneBookId(), 1);

        System.out.println("Sample cart created.");
        System.out.println("Sample cart user ID: " + dummyUserId);
    }
}
