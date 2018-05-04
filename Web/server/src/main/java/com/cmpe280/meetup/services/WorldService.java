package com.cmpe280.meetup.services;

import com.cmpe280.meetup.models.World;
import com.cmpe280.meetup.repositories.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Service
public class WorldService {

    @Autowired
    private WorldRepository worldRepository;

    public List<World> findByMDate(LocalDate date) {
        return this.worldRepository.findByMDate(date);
    }

    public List<World> findAll() {
        return this.worldRepository.findAll();
    }
}
