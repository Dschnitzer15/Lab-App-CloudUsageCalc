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
        detailsMap.put(3L, new BookDetails(3L, "Description of Book Three", 2010));
        detailsMap.put(4L, new BookDetails(4L, "Description of Book Four", 2012));
        detailsMap.put(5L, new BookDetails(5L, "Description of Book Five", 2015));
        detailsMap.put(6L, new BookDetails(6L, "Description of Book Six", 2018));
        detailsMap.put(7L, new BookDetails(7L, "Description of Book Seven", 2020));
        detailsMap.put(8L, new BookDetails(8L, "Description of Book Eight", 2021));
        detailsMap.put(9L, new BookDetails(9L, "Description of Book Nine", 2017));
        detailsMap.put(10L, new BookDetails(10L, "Description of Book Ten", 2016));
        detailsMap.put(11L, new BookDetails(11L, "Description of Book Eleven", 2019));
        detailsMap.put(12L, new BookDetails(12L, "Description of Book Twelve", 2024));
    }

    public Optional<BookDetails> getBookDetails(long id) {
        return Optional.ofNullable(detailsMap.get(id));
    }
}


