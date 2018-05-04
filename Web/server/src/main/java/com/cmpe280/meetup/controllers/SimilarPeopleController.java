package com.cmpe280.meetup.controllers;

import com.cmpe280.meetup.models.SimilarPeople;
import com.cmpe280.meetup.services.SimilarPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SimilarPeopleController {
    @Autowired
    private SimilarPeopleService similarPeopleService;

    @GetMapping("similar")
    public ResponseEntity<Iterable<SimilarPeople>> getAll() {
        return new ResponseEntity<>(similarPeopleService.findAll(), HttpStatus.OK);
    }
}
