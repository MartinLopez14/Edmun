package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EditSubscriptionRequest {

  @JsonProperty("subscriber")
  private SubscriptionRequest subscription;

  public SubscriptionRequest getSubscription() {
    return subscription;
  }
}
