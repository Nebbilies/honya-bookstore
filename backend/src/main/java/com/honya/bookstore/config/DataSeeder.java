package com.honya.bookstore.config;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.repo.BookRepository;
import com.honya.bookstore.catalog.repo.CategoryRepository;
import com.honya.bookstore.cart.CartService; // MODULITH FIX: Importing the public interface
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CartService cartService; // Injecting the Cart module!

    @Override
    public void run(String... args) throws Exception {
        // Only run this if the database is empty!
        if (categoryRepository.count() == 0 && bookRepository.count() == 0) {
            System.out.println("🌱 Database is empty! Seeding dummy data...");

            // 1. Create Dummy Categories
            Category manga = Category.builder()
                    .name("Manga")
                    .slug("manga")
                    .description("Japanese comic books and graphic novels.")
                    .build();

            Category sciFi = Category.builder()
                    .name("Science Fiction")
                    .slug("science-fiction")
                    .description("Futuristic concepts, advanced science, and space exploration.")
                    .build();

            // Save and capture the generated Category IDs
            List<Category> savedCategories = categoryRepository.saveAll(List.of(manga, sciFi));
            Category savedManga = savedCategories.get(0);
            Category savedSciFi = savedCategories.get(1);

            // 2. Create Dummy Books
            Book naruto = Book.builder()
                    .title("Naruto")
                    .description("The story of a young ninja who seeks recognition from his peers and dreams of becoming the Hokage.")
                    .author("Masashi Kishimoto")
                    .price(10)
                    .pagesCount(192)
                    .yearPublished(1999)
                    .publisher("Shueisha")
                    .weight(0.2f)
                    .stockQuantity(100)
                    .purchaseCount(0)
                    .rating(4.8f)
                    .categories(List.of(savedManga)) // Link to saved category
                    .build();

            Book dune = Book.builder()
                    .title("Dune")
                    .description("Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides.")
                    .author("Frank Herbert")
                    .price(15)
                    .pagesCount(412)
                    .yearPublished(1965)
                    .publisher("Chilton Books")
                    .weight(0.5f)
                    .stockQuantity(50)
                    .purchaseCount(0)
                    .rating(4.9f)
                    .categories(List.of(savedSciFi)) // Link to saved category
                    .build();

            // Save and capture the generated Book IDs
            List<Book> savedBooks = bookRepository.saveAll(List.of(naruto, dune));
            Book savedNaruto = savedBooks.get(0);
            Book savedDune = savedBooks.get(1);

            System.out.println("✅ Dummy Catalog data successfully seeded into Supabase!");

            // 3. Create a Dummy Cart
            System.out.println("🛒 Seeding a dummy cart...");

            // Generate a random ID to act as our "logged in" user for testing
            String dummyUserId = UUID.randomUUID().toString();

            // Add 2 copies of Naruto and 1 copy of Dune to this user's cart
            cartService.addItemToCart(dummyUserId, savedNaruto.getId(), 2);
            cartService.addItemToCart(dummyUserId, savedDune.getId(), 1);

            System.out.println("✅ Dummy cart successfully created!");
            System.out.println("=======================================================");
            System.out.println("🔥 IMPORTANT: Use this User ID to check your cart in Postman:");
            System.out.println("   USER ID: " + dummyUserId);
            System.out.println("=======================================================");

        } else {
            System.out.println("⏩ Database already contains data. Skipping seeder.");
        }
    }
}