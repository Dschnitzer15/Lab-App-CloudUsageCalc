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
        reviews.add(new Review(4L, 2L, "Could be better", 3));
        reviews.add(new Review(5L, 3L, "A masterpiece!", 5));
        reviews.add(new Review(6L, 3L, "Quite engaging", 4));
        reviews.add(new Review(7L, 4L, "Not bad", 3));
        reviews.add(new Review(8L, 4L, "I loved it!", 5));
        reviews.add(new Review(9L, 5L, "Very informative", 4));
        reviews.add(new Review(10L, 5L, "Could be more concise", 3));
        reviews.add(new Review(11L, 6L, "A thrilling story", 5));
        reviews.add(new Review(12L, 7L, "Good, but a bit long", 4));
        reviews.add(new Review(13L, 8L, "An exciting read", 5));
        reviews.add(new Review(14L, 9L, "Very dry, didn't enjoy", 2));
        reviews.add(new Review(15L, 9L, "Good insights", 4));
        reviews.add(new Review(16L, 10L, "Interesting characters", 4));
        reviews.add(new Review(17L, 11L, "Very emotional", 5));
        reviews.add(new Review(18L, 12L, "A great conclusion", 5));
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
