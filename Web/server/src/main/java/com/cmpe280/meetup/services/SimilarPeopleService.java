package com.cmpe280.meetup.services;

import com.cmpe280.meetup.models.SimilarPeople;
import com.cmpe280.meetup.repositories.SimilarPeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarPeopleService {
    @Autowired
    private SimilarPeopleRepository similarPeopleRepository;

    public List<SimilarPeople> findAll() {
        return similarPeopleRepository.findAll();
    }

    public List<SimilarPeople> findById(Long id) {
        return similarPeopleRepository.findById(id);
    }
}
