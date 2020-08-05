package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
  /**
   * Find activity by activity id
   *
   * @param id activity id
   * @return object of activity class
   */
  Optional<Activity> findById(int id);

  List<Activity> findByProfile_IdAndArchivedFalse(int id);

  List<Activity> findByTags_NameOrderByCreationDateDesc(String name);

  List<Activity> findAll();

  Activity findByActivityName(String activityName);

  @Query(value = "select t from Activity a left join a.tags t WHERE a.id = :activityId")
  Set<Tag> getActivityTags(int activityId);
}
