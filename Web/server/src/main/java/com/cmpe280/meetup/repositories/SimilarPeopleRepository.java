package com.cmpe280.meetup.repositories;

import com.cmpe280.meetup.models.SimilarPeople;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimilarPeopleRepository extends CrudRepository<SimilarPeople, Integer> {
    @Query("SELECT * FROM similar_people LIMIT 10")
    List<SimilarPeople> findAll();

    @Query("SELECT * FROM similar_people WHERE id_a = :id LIMIT 10")
    List<SimilarPeople> findById(@Param("id")Long id);

    @Query("SELECT * FROM similar_people WHERE id_a = :id AND country_b = :country LIMIT 10")
    List<SimilarPeople> findByIdAndCountry(@Param("id")Long id, @Param("country")String country);

    @Query("SELECT * FROM similar_people WHERE id_a = :id AND country_b = :country AND state_b = :state LIMIT 10")
    List<SimilarPeople> findByIdAndCountryAndState(@Param("id")Long id, @Param("country")String country, @Param("state")String state);
}
