package com.springvuegradle.team6.controllers.ActivityControllerTest;

import com.springvuegradle.team6.models.Activity;
import com.springvuegradle.team6.models.ActivityHistory;
import com.springvuegradle.team6.models.ActivityHistoryRepository;
import com.springvuegradle.team6.models.ActivityRepository;
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

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class DeleteActivityTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private ActivityHistoryRepository activityHistoryRepository;

  private int id;

  private int activityId;

  private MockHttpSession session;

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

    String activityJson =
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
    activityId = Integer.parseInt(activityBody);
  }

  @Test
  void deleteActivityReturnStatusOKActivityIsArchived() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .session(session))
        .andExpect(status().isOk());
    Activity activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertTrue(activity.isArchived());
  }

  @Test
  void deleteActivityThatDoesNotExistReturnStatus4xxError() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete("/profiles/{profileId}/activities/45354435353353", id)
                .session(session))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteSameActivityTwiceReturnStatusOkActivityIsArchived() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .session(session))
        .andExpect(status().isOk());
    Activity activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertTrue(activity.isArchived());
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                .session(session))
        .andExpect(status().isOk());
    activity = activityRepository.findById(activityId).get();
    org.junit.jupiter.api.Assertions.assertTrue(activity.isArchived());
  }

  @Test
  void deleteAnotherUsersActivityReturnStatus4xxError() throws Exception {
    String differentUser =
        "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly2@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";
    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                    .content(differentUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isCreated())
            .andDo(print());
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .session(session))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteWithUnauthorisedUserReturnStatus4xxError() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId))
            .andExpect(status().is4xxClientError());
  }

  @Test
  void deleteActivityReturnStatusOkAndCreatesNewEntryOfActivityHistory() throws Exception {
    Set<ActivityHistory> activityHistorySetBefore = activityHistoryRepository.findByActivity_id(activityId);
    mvc.perform(
            MockMvcRequestBuilders.delete(
                    "/profiles/{profileId}/activities/{activityId}", id, activityId)
                    .session(session))
            .andExpect(status().isOk());

    Set<ActivityHistory> activityHistorySetAfter = activityHistoryRepository.findByActivity_id(activityId);
    org.junit.jupiter.api.Assertions.assertEquals(activityHistorySetBefore.size() + 1, activityHistorySetAfter.size());
  }
}
