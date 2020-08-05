package com.springvuegradle.team6.controllers.ActivityControllerTest;

import com.springvuegradle.team6.models.*;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class EditActivityTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private ActivityHistoryRepository activityHistoryRepository;

  @Autowired
  private ActivityRoleRepository activityRoleRepository;

  private int id;

  private int activityId;

  private MockHttpSession session;

  private MockHttpSession otherSession;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    String profileJson =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    // Logs in and get profile Id
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profileJson)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    id = Integer.parseInt(body);

    Activity activity = new Activity();
    activity.setActivityName("Kaikoura Coast Track race");
    activity.setDescription("A big and nice race on a lovely peninsula");
    Set<ActivityType> activityTypes = new HashSet<>();
    activityTypes.add(ActivityType.Walk);
    activity.setActivityTypes(activityTypes);
    activity.setContinuous(true);
    activity.setStartTime("2000-04-28T15:50:41+1300");
    activity.setEndTime("2030-08-28T15:50:41+1300");
    activity.setProfile(profileRepository.findById(id));
    activity = activityRepository.save(activity);
    activityId = activity.getId();
  }

  @Test
  void editAllActivityWithoutAuthorizationReturnIsUnauthorized() throws Exception {
    otherSession = new MockHttpSession();
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(otherSession))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void editAllActivityWithoutTimeReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editAllActivityWithTimeReturnStatusIsOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Changed activity name\",\n"
            + "  \"description\": \"A new description\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Run\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2040-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editActivityWithStartDateBeforeNowAndUnchangedReturnStatusOK() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void editActivityWithStartDateBeforeNowChangedReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:51:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithBothDatesBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2000-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithStartDateAfterEndDateReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2040-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void editActivityWithStartDateEqualEndDateReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  void EditActivityNameReturnStatusBadRequest() throws Exception {
    String jsonString = "{\n" + "  \"activity_name\": \"Kaikoura Coast Track race\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void EditActivityLocationReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"state\": \"Canterbury\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void EditActivityLocationEmptyReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + " \"location\": {\n"
            + "  }"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void EditActivityLocationNoStateReturnStatusIsOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                + "  \"activity_type\":[ \n"
                + "    \"Walk\"\n"
                + "  ],\n"
                + "  \"continuous\": false,\n"
                + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
                + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
                + " \"location\": {\n"
                + " \t\"city\": \"Christchurch\",\n"
                + " \t\"country\": \"New Zealand\"\n"
                + "  }"
                + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());
  }

  @Test
  void editActivityWithHashtagReturnStatusOKAndActivityIsUpdated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
    Set<String> expectedResult = new HashSet<>();

    expectedResult.add("a1");
    expectedResult.add("a2");
    expectedResult.add("a3");
    expectedResult.add("a4");
    expectedResult.add("a5");

    Set<Tag> result = activityRepository.getActivityTags(activityId);
    Set<String> resultStrings = new HashSet<>();
    for (Tag tag : result) {
      resultStrings.add(tag.getName());
    }
    System.out.println("Flag");
    System.out.println(resultStrings);
    System.out.println(expectedResult);
    org.junit.jupiter.api.Assertions.assertTrue(resultStrings.containsAll(expectedResult));
  }

  @Test
  void editActivityWithInvalidHashtagReturnStatusIsBadRequestAndErrorMessageIsReturned()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"hashtags\": [\n"
            + "    \"#a1 123\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isBadRequest());
    Assert.assertTrue(
        responseString
            .andReturn()
            .getResponse()
            .getContentAsString()
            .contains("contains characters other than alphanumeric characters and underscores"));
  }

  @Test
  void editActivityWithMoreThan30HashtagReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\", \n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\",\n"
            + "    \"#a6\",\n"
            + "    \"#a7\",\n"
            + "    \"#a8\",\n"
            + "    \"#a9\",\n"
            + "    \"#a10\",\n"
            + "    \"#a11\",\n"
            + "    \"#a12\",\n"
            + "    \"#a13\",\n"
            + "    \"#a14\",\n"
            + "    \"#a15\",\n"
            + "    \"#a16\",\n"
            + "    \"#a17\",\n"
            + "    \"#a18\",\n"
            + "    \"#a19\",\n"
            + "    \"#a20\",\n"
            + "    \"#a21\",\n"
            + "    \"#a22\",\n"
                + "    \"#a23\",\n"
                + "    \"#a24\",\n"
                + "    \"#a25\",\n"
                + "    \"#a26\",\n"
                + "    \"#a27\",\n"
                + "    \"#a28\",\n"
                + "    \"#a29\",\n"
                + "    \"#a30\",\n"
                + "    \"#a31\"\n"
                + "  ]\n"
                + "}";
    ResultActions responseString =
            mvc.perform(
                    MockMvcRequestBuilders.put(
                            "/profiles/{profileId}/activities/{activityId}", id, activityId)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session));
    responseString.andExpect(status().isBadRequest());
  }

  @Test
  void editActivityReturnStatusOkAndCreatesNewEntryOfActivityHistory() throws Exception {
    Set<ActivityHistory> activityHistorySetBefore = activityHistoryRepository.findByActivity_id(activityId);
    String jsonString =
            "{\n"
                    + "  \"activity_name\": \"Changed activity name\",\n"
                    + "  \"description\": \"A new description\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Run\"\n"
                    + "  ],\n"
                    + "  \"continuous\": true\n"
                    + "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());

    Set<ActivityHistory> activityHistorySetAfter = activityHistoryRepository.findByActivity_id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(activityHistorySetBefore.size() + 1, activityHistorySetAfter.size());
  }

  @Test
  void editActivityWithNoChangesReturnStatusOkAndDoesNotCreateNewEntryOfActivityHistory() throws Exception {
    // calls the edit activity endpoint with the same json twice to ensure there is no updates to the activity
    String jsonString =
            "{\n" +
                    "  \"activity_name\": \"Kaikoura Coast Track race\",\n" +
                    "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                    "  \"activity_type\":[ \n" +
                    "    \"Walk\"\n" +
                    "  ],\n" +
                    "  \"continuous\": true,\n" +
                    "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n" +
                    "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n" +
                    "}";
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());

    Set<ActivityHistory> activityHistorySetBefore = activityHistoryRepository.findByActivity_id(activityId);
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());
    Set<ActivityHistory> activityHistorySetAfter = activityHistoryRepository.findByActivity_id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(activityHistorySetBefore.size(), activityHistorySetAfter.size());
  }

  @Test
  void EditActivityVisibilityTypeFromPublicToPrivateReturnStatusIsOkAndActivityVisibilityTypeIsChanged() throws Exception {
    String jsonString =
            "{\n" +
                    "  \"activity_name\": \"Kaikoura Coast Track race\",\n" +
                    "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                    "  \"activity_type\":[ \n" +
                    "    \"Walk\"\n" +
                    "  ],\n" +
                    "  \"continuous\": false,\n" +
                    "  \"start_time\": \"2030-04-28T15:50:41+1300\",\n" +
                    "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n" +
                    "  \"visibility\": \"private\"\n" +
                    "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());

    Assert.assertSame(VisibilityType.Private, activityRepository.findById(activityId).get().getVisibilityType());
  }

  @Test
  void editActivityVisbilityTypeFromPublicToRestrictedReturnStatusOkAndAccessRoleIsAssignedToAccessors() throws Exception {
    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    String jsonString =
            "{\n" +
                    "  \"activity_name\": \"Kaikoura Coast track\",\n" +
                    "  \"description\": \"A big and nice race on a lovely peninsula\",\n" +
                    "  \"activity_type\":[ \n" +
                    "    \"Walk\"\n" +
                    "  ],\n" +
                    "  \"continuous\": true,\n" +
                    "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n" +
                    "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n" +
                    "  \"visibility\": \"restricted\",\n" +
                    "  \"accessors\": [\n" +
                    "    \"johnny@email.com\",\n" +
                    "    \"doe@email.com\",\n" +
                    "    \"poly@pocket.com\"\n" +
                    "  ]\n" +
                    "}";

    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());

    Assert.assertSame(VisibilityType.Restricted, activityRepository.findById(activityId).get().getVisibilityType());

    List<ActivityRole> activityRoleList = activityRoleRepository.findByActivity_Id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(3, activityRoleList.size());

  }
}
