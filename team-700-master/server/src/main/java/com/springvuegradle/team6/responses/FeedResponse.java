package com.springvuegradle.team6.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedResponse {

    public FeedResponse(Integer activityId, String message, String dateTime) {
        this.activityId = activityId;
        this.message = message;
        this.dateTime = dateTime;
    }

    @JsonProperty("activity_id")
    public Integer activityId;

    @JsonProperty("message")
    public String message;

    @JsonProperty("date_time")
    public String dateTime;
}
