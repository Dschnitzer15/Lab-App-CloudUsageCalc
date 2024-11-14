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

