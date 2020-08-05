package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.ProfileRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
public class ActivitySharingFeatureSteps {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private MockMvc mvc;

  @Autowired
  private LoginSteps loginSteps;

  private ResultActions mvcResponse;

  private String jsonString;

  private String activityId;

  @Given("There is an activity {string} with {string} access")
  public void there_is_an_activity_with_access(String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"activity_name\": \""
                    + activityName
                    + "\",\n"
                    + "  \"description\": \"tramping iz fun\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Hike\",\n"
                    + "    \"Bike\"\n"
                    + "  ],\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\",\n"
                    + "  \"continuous\": true"
                    + "}";
    String createActivityUrl = "/profiles/" + loginSteps.profileId + "/activities";
    activityId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createActivityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }

  @Given("user {string} creates activity {string} with {string} access")
  public void user_creates_activity_with_access(String email, String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"activity_name\": \""
                    + activityName
                    + "\",\n"
                    + "  \"description\": \"tramping iz fun\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Hike\",\n"
                    + "    \"Bike\"\n"
                    + "  ],\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\",\n"
                    + "  \"continuous\": true"
                    + "}";
    String createActivityUrl = "/profiles/" + loginSteps.profileId + "/activities";
    activityId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createActivityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }

  @When("user {string} sets activity {string} to {string} access")
  public void user_sets_activity_to_access(String email, String activityName, String visibility) throws Exception {
    jsonString =
            "{\n"
                    + "  \"visibility\": \""
                    + visibility
                    + "\""
                    + "}";
    String changeVisibilityUrl = "/profiles/" + loginSteps.profileId + "/activities/" + activityId + "/visibility";
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.put(changeVisibilityUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
  }

  @When("user {string} views {string}")
  public void user_views(String email, String activityName) throws Exception {
    String password = "Johnnypwd1";
    jsonString =
            "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \""
                    + email
                    + "\",\r\n  \"password\": \""
                    + password
                    + "\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
    String createProfileUrl = "/profiles";
    String profileId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createProfileUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

    MockHttpSession session = new MockHttpSession();
    jsonString =
            "{\n"
                    + "\t\"email\": \""
                    + email
                    + "\",\n"
                    + "\t\"password\": \""
                    + password
                    + "\"\n"
                    + "}";
    String loginUrl = "/login";
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.post(loginUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));

    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get("/activities/" + activityId)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
  }

  @Then("I have response status {string} on activity {string}")
  public void i_have_response_status_on_activity(String statusCode, String activityName) throws Exception {
    mvcResponse.andExpect(status().is(Integer.parseInt(statusCode)));
  }
}
