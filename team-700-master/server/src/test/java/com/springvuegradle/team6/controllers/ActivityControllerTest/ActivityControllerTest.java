package com.springvuegradle.team6.controllers.ActivityControllerTest;

import com.springvuegradle.team6.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class ActivityControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRoleRepository activityRoleRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    activityRepository.deleteAll();
    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
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
  }

  @Test
  void createActivityWithTimeReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithDateOnlyReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2030-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2030-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithStartDateBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithBothDateBeforeNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2010-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2010-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithStartDateAfterEndDateNowReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2060-04-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithStartDateEqualEndDateReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": false,\n"
            + "  \"start_time\": \"2050-08-28T24:00:00+1300\", \n"
            + "  \"end_time\": \"2050-08-28T24:00:00+1300\"\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithOnlyMandatoryValuesReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithNoActivityNameReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithNoActivityTypeReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "  ]\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithActivityTypeIsNullReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"continuous\": true\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithNoContinuousReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ]\n"
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithLocationReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"state\": \"Canterbury\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithLocationNoStateReturnStatusIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + " \t\"city\": \"Christchurch\",\n"
            + " \t\"country\": \"New Zealand\"\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated());
  }

  @Test
  void createActivityWithLocationEmptyReturnStatusBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + " \"location\": {\n"
            + "  }"
            + ""
            + "}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithHashtagReturnStatusIsCreatedAndActivityIsCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\",\n"
            + "    \"#a2\",\n"
            + "    \"#a3\",\n"
            + "    \"#a4\",\n"
            + "    \"#a5\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
    Assert.assertTrue(
        activityRepository
            .findById(
                Integer.parseInt(responseString.andReturn().getResponse().getContentAsString()))
            .isPresent());
  }

  @Test
  void createActivityWithInvalidHashtagReturnStatusIsBadRequestAndErrorMessageIsReturned()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
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
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
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
  void createActivityWithMoreThan30HashtagReturnStatusIsBadRequest() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
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
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isBadRequest());
  }

  @Test
  void createActivityWithVisibilityTypePublicReturnStatusOk() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Tramping\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": \"true\",\n"
            + "  \"visibility\": \"public\"\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
  }

  @Test
  void createActivityWithoutVisibilityTypeReturnStatusOkAndVisibilityTypeDefaultsToPublic()
      throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Running\",\n"
            + "  \"description\": \"tramping iz fun\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Hike\",\n"
            + "    \"Bike\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"hashtags\": [\n"
            + "    \"#a1\"\n"
            + "  ]\n"
            + "}";
    ResultActions responseString =
        mvc.perform(
            MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session));
    responseString.andExpect(status().isCreated());
    Assert.assertSame(
        VisibilityType.Public,
        activityRepository
            .findById(
                Integer.parseInt(responseString.andReturn().getResponse().getContentAsString()))
            .get()
            .getVisibilityType());
  }

  @Test
  void createActivityWithVisibilityTypeRestrictedReturnStatusOkAndAccessRoleIsAssignedToAccessors()
      throws Exception {
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
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast track\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true,\n"
            + "  \"start_time\": \"2000-04-28T15:50:41+1300\",\n"
            + "  \"end_time\": \"2030-08-28T15:50:41+1300\",\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\",\n"
            + "    \"doe@email.com\",\n"
            + "    \"poly@pocket.com\"\n"
            + "  ]\n"
            + "}";

    ResultActions responseString =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated());

    Integer activityId =
        Integer.parseInt(responseString.andReturn().getResponse().getContentAsString());

    Assert.assertSame(
        VisibilityType.Restricted,
        activityRepository.findById(activityId).get().getVisibilityType());

    List<ActivityRole> activityRoleList = activityRoleRepository.findByActivity_Id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(3, activityRoleList.size());
  }

  @Test
  void createActivityReturnStatusIsCreatedCreatorRoleCreated() throws Exception {
    String jsonString =
        "{\n"
            + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
            + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
            + "  \"activity_type\":[ \n"
            + "    \"Walk\"\n"
            + "  ],\n"
            + "  \"continuous\": true\n"
            + "}";
    String activityId =
        mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    List<ActivityRole> activityRoles = activityRoleRepository.findByActivity_Id(Integer.parseInt(activityId));
    ActivityRole creator = activityRoles.get(0);
    org.junit.jupiter.api.Assertions.assertEquals(ActivityRoleType.Creator, creator.getActivityRoleType());
    org.junit.jupiter.api.Assertions.assertEquals(id, creator.getProfile().getId());

  }

  @Test
  void getActivityCreatorReturnStatusOk() throws Exception {
    Profile profile1 = profileRepository.findById(id);
    Activity testActivity1 = new Activity();
    testActivity1.setActivityName("Test");
    testActivity1.setProfile(profile1);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    // ID to check for
    String activity1Id = testActivity1.getId().toString();

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activity1Id + "/creatorId")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals(((Integer) id).toString(), response);
  }

  @Test
  void getActivityById() throws Exception {
    Profile profile1 = profileRepository.findById(id);
    Activity testActivity1 = new Activity();
    testActivity1.setActivityName("Test");
    testActivity1.setProfile(profile1);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    // ID to check for
    String activity1Id = testActivity1.getId().toString();

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/activities/" + activity1Id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject result = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(activity1Id, result.getString("id"));
  }

  @Test
  void getActivityByIdWhenIdDoesntExists() throws Exception {
    Profile profile1 = profileRepository.findById(id);
    Activity testActivity1 = new Activity();
    testActivity1.setId(1000);
    testActivity1.setProfile(profile1);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setId(2000);
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/3")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getActivitiesByUserIdReturnActivities() throws Exception {
    Activity testActivity1 = new Activity();
    Profile profile1 = profileRepository.findById(id);
    Profile profile2 = new Profile();
    profileRepository.save(profile2);

    testActivity1.setProfile(profile1);

    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile1);
    activityRepository.save(testActivity2);

    Activity testActivity3 = new Activity();
    testActivity3.setProfile(profile2);
    activityRepository.save(testActivity3);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles/{profileId}/activities", profile1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());
  }

  @Test
  void getActivitiesWhenUserHasNoActivitiesReturnNoActivities() throws Exception {
    Activity testActivity1 = new Activity();
    Profile profile1 = profileRepository.findById(id);
    Profile profile2 = new Profile();
    profileRepository.save(profile2);

    testActivity1.setProfile(profile2);
    activityRepository.save(testActivity1);

    Activity testActivity2 = new Activity();
    testActivity2.setProfile(profile2);
    activityRepository.save(testActivity2);

    Activity testActivity3 = new Activity();
    testActivity3.setProfile(profile2);
    activityRepository.save(testActivity3);

    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/{profileId}/activities", profile1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string("[]"));
  }

  @Test
  void getActivitiesWhereUserDoesNotExistReturnStatusNotFound() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/profiles/{profileId}/activities", 9999)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getActivityThatIsArchivedReturnStatusOKAndNoData() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setArchived(true);
    activity = activityRepository.save(activity);
    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string("Activity is archived"));
  }

  @Test
  void getActivityPrivateAccessAnotherUserTriesToView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityType("private");
    activity = activityRepository.save(activity);

    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"john@pockets.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("Activity is private"));
  }

  @Test
  void getPrivateActivityOwnerCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityType("private");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void getPublicActivityAnyoneCanView() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityType("public");
    activity = activityRepository.save(activity);

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }

  @Test
  void getRestrictedActivityWithAccess() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityType("restricted");
    activity = activityRepository.save(activity);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"johnny@email.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String jsonString1 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\",\n"
            + "    \"doe@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString1)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    System.out.println(session.getAttribute("id"));

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());
  }
  ;

  @Test
  void getRestrictedActivityWithoutAccess() throws Exception {
    Activity activity = new Activity();
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);
    activity.setActivityName("My running activity");
    activity.setContinuous(true);
    activity.setVisibilityType("restricted");
    activity = activityRepository.save(activity);

    Profile profile2 = new Profile();
    profile2.setFirstname("Doe");
    profile2.setLastname("John");
    Set<Email> email2 = new HashSet<Email>();
    email2.add(new Email("doe@email.com"));
    profile2.setEmails(email2);
    profileRepository.save(profile2);

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"johnny@email.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

    mvc.perform(MockMvcRequestBuilders.get("/logout/").session(session))
        .andExpect(status().isOk())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String jsonString1 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"doe@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString1)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    System.out.println(session.getAttribute("id"));

    mvc.perform(
            MockMvcRequestBuilders.get("/activities/{activityId}", activity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().is4xxClientError());
  }
  ;

  @Test
  void putActivityVisibilityAdd() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

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

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\",\n"
            + "    \"doe@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(3, result.size());
  }

  @Test
  void putActivityVisibilityDelete() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

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

    Profile profile3 = new Profile();
    profile3.setFirstname("Martin");
    profile3.setLastname("Johns");
    Set<Email> email3 = new HashSet<Email>();
    email3.add(new Email("martin@email.com"));
    profile3.setEmails(email3);
    profileRepository.save(profile3);

    String jsonString1 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\",\n"
            + "    \"doe@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString1)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    String jsonString2 =
        "{\n"
            + "  \"visibility\": \"restricted\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\",\n"
            + "    \"martin@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString2)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(2, result.size());
  }

  @Test
  void putVisibilityTypeAsPublicWillIgnoreAccessorList() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    Profile profile1 = new Profile();
    profile1.setFirstname("Johnny");
    profile1.setLastname("Dong");
    Set<Email> email1 = new HashSet<Email>();
    email1.add(new Email("johnny@email.com"));
    profile1.setEmails(email1);
    profileRepository.save(profile1);

    String jsonString =
        "{\n"
            + "  \"visibility\": \"public\",\n"
            + "  \"accessors\": [\n"
            + "    \"johnny@email.com\"\n"
            + "  ]\n"
            + "}";

    mvc.perform(
        MockMvcRequestBuilders.put(
                "/profiles/{profileId}/activities/{activityId}/visibility", id, activity.getId())
            .content(jsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session));

    List<ActivityRole> result = activityRoleRepository.findByActivity_Id(activity.getId());
    org.junit.jupiter.api.Assertions.assertEquals(0, result.size());
  }

  @Test
  void testUserIsAFollowerUponCreatingActivity() throws Exception {
    String activityJson =
            "{\n"
                    + "  \"activity_name\": \"Kaikoura Coast Track racey\",\n"
                    + "  \"description\": \"A big and nice race on a lovely peninsula\",\n"
                    + "  \"activity_type\":[ \n"
                    + "    \"Walk\"\n"
                    + "  ],\n"
                    + "  \"continuous\": false,\n"
                    + "  \"start_time\": \"2030-04-28T15:50:41+1300\", \n"
                    + "  \"end_time\": \"2030-08-28T15:50:41+1300\"\n"
                    + "}";
    // Creates an activity
    String activityBody =
            mvc.perform(
                    MockMvcRequestBuilders.post("/profiles/{profileId}/activities", id)
                            .content(activityJson)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    int activityId = Integer.parseInt(activityBody);

    String response =
            mvc.perform(MockMvcRequestBuilders
                    .get("/profiles/"+ id + "/subscriptions/activities/" + activityId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session)
            )
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    org.junit.jupiter.api.Assertions.assertEquals(true, obj.get("subscribed"));
  }
  
  @Test
  void addActivityHashtagsReturnStatusOkAndHashtagSetSizeIsIncreased() throws Exception {
    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    String jsonString =
        "{\n"
            + "  \"hashtags\": [\n"
            + "    \"#nosweatnoglory\",\n"
            + "    \"#whalewatching\"\n"
            + "  ]\n"
            + "}";

    Set<Tag> hashtagSetBefore = activityRepository.findById(activity.getId()).get().getTags();
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/hashtags", id, activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Tag> hashtagSetAfter = activityRepository.findById(activity.getId()).get().getTags();
    org.junit.jupiter.api.Assertions.assertEquals(
        hashtagSetBefore.size() + 2, hashtagSetAfter.size());
  }

  @Test
  void removeActivityHashtagsReturnStatusOkAndHashtagSetSizeIsDecreased() throws Exception {
    Tag tag1 = new Tag("#nosweatnoglory");
    tagRepository.save(tag1);
    Tag tag2 = new Tag("#whalewatching");
    tagRepository.save(tag2);
    Set<Tag> tagSet = new HashSet<>();
    tagSet.add(tag1);
    tagSet.add(tag2);

    Profile owner = profileRepository.findById(id);
    Activity activity = new Activity();
    activity.setActivityName("testing my run");
    activity.setContinuous(true);
    activity.setTags(tagSet);
    activity.setProfile(owner);
    activity = activityRepository.save(activity);

    String jsonString = "{\n" + "  \"hashtags\": [\n" + "    \"#nosweatnoglory\"\n" + "  ]\n" + "}";

    Set<Tag> hashtagSetBefore = activityRepository.findById(activity.getId()).get().getTags();
    mvc.perform(
            MockMvcRequestBuilders.put(
                    "/profiles/{profileId}/activities/{activityId}/hashtags", id, activity.getId())
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    Set<Tag> hashtagSetAfter = activityRepository.findById(activity.getId()).get().getTags();
    org.junit.jupiter.api.Assertions.assertEquals(
        hashtagSetBefore.size() - 1, hashtagSetAfter.size());
  }
}
