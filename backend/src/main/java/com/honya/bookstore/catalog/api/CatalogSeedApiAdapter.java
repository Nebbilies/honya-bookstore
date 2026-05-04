package com.honya.bookstore.catalog.api;

import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.repo.BookRepository;
import com.honya.bookstore.catalog.repo.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CatalogSeedApiAdapter implements CatalogSeedApi {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    public CatalogSeedResult seedDefaultCatalogIfEmpty() {
        if (categoryRepository.count() != 0 || bookRepository.count() != 0) {
            return CatalogSeedResult.skipped();
        }

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

        List<Category> savedCategories = categoryRepository.saveAll(List.of(manga, sciFi));
        Category savedManga = savedCategories.get(0);
        Category savedSciFi = savedCategories.get(1);

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
                .categories(List.of(savedManga))
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
                .categories(List.of(savedSciFi))
                .build();

        List<Book> savedBooks = bookRepository.saveAll(List.of(naruto, dune));
        return CatalogSeedResult.seeded(savedBooks.get(0).getId(), savedBooks.get(1).getId());
    }
}
