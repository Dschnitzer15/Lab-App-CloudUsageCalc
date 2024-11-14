package com.example.books.Model;

public class BookDto {
    private Long id;
    private String title;
    private String author;

    public BookDto(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}

