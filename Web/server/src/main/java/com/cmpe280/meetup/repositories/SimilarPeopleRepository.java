package com.cmpe280.meetup.repositories;

import com.cmpe280.meetup.models.SimilarPeople;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimilarPeopleRepository extends CrudRepository<SimilarPeople, Integer> {
    @Query("SELECT * FROM similar_people LIMIT 20")
    List<SimilarPeople> findAll();
}
