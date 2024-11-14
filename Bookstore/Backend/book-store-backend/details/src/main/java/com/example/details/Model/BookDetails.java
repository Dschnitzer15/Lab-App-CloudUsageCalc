package com.example.details.Model;

public class BookDetails {
    private Long id;
    private String description;
    private Integer publicationYear;

    public BookDetails(Long id, String description, Integer publicationYear) {
        this.id = id;
        this.description = description;
        this.publicationYear = publicationYear;
    }

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public Integer getPublicationYear() { return publicationYear; }
}

