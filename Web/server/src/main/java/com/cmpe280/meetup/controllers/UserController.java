package com.cmpe280.meetup.controllers;

import com.cmpe280.meetup.repositories.UserByNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserByNameRepository userByNameRepository;

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Long>> getIdByName(@PathVariable("name") String name) {
        List<Long> res = userByNameRepository.findByName(name);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
