package com.honya.bookstore.contract;

import com.honya.bookstore.catalog.web.BookController;
import com.honya.bookstore.catalog.web.CategoryController;
import com.honya.bookstore.catalog.domain.Book;
import com.honya.bookstore.catalog.domain.Category;
import com.honya.bookstore.catalog.web.dto.request.BookRequestDTO;
import com.honya.bookstore.catalog.web.dto.request.CategoryRequestDTO;
import com.honya.bookstore.catalog.application.BookService;
import com.honya.bookstore.catalog.application.CategoryService;
import com.honya.bookstore.cart.domain.Cart;
import com.honya.bookstore.cart.web.CartController;
import com.honya.bookstore.cart.domain.CartItem;
import com.honya.bookstore.cart.application.CartService;
import com.honya.bookstore.cart.web.dto.request.AddItemRequestDTO;
import com.honya.bookstore.checkout.CheckoutController;
import com.honya.bookstore.checkout.CheckoutRequestDTO;
import com.honya.bookstore.checkout.CheckoutService;
import com.honya.bookstore.order.api.OrderItemResponse;
import com.honya.bookstore.order.api.OrderResponse;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.web.OrderController;
import com.honya.bookstore.order.domain.OrderItem;
import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.OrderProvider;
import com.honya.bookstore.order.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoreApiContractFreezeTest {


    private MockMvc mockMvc;
    private BookService bookService;
    private CategoryService categoryService;
    private CartService cartService;
    private OrderService orderService;
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        categoryService = mock(CategoryService.class);
        cartService = mock(CartService.class);
        orderService = mock(OrderService.class);
        checkoutService = mock(CheckoutService.class);

        BookController bookController = new BookController(bookService, categoryService);
        CategoryController categoryController = new CategoryController(categoryService);
        CartController cartController = new CartController(cartService);
        OrderController orderController = new OrderController(orderService);
        CheckoutController checkoutController = new CheckoutController(checkoutService);

        mockMvc = MockMvcBuilders.standaloneSetup(
                bookController,
                categoryController,
                cartController,
                orderController,
                checkoutController
        ).build();
    }

    @Test
    void getBooksReturns200AndExpectedFields() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("Fiction")
                .slug("fiction")
                .description("Fiction books")
                .build();

        UUID bookId = UUID.randomUUID();
        Book book = Book.builder()
                .id(bookId)
                .title("Book A")
                .description("Desc")
                .author("Author")
                .price(100)
                .pagesCount(200)
                .yearPublished(2020)
                .publisher("Pub")
                .weight(1.5f)
                .stockQuantity(10)
                .purchaseCount(5)
                .rating(4.5f)
                .categories(List.of(category))
                .build();

        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").value("Book A"))
                .andExpect(jsonPath("$[0].description").value("Desc"))
                .andExpect(jsonPath("$[0].author").value("Author"))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[0].pagesCount").value(200))
                .andExpect(jsonPath("$[0].yearPublished").value(2020))
                .andExpect(jsonPath("$[0].publisher").value("Pub"))
                .andExpect(jsonPath("$[0].weight").value(1.5))
                .andExpect(jsonPath("$[0].stockQuantity").value(10))
                .andExpect(jsonPath("$[0].purchaseCount").value(5))
                .andExpect(jsonPath("$[0].rating").value(4.5))
                .andExpect(jsonPath("$[0].categories[0].id").exists())
                .andExpect(jsonPath("$[0].categories[0].name").value("Fiction"))
                .andExpect(jsonPath("$[0].categories[0].slug").value("fiction"))
                .andExpect(jsonPath("$[0].categories[0].description").value("Fiction books"));
    }

    @Test
    void getBookByIdInvalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/books/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookReturns201AndExpectedFields() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("Fiction")
                .slug("fiction")
                .description("Fiction books")
                .build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        Book saved = Book.builder()
                .id(UUID.randomUUID())
                .title("New")
                .description("New Desc")
                .author("Writer")
                .price(150)
                .pagesCount(300)
                .yearPublished(2021)
                .publisher("Pub2")
                .weight(1.1f)
                .stockQuantity(20)
                .purchaseCount(0)
                .rating(0.0f)
                .categories(List.of(category))
                .build();

        when(bookService.createBook(any(Book.class))).thenReturn(saved);

        BookRequestDTO request = BookRequestDTO.builder()
                .title("New")
                .description("New Desc")
                .author("Writer")
                .price(150)
                .pagesCount(300)
                .yearPublished(2021)
                .publisher("Pub2")
                .weight(1.1f)
                .stockQuantity(20)
                .categoryIds(List.of(categoryId))
                .build();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.categories[0].id").exists());
    }

    @Test
    void updateBookReturns200() throws Exception {
        UUID categoryId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Category category = Category.builder()
                .id(categoryId)
                .name("Updated Cat")
                .slug("updated-cat")
                .description("Updated")
                .build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        Book updated = Book.builder()
                .id(bookId)
                .title("Updated")
                .description("Updated Desc")
                .author("A")
                .price(99)
                .pagesCount(101)
                .yearPublished(2022)
                .publisher("P")
                .weight(0.7f)
                .stockQuantity(9)
                .purchaseCount(2)
                .rating(3.0f)
                .categories(List.of(category))
                .build();

        when(bookService.updateBook(eq(bookId), any(Book.class))).thenReturn(updated);

        BookRequestDTO request = BookRequestDTO.builder()
                .title("Updated")
                .description("Updated Desc")
                .author("A")
                .price(99)
                .pagesCount(101)
                .yearPublished(2022)
                .publisher("P")
                .weight(0.7f)
                .stockQuantity(9)
                .categoryIds(List.of(categoryId))
                .build();

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId.toString()));
    }

    @Test
    void deleteBookReturns204() throws Exception {
        UUID bookId = UUID.randomUUID();
        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCategoriesReturns200AndExpectedFields() throws Exception {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Cat")
                .slug("cat")
                .description("Cat Desc")
                .build();

        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Cat"))
                .andExpect(jsonPath("$[0].slug").value("cat"))
                .andExpect(jsonPath("$[0].description").value("Cat Desc"));
    }

    @Test
    void getCategoryByIdInvalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/categories/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategoryReturns201AndExpectedFields() throws Exception {
        Category saved = Category.builder()
                .id(UUID.randomUUID())
                .name("New Cat")
                .slug("new-cat")
                .description("New Cat Desc")
                .build();

        when(categoryService.createCategory(any(Category.class))).thenReturn(saved);

        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("New Cat")
                .slug("new-cat")
                .description("New Cat Desc")
                .build();

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Cat"))
                .andExpect(jsonPath("$.slug").value("new-cat"))
                .andExpect(jsonPath("$.description").value("New Cat Desc"));
    }

    @Test
    void updateCategoryReturns200() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Category updated = Category.builder()
                .id(categoryId)
                .name("Updated Cat")
                .slug("updated-cat")
                .description("Updated Desc")
                .build();

        when(categoryService.updateCategory(eq(categoryId), any(Category.class))).thenReturn(updated);

        CategoryRequestDTO request = CategoryRequestDTO.builder()
                .name("Updated Cat")
                .slug("updated-cat")
                .description("Updated Desc")
                .build();

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId.toString()));
    }

    @Test
    void deleteCategoryReturns204() throws Exception {
        UUID categoryId = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCartWithHeaderReturns200AndExpectedFields() throws Exception {
        String userId = UUID.randomUUID().toString();
        Cart cart = sampleCart(UUID.fromString(userId));

        when(cartService.getCartByUserId(userId)).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ownerId").value(userId))
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.items[0].id").exists())
                .andExpect(jsonPath("$.items[0].bookId").exists())
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void getCartWithoutHeaderReturns400() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCartItemWithHeaderReturns200() throws Exception {
        String userId = UUID.randomUUID().toString();
        UUID bookId = UUID.randomUUID();
        Cart cart = sampleCart(UUID.fromString(userId));

        when(cartService.addItemToCart(eq(userId), eq(bookId), eq(2))).thenReturn(cart);

        AddItemRequestDTO request = new AddItemRequestDTO();
        request.setBookId(bookId);
        request.setQuantity(2);

        mockMvc.perform(post("/api/cart/items")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void addCartItemWithoutHeaderReturns400() throws Exception {
        AddItemRequestDTO request = new AddItemRequestDTO();
        request.setBookId(UUID.randomUUID());
        request.setQuantity(2);

        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeCartItemWithHeaderReturns200() throws Exception {
        String userId = UUID.randomUUID().toString();
        UUID itemId = UUID.randomUUID();
        Cart cart = sampleCart(UUID.fromString(userId));

        when(cartService.removeItemFromCart(userId, itemId)).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/items/{itemId}", itemId)
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void removeCartItemWithoutHeaderReturns400() throws Exception {
        mockMvc.perform(delete("/api/cart/items/{itemId}", UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeCartItemInvalidUuidReturns400() throws Exception {
        mockMvc.perform(delete("/api/cart/items/not-a-uuid")
                        .header("X-User-Id", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearCartWithUuidHeaderReturns204() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(cartService).clearCart(userId);

        mockMvc.perform(delete("/api/cart")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void clearCartWithoutHeaderReturns400() throws Exception {
        mockMvc.perform(delete("/api/cart"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearCartWithInvalidUuidHeaderReturns400() throws Exception {
        mockMvc.perform(delete("/api/cart")
                        .header("X-User-Id", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersWithHeaderReturns200AndExpectedFields() throws Exception {
        String userId = UUID.randomUUID().toString();
        Order order = sampleOrder(UUID.fromString(userId));

        when(orderService.getOrdersByUserId(userId)).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].address").value("Street 1"))
                .andExpect(jsonPath("$[0].city").value("City"))
                .andExpect(jsonPath("$[0].provider").value("COD"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].isPaid").value(false))
                .andExpect(jsonPath("$[0].totalAmount").value(1234))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].items[0].id").exists())
                .andExpect(jsonPath("$[0].items[0].bookId").exists())
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].price").value(617));
    }

    @Test
    void getOrdersWithoutHeaderReturns400() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderByIdReturns200() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = sampleOrder(UUID.randomUUID());
        order.setId(orderId);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void getOrderByIdInvalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/orders/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutWithHeaderReturns200AndExpectedFields() throws Exception {
        String userId = UUID.randomUUID().toString();
        OrderResponse created = sampleOrderResponse(UUID.fromString(userId));

        when(checkoutService.checkout(eq(userId), any(CheckoutRequestDTO.class))).thenReturn(created);

        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("Street 1");
        request.setCity("City");

        mockMvc.perform(post("/api/orders/checkout")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("Street 1"))
                .andExpect(jsonPath("$.city").value("City"))
                .andExpect(jsonPath("$.provider").value("COD"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.isPaid").value(false))
                .andExpect(jsonPath("$.totalAmount").value(1234))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void checkoutWithoutHeaderReturns400() throws Exception {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("Street 1");
        request.setCity("City");

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest());
    }

    private Cart sampleCart(UUID userId) {
        CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .bookId(UUID.randomUUID())
                .quantity(2)
                .build();

        return Cart.builder()
                .id(UUID.randomUUID())
                .ownerId(userId)
                .updatedAt(OffsetDateTime.parse("2026-05-04T10:15:30Z"))
                .items(List.of(item))
                .build();
    }

    private String toJson(Object source) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = source.getClass().getDeclaredFields();
        int written = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            if (written > 0) {
                sb.append(",");
            }
            sb.append("\"").append(field.getName()).append("\":");
            if (value == null) {
                sb.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else if (value instanceof List<?> list) {
                sb.append("[");
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item == null) {
                        sb.append("null");
                    } else if (item instanceof Number || item instanceof Boolean) {
                        sb.append(item);
                    } else {
                        sb.append("\"").append(item.toString().replace("\"", "\\\"")).append("\"");
                    }
                    if (i < list.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("]");
            } else {
                sb.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            }
            written++;
        }
        sb.append("}");
        return sb.toString();
    }

    private OrderResponse sampleOrderResponse(UUID userId) {
        return new OrderResponse(
                UUID.randomUUID(),
                "John",
                "Doe",
                "Street 1",
                "City",
                "COD",
                "PENDING",
                false,
                1234,
                userId,
                List.of(new OrderItemResponse(UUID.randomUUID(), UUID.randomUUID(), 2, 617))
        );
    }

    private Order sampleOrder(UUID userId) {
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .bookId(UUID.randomUUID())
                .quantity(2)
                .price(617)
                .build();

        return Order.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .address("Street 1")
                .city("City")
                .provider(OrderProvider.COD)
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .totalAmount(1234)
                .userId(userId)
                .items(List.of(item))
                .build();
    }
}
