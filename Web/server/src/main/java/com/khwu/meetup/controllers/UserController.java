package com.khwu.meetup.controllers;

import com.khwu.meetup.repositories.UserByNameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserByNameRepository userByNameRepository;

    @GetMapping("/name/{name:.+}")
    public ResponseEntity<List<Long>> getIdByName(@PathVariable("name") String name) {
        logger.info(String.format("Searching for user with name: %s", name));
        List<Long> res = userByNameRepository.findByName(name);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
