package com.example.books.Controller;

import com.example.books.Model.BookDto;
import com.example.books.Service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public Set<BookDto> listBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/details/{id}")
    public Optional<BookDto> fetchBook(@PathVariable long id) {
        return bookService.getBook(id);
    }

    @GetMapping("/reviews/{id}")
    public String fetchBookReviews(@PathVariable long id) {
        return bookService.getReviews(id);
    }
}
