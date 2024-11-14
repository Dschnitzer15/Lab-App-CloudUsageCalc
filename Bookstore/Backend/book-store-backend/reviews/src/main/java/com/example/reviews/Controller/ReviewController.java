package com.example.reviews.Controller;

import com.example.reviews.Model.Review;
import com.example.reviews.Service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{bookId}")
    public List<Review> fetchReviews(@PathVariable long bookId) {
        return reviewService.getReviewsForBook(bookId);
    }
}

