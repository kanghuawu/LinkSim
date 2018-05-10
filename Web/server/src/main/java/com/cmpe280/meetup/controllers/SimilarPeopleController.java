package com.cmpe280.meetup.controllers;

import com.cmpe280.meetup.geo_finder.Geo;
import com.cmpe280.meetup.models.SimilarPeople;
import com.cmpe280.meetup.services.SimilarPeopleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("similar")
public class SimilarPeopleController {

    private static Logger logger = LoggerFactory.getLogger(SimilarPeopleController.class);

    @Autowired
    private SimilarPeopleService similarPeopleService;

    @GetMapping("")
    public ResponseEntity<Iterable<SimilarPeople>> getAll() {
        logger.info("Searching all people");
        return new ResponseEntity<>(similarPeopleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}/{lat}/{lng}")
    public ResponseEntity<List<SimilarPeople>> getById(@PathVariable("id") Long id,
                                                       @PathVariable("lat") Float lat,
                                                       @PathVariable("lng") Float lng) {
        logger.info(String.format("Searching for %d, %f, %f", id, lat, lng));
        return new ResponseEntity<>(similarPeopleService.findById(id, lat, lng), HttpStatus.OK);
    }
}
