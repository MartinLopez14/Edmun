package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface ActivityHistoryRepository extends JpaRepository<ActivityHistory, Integer> {
  ActivityHistory findById(int id);

  Set<ActivityHistory> findByActivity_id(int id);

  @Query(
      value =
          "SELECT * FROM activity_history a WHERE a.activity_id = :id AND a.time_date > :startDateTime "
              + "AND (a.time_date < :endDateTime OR :endDateTime is NULL)",
      nativeQuery = true)
  List<ActivityHistory> getActivityHistoryBetweenSubscribeStartEndDateTimeAndActivityId(
      int id, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
