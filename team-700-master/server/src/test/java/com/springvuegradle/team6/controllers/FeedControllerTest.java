package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.LoginRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class FeedControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ActivityHistoryRepository activityHistoryRepository;

  @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;

  private MockHttpSession session;

  private Profile profile;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();

    Set<Email> emails = new HashSet<>();
    Email email = new Email("johnydoe99@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    profile = new Profile();
    profile.setFirstname("John");
    profile.setLastname("Doe");
    profile.setEmails(emails);
    profile.setDob("2010-01-01");
    profile.setPassword("Password1");
    profile.setGender("male");
    profile = profileRepository.save(profile);
    String login_url = "/login/";
    mvc.perform(
            post(login_url)
                .content(
                    "{\n"
                        + "\t\"email\" : \"johnydoe99@gmail.com\",\n"
                        + "\t\"password\": \"Password1\"\n"
                        + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void testGetHomeFeedReturnStatusOkReturnCorrectOrder() throws Exception {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId()).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());

    LocalDateTime dateTime = LocalDateTime.parse(arr.getJSONObject(0).getString("date_time"));
    LocalDateTime dateTime1 = LocalDateTime.parse(arr.getJSONObject(1).getString("date_time"));
    org.junit.jupiter.api.Assertions.assertTrue(dateTime.isAfter(dateTime1));
  }

  @Test
  void testGetHomeFeedWithSubscriptionEndDateReturnStatusOkReturnCorrectResults() throws Exception {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 3, 13, 15));
    history1.setMessage("Activity " + activity.getActivityName() + " was edited");
    history1 = activityHistoryRepository.save(history1);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory.setEndDateTime(LocalDateTime.of(2020, 3, 2, 1, 0));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId()).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void testGetHomeFeedWithMultipleActivitySubscriptionsReturnStatusOkReturnCorrectResults()
      throws Exception {
    Set<Email> emails = new HashSet<>();
    Email email = new Email("polypocket@gmail.com");
    email.setPrimary(true);
    emails.add(email);
    Profile profile1 = new Profile();
    profile1.setFirstname("Poly");
    profile1.setLastname("Pocket");
    profile1.setEmails(emails);
    profile1.setDob("2010-02-01");
    profile1.setPassword("Password1");
    profile1.setGender("female");
    profile1 = profileRepository.save(profile1);

    Activity activity = new Activity();
    activity.setProfile(profile1);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile1);
    activity1.setActivityName("Running");
    activity1.setContinuous(true);
    activity1 = activityRepository.save(activity1);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity1);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 3, 13, 15));
    history1.setMessage("Activity " + activity1.getActivityName() + " was created");
    history1 = activityHistoryRepository.save(history1);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    SubscriptionHistory subscriptionHistory1 = new SubscriptionHistory();
    subscriptionHistory1.setActivity(activity1);
    subscriptionHistory1.setProfile(profile);
    subscriptionHistory1.setStartDateTime(LocalDateTime.of(2020, 2, 1, 15, 14));
    subscriptionHistory1 = subscriptionHistoryRepository.save(subscriptionHistory1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId()).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void testGetHomeFeedWithOffsetLimitReturnStatusOkReturnCorrectResults() throws Exception {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Running");
    activity1.setContinuous(true);
    activity1 = activityRepository.save(activity1);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity1);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 3, 13, 15));
    history1.setMessage("Activity " + activity1.getActivityName() + " was created");
    history1 = activityHistoryRepository.save(history1);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    SubscriptionHistory subscriptionHistory1 = new SubscriptionHistory();
    subscriptionHistory1.setActivity(activity1);
    subscriptionHistory1.setProfile(profile);
    subscriptionHistory1.setStartDateTime(LocalDateTime.of(2020, 2, 1, 15, 14));
    subscriptionHistory1 = subscriptionHistoryRepository.save(subscriptionHistory1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/feed/homefeed/" + profile.getId() + "?offset=1&limit=2")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void testGetHomeFeedWithLimitLargerThanNumberOfFeedsLeftReturnStatusOkReturnCorrectResults()
      throws Exception {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Running");
    activity1.setContinuous(true);
    activity1 = activityRepository.save(activity1);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity1);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 3, 13, 15));
    history1.setMessage("Activity " + activity1.getActivityName() + " was created");
    history1 = activityHistoryRepository.save(history1);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    SubscriptionHistory subscriptionHistory1 = new SubscriptionHistory();
    subscriptionHistory1.setActivity(activity1);
    subscriptionHistory1.setProfile(profile);
    subscriptionHistory1.setStartDateTime(LocalDateTime.of(2020, 2, 1, 15, 14));
    subscriptionHistory1 = subscriptionHistoryRepository.save(subscriptionHistory1);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId() + "?limit=20")
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(4, arr.length());
  }

  @Test
  void testGetHomeFeedWithOffsetLargerThanNumberOfFeedsLeftReturnStatusOkReturnNoResults()
          throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId() + "?offset=1")
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void testGetHomeFeedWithOffsetPlusLimitLargerThanNumberOfFeedsLeftReturnStatusOkReturnCorrectResults()
          throws Exception {
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Play rock, paper, scissors");
    activity.setContinuous(true);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Running");
    activity1.setContinuous(true);
    activity1 = activityRepository.save(activity1);

    ActivityHistory history = new ActivityHistory();
    history.setActivity(activity);
    history.setTimeDate(LocalDateTime.of(2020, 3, 1, 13, 15));
    history.setMessage("Activity " + activity.getActivityName() + " was created");
    history = activityHistoryRepository.save(history);

    ActivityHistory history1 = new ActivityHistory();
    history1.setActivity(activity1);
    history1.setTimeDate(LocalDateTime.of(2020, 3, 3, 13, 15));
    history1.setMessage("Activity " + activity1.getActivityName() + " was created");
    history1 = activityHistoryRepository.save(history1);

    SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
    subscriptionHistory.setActivity(activity);
    subscriptionHistory.setProfile(profile);
    subscriptionHistory.setStartDateTime(LocalDateTime.of(2020, 2, 1, 13, 14));
    subscriptionHistory = subscriptionHistoryRepository.save(subscriptionHistory);

    SubscriptionHistory subscriptionHistory1 = new SubscriptionHistory();
    subscriptionHistory1.setActivity(activity1);
    subscriptionHistory1.setProfile(profile);
    subscriptionHistory1.setStartDateTime(LocalDateTime.of(2020, 2, 1, 15, 14));
    subscriptionHistory1 = subscriptionHistoryRepository.save(subscriptionHistory1);

    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/feed/homefeed/" + profile.getId() + "?offset=2&limit=4")
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("feeds");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }
}
