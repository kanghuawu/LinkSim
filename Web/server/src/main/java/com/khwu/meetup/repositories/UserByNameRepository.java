package com.khwu.meetup.repositories;

import com.khwu.meetup.models.UserByName;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserByNameRepository extends CrudRepository<UserByName, String> {
    @Query("SELECT id FROM user_by_name WHERE name = :name")
    List<Long> findByName(@Param("name")String name);
}
