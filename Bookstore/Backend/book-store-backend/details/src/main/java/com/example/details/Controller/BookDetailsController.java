package com.example.details.Controller;

import com.example.details.Model.BookDetails;
import com.example.details.Service.DetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/details")
public class BookDetailsController {
    private final DetailsService detailsService;

    public BookDetailsController(DetailsService detailsService) {
        this.detailsService = detailsService;
    }

    @GetMapping("/{id}")
    public Optional<BookDetails> fetchBookDetails(@PathVariable long id) {
        return detailsService.getBookDetails(id);
    }
}

