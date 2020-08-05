package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
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
public class TagControllerTest {

  @Autowired private ActivityRepository activityRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private ProfileRepository profileRepository;

  private MockHttpSession session;

  @Autowired private MockMvc mvc;

  private int id;

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
  void getHashtagAutocompleteHashtagLengthLessThanThreeReturnInitialHashtag() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=co", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals("co", arr.get(0));
  }

  @Test
  void getHashtagAuctocompleteReturnCorrectOrder() throws Exception {
    Tag cool = new Tag();
    cool.setName("is_cool");
    cool = tagRepository.save(cool);

    Tag cold = new Tag();
    cold.setName("is_cold");
    cold = tagRepository.save(cold);

    Tag colour = new Tag();
    colour.setName("is_colour");
    colour = tagRepository.save(colour);

    Tag awesome = new Tag();
    awesome.setName("awesome");
    awesome = tagRepository.save(awesome);

    Profile profile = profileRepository.getOne(id);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    tags.add(cold);
    tags.add(cool);
    activity.setTags(tags);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cold);
    tags1.add(colour);
    activity1.setTags(tags1);
    activity1 = activityRepository.save(activity1);

    Activity activity2 = new Activity();
    activity2.setProfile(profile);
    activity2.setActivityName("Burnside Park Rugby");
    activity2.setContinuous(true);
    Set<Tag> tags2 = new HashSet<>();
    tags2.add(cold);
    tags2.add(awesome);
    tags2.add(cool);
    activity2.setTags(tags2);
    activity2 = activityRepository.save(activity2);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=is_c", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");

    List<String> expectedResultList = new ArrayList<>();
    expectedResultList.add("is_cold");
    expectedResultList.add("is_cool");
    expectedResultList.add("is_colour");
    JSONArray expectedResult = new JSONArray(expectedResultList);

    org.junit.jupiter.api.Assertions.assertEquals(expectedResult, arr);
  }

  @Test
  void getHashtagAutocompleteReturnTenResultsMax() throws Exception {
    Profile profile = profileRepository.getOne(id);
    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    Set<Tag> tags = new HashSet<>();
    for (int i = 0; i < 20; i++) {
      Tag tag = new Tag();
      tag.setName("number" + i);
      tag = tagRepository.save(tag);
      tags.add(tag);
    }
    activity.setTags(tags);
    activity = activityRepository.save(activity);

    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/autocomplete?hashtag=number", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(10, arr.length());
  }

  @Test
  void getActivitiesByHashTagReturnStatusOkReturnOneResult() throws Exception {
    // Set up data
    Tag tag = new Tag();
    tag.setName("myrun");

    Activity activity = new Activity();
    activity.setActivityName("Test");
    Set<Tag> tags = new HashSet<Tag>();
    tags.add(tag);
    activity.setTags(tags);
    activity.setContinuous(false);
    activity.setDescription("description blah blah");
    Profile profile = profileRepository.findById(id);
    activity.setProfile(profile);

    tagRepository.save(tag);
    activityRepository.save(activity);

    // Actual MVC response
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/myrun")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(1, result.length());
  }

  @Test
  void getActivitiesByHashtagReturnStatusOkReturnNoResult() throws Exception {
    Tag tag = new Tag();
    tag.setName("myrun");
    tagRepository.save(tag);

    // Actual MVC response
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/myrun")
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(0, result.length());
  }

  @Test
  void getActivitiesByHashtagReturnStatusOkReturnCorrectOrder() throws Exception {
    Tag cold = new Tag();
    cold.setName("is_cold");
    cold = tagRepository.save(cold);

    Profile profile = profileRepository.getOne(id);

    Activity activity = new Activity();
    activity.setProfile(profile);
    activity.setActivityName("Run at Hagley Park");
    activity.setContinuous(true);
    activity.setCreationDate(LocalDateTime.now());
    Set<Tag> tags = new HashSet<>();
    tags.add(cold);
    activity.setTags(tags);
    activity = activityRepository.save(activity);

    Activity activity1 = new Activity();
    activity1.setProfile(profile);
    activity1.setActivityName("Avonhead Park Walk");
    activity1.setContinuous(true);
    activity1.setCreationDate(LocalDateTime.now());
    Set<Tag> tags1 = new HashSet<>();
    tags1.add(cold);
    activity1.setTags(tags1);
    activity1 = activityRepository.save(activity1);

    // Actual MVC response
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/hashtag/" + cold.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONArray result = new JSONArray(response);
    org.junit.jupiter.api.Assertions.assertEquals(2, result.length());

    LocalDateTime dateTime = LocalDateTime.parse(result.getJSONObject(0).getString("creationDate"));
    LocalDateTime dateTime1 =
        LocalDateTime.parse(result.getJSONObject(1).getString("creationDate"));
    org.junit.jupiter.api.Assertions.assertTrue(dateTime.isAfter(dateTime1));
  }
}
