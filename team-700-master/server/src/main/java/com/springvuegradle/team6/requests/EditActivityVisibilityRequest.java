package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class EditActivityVisibilityRequest {

  @NotNull
  @NotEmpty
  @JsonProperty("visibility")
  private String visibility;

  @JsonProperty("accessors")
  private List<String> emails;

  public String getVisibility() {
    return visibility;
  }

  public List<String> getEmails() {
    return emails;
  }
}
