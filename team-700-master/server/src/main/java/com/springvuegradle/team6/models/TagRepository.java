package com.springvuegradle.team6.models;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface TagRepository extends JpaRepository<Tag, Integer> {

  List<Tag> findByNameContaining(String name);

  boolean existsByName(String name);

  Tag findByName(String name);

  List<Activity> findActivitiesByName(String name);

  @Query(
      "SELECT t.name from Tag t left join t.activities a where t.name like :search% group by (t.name) ORDER BY COUNT(t.name) DESC")
  List<String> findTagsMatchingSearch(@Param("search") String search, Pageable pageable);


}
