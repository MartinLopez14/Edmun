package com.springvuegradle.team6.models;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.springvuegradle.team6.models.ActivityRoleType;

import java.util.List;

@RepositoryRestResource
public interface ActivityRoleRepository extends JpaRepository<ActivityRole, Integer> {

  List<ActivityRole> findByActivityRoleType(ActivityRoleType type);


  ActivityRole findByProfile_IdAndActivity_Id(int profileId, int activityId);

  List<ActivityRole> findByActivity_Id(int activityId);
  List<Profile> findByActivity_IdAndActivityRoleType(int activityId, ActivityRoleType type);
  @Query(
          value =
                  "select CONCAT(firstname, ' ' , lastname) as full_name, profile_id from activity_role JOIN profile on " +
                          "activity_role.profile_id = profile.id where activity_id = :activityId and activity_role_type = :type " +
                          "LIMIT :limit OFFSET :offset",
          nativeQuery = true)
  List<JSONObject> findMembers(int activityId, int type, int limit, int offset);

  @Query(
          value =
                  "select profile_id from activity_role JOIN profile on activity_role.profile_id = profile.id where activity_id = :activityId and activity_role_type = :type",
          nativeQuery = true)
  List<Integer> findRestrictedEmails(int activityId, int type);

  @Query(
          value =
                  "select COUNT(*) as Count from activity_role where activity_id = :activityId and activity_role_type = :type",
          nativeQuery = true)
  int findMembersCount(int activityId, int type);
}
