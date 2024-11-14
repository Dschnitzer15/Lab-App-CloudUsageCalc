package com.example.books.Service;

import com.example.books.Model.BookDto;
import com.example.books.Repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<BookDto> getBook(long id) {
        return bookRepository.findBookById(id)
                .map(book -> new BookDto(book.getId(), book.getTitle(), book.getAuthor()));
    }

    public Set<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookDto(book.getId(), book.getTitle(), book.getAuthor()))
                .collect(Collectors.toSet());
    }

    public String getReviews(long id) {
        return "Reviews for book with ID: " + id;
    }
}



