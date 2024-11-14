package com.example.reviews.Model;

public class Review {
    private Long id;
    private Long bookId;
    private String reviewText;
    private int rating;

    public Review(Long id, Long bookId, String reviewText, int rating) {
        this.id = id;
        this.bookId = bookId;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public String getReviewText() { return reviewText; }
    public int getRating() { return rating; }
}
