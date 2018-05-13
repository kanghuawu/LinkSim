package com.khwu.meetup.services;

import com.khwu.meetup.geo_finder.Geo;
import com.khwu.meetup.models.SimilarPeople;
import com.khwu.meetup.repositories.SimilarPeopleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.khwu.meetup.geo_finder.Geo.USA;

@Service
public class SimilarPeopleService {

    private static Logger logger = LoggerFactory.getLogger(SimilarPeopleService.class);

    @Autowired
    private SimilarPeopleRepository similarPeopleRepository;

    public List<SimilarPeople> findAll() {
        return similarPeopleRepository.findAll();
    }

    public List<SimilarPeople> findById(Long id, Float lat, Float lng) {
        List<SimilarPeople> res = new ArrayList<>();
        try {
            String country = Geo.getInstance().findLocation("WORLD", lng, lat);
            logger.info("Country: " + country);
            if (country.equals(USA)) {
                String state = Geo.getInstance().findLocation("USA", lng, lat);
                logger.info("State: " + state);
                res = similarPeopleRepository.findByIdAndCountryAndState(id, country, state);
            }
            if (country != null) {
                if (res.isEmpty()) {
                    res = similarPeopleRepository.findByIdAndCountry(id, country);
                }
                if (res.isEmpty()) {
                    res = similarPeopleRepository.findById(id);
                }
            }

            return res;
        } catch (IOException e) {}

        return similarPeopleRepository.findById(id);
    }
}
