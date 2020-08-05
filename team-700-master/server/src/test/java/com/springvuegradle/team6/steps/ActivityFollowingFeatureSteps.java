package com.springvuegradle.team6.steps;

import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityRepository;
import com.springvuegradle.team6.models.ProfileRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
@DirtiesContext
public class ActivityFollowingFeatureSteps {

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LoginSteps loginSteps;

  private ResultActions mvcResponse;

  private String jsonString;

  private String activityId;

  @Given("I create an activity {string}")
  public void i_create_an_activity(String activityName) throws Exception {
    String jsonString;
    jsonString = "{\n" +
            "  \"activity_name\": \"" + activityName + "\",\n" +
            "  \"description\": \"tramping iz fun\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Hike\",\n" +
            "    \"Bike\"\n" +
            "  ],\n" +
            "  \"continuous\": true\n" +
            "}";
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

  @Then("User with email {string} home feed shows")
  public void user_with_email_home_feed_shows(
      String emailStr, io.cucumber.datatable.DataTable dataTable) throws Exception {
    List<String> activityNames = new ArrayList<>();
    for (Map<String, String> activityMapping : dataTable.asMaps()) {
      String activityName = activityMapping.get("Activity Name");
      activityNames.add(activityName);
    }

    String url = "/feed/homefeed/" + loginSteps.profileId;
    mvcResponse =
        mvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .session(loginSteps.session));

    String response = mvcResponse.andReturn().getResponse().getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(activityNames.size(), arr.length());

    List<String> responseActivityNames = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      int activityId = arr.getJSONObject(i).getInt("activity_id");
      String name = activityRepository.findById(activityId).get().getActivityName();
      responseActivityNames.add(name);
    }
    org.junit.jupiter.api.Assertions.assertTrue(responseActivityNames.containsAll(activityNames));
  }

  @When("User follows the activity {string}")
  public void user_follows_the_activity(String activityName) throws Exception{
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
  }

  @Then("User is a follower of {string}")
  public void user_is_a_follower_of(String activityName) throws Exception{
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(loginSteps.session)
            )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }

  @When("User unfollows the activity {string}")
  public void userUnfollowsTheActivity(String activityName) throws Exception {
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.delete(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
  }

  @Then("User is not a follower of {string}")
  public void userIsNotAFollowerOf(String activityName) throws Exception {
    Activity activity = activityRepository.findByActivityName(activityName);
    String url = "/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId();
    mvcResponse =
            mvc.perform(
                    MockMvcRequestBuilders.get(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(loginSteps.session));
    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/profiles/"+ loginSteps.profileId + "/subscriptions/activities/" + activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(loginSteps.session)
            )
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(false, obj.get("subscribed"));
  }

  @When("the activity {string} has its name changed to {string}")
  public void the_activity_has_its_name_changed_to(String activityNameBefore, String activityNameAfter) throws Exception {
    String jsonString;
    jsonString = "{\n" +
            "  \"activity_name\": \"" + activityNameAfter + "\",\n" +
            "  \"description\": \"tramping iz fun\",\n" +
            "  \"activity_type\":[ \n" +
            "    \"Hike\",\n" +
            "    \"Bike\"\n" +
            "  ],\n" +
            "  \"continuous\": true\n" +
            "}";

    String editActivityUrl = "/profiles/" + loginSteps.profileId + "/activities/" + activityId;
    mvc.perform(
            MockMvcRequestBuilders.put(editActivityUrl)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(loginSteps.session))
            .andExpect(status().isOk());
  }
}
