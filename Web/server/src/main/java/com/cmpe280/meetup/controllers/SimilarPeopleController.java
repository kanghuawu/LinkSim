package com.cmpe280.meetup.controllers;

import com.cmpe280.meetup.models.SimilarPeople;
import com.cmpe280.meetup.services.SimilarPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("similar")
public class SimilarPeopleController {
    @Autowired
    private SimilarPeopleService similarPeopleService;

    @GetMapping("")
    public ResponseEntity<Iterable<SimilarPeople>> getAll() {
        return new ResponseEntity<>(similarPeopleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id1}")
    public ResponseEntity<SimilarPeople> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(similarPeopleService.findById(id), HttpStatus.OK);
    }
}
