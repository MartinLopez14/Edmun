package com.springvuegradle.team6.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.ActivityType;

import java.util.Set;

public class SearchProfileResponse {

  @JsonProperty("profile_id")
  public Integer profileId;

  @JsonProperty("lastname")
  public String lastname;

  @JsonProperty("firstname")
  public String firstname;

  @JsonProperty("middlename")
  public String middlename;

  @JsonProperty("nickname")
  public String nickname;

  @JsonProperty("primary_email")
  public String primaryEmail;

  @JsonProperty("activity_types")
  public String activityTypes;

  public SearchProfileResponse(
          Integer profileId,
          String lastname,
          String firstname,
          String middlename,
          String nickname,
          String primaryEmail,
          String activityTypes) {
    this.profileId = profileId;
    this.lastname = lastname;
    this.firstname = firstname;
    this.middlename = middlename;
    this.nickname = nickname;
    this.primaryEmail = primaryEmail;
    this.activityTypes = activityTypes.substring(1, activityTypes.length() - 1);
  }
}
