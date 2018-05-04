package com.cmpe280.meetup.repositories;

import com.cmpe280.meetup.models.World;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorldRepository extends CrudRepository<World, LocalDate> {

    @Query("SELECT * FROM world_stat_by_date WHERE mdate =?0")
    List<World> findByMDate(LocalDate date);

    @Query("SELECT * FROM world_stat_by_date")
    List<World> findAll();
}
