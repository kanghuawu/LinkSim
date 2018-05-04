package com.cmpe280.meetup.controllers;

import com.cmpe280.meetup.models.World;
import com.cmpe280.meetup.services.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class WorldController {

    @Autowired
    private WorldService worldService;

    @GetMapping("/world")
    public ResponseEntity<Iterable<World>> getAll() {
        Iterable<World> res = worldService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
