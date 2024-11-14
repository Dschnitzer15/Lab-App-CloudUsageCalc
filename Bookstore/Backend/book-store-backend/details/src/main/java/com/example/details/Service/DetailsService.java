package com.example.details.Service;

import com.example.details.Model.BookDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DetailsService {
    private final Map<Long, BookDetails> detailsMap = new HashMap<>();

    public DetailsService() {
        detailsMap.put(1L, new BookDetails(1L, "Description of Book One", 1999));
        detailsMap.put(2L, new BookDetails(2L, "Description of Book Two", 2005));
    }

    public Optional<BookDetails> getBookDetails(long id) {
        return Optional.ofNullable(detailsMap.get(id));
    }
}


