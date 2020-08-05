package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LocationUpdateRequest {
  @NotNull(message = "city cannot be null")
  @NotEmpty(message = "city cannot be empty")
  public String city;

  public String state;

  @NotNull(message = "country cannot be null")
  @NotEmpty(message = "country cannot be empty")
  public String country;
}
