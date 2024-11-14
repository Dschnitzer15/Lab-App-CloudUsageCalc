package com.example.reviews.Service;

import com.example.reviews.Model.Review;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    private final List<Review> reviews = new ArrayList<>();

    public ReviewService() {
        reviews.add(new Review(1L, 1L, "Great book!", 5));
        reviews.add(new Review(2L, 1L, "Interesting read", 4));
        reviews.add(new Review(3L, 2L, "Not my type", 2));
    }

    public List<Review> getReviewsForBook(long bookId) {
        List<Review> result = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getBookId().equals(bookId)) {
                result.add(review);
            }
        }
        return result;
    }
}
