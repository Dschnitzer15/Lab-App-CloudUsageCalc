package com.example.books.Repository;

import com.example.books.Model.Book;
import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final Set<Book> books = new HashSet<>();

    public BookRepositoryImpl() {
        books.add(new Book(1L, "Book One", "Author A"));
        books.add(new Book(2L, "Book Two", "Author B"));
        books.add(new Book(3L, "Book Three", "Author C"));
        books.add(new Book(4L, "Book Four", "Author D"));
        books.add(new Book(5L, "Book Five", "Author E"));
        books.add(new Book(6L, "Book Six", "Author F"));
        books.add(new Book(7L, "Book Seven", "Author G"));
        books.add(new Book(8L, "Book Eight", "Author H"));
        books.add(new Book(9L, "Book Nine", "Author I"));
        books.add(new Book(10L, "Book Ten", "Author J"));
        books.add(new Book(11L, "Book Eleven", "Author K"));
        books.add(new Book(12L, "Book Twelve", "Author L"));
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst();
    }

    @Override
    public Set<Book> findAll() {
        return books;
    }
}

