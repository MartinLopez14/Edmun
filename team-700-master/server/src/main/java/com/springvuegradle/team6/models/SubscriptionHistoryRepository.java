package com.springvuegradle.team6.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

  SubscriptionHistory findById(int id);

  Set<SubscriptionHistory> findByActivity_id(int activityId);

  Set<SubscriptionHistory> findByProfile_id(int profileId);

  @Query(
          value =
                  "SELECT * from subscription_history where activity_id = :activityId " +
                          "and profile_id = :profileId and end_date_time is null",
          nativeQuery = true)
  List<SubscriptionHistory> findActive(int activityId, int profileId);
}
