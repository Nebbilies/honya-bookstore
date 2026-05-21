CREATE TABLE IF NOT EXISTS "order".order_items_books (
    id UUID PRIMARY KEY,
    price INTEGER,
    author VARCHAR(255),
    rating INTEGER,
    title VARCHAR(255)
);

INSERT INTO "order".order_items_books (id, price, author, rating, title)
SELECT DISTINCT
    item.book_id,
    book.price,
    book.author,
    ROUND(book.rating)::INTEGER,
    book.title
FROM "order".order_items item
LEFT JOIN catalog.books book ON book.id = item.book_id
WHERE item.book_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM "order".order_items_books snapshot
      WHERE snapshot.id = item.book_id
  );

ALTER TABLE "order".order_items
    ADD CONSTRAINT fk_order_items_book_snapshot
    FOREIGN KEY (book_id) REFERENCES "order".order_items_books(id);

ALTER TABLE "order".orders
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;