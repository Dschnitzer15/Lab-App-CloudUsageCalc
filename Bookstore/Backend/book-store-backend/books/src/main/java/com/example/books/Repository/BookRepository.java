package com.example.books.Repository;

import com.example.books.Model.Book;
import java.util.Optional;
import java.util.Set;

public interface BookRepository {
    Optional<Book> findBookById(Long id);

    // Hinzufügen der findAll Methode
    Set<Book> findAll();
}



