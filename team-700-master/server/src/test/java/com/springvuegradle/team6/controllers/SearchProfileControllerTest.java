package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchProfileControllerTest {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  @BeforeAll
  void setup() throws Exception {
    session = new MockHttpSession();
    String profile1 =
        "{\n"
            + "  \"lastname\": \"Pocket\",\n"
            + "  \"firstname\": \"Poly\",\n"
            + "  \"middlename\": \"Michelle\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"poly@pocket.com\",\n"
            + "\"additional_email\": [\n"
            + "    \"poly1@pocket.com\",\n"
            + "    \"poly2@pocket.com\",\n"
            + "    \"poly3@pocket.com\"\n"
            + "  ],\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n"
            + "}";
    String profile2 =
        "{\n"
            + "  \"lastname\": \"Benson\",\n"
            + "  \"firstname\": \"Maurice\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"Jacky\",\n"
            + "  \"primary_email\": \"jacky@google.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd1\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}\n";
    String profile3 =
        "{\n"
            + "  \"lastname\": \"Benson\",\n"
            + "  \"firstname\": \"Maurice\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"JackyCopy\",\n"
            + "  \"primary_email\": \"jackyCopy@google.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd1\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}\n";

    String profile4 =
        "{\n"
            + "  \"lastname\": \"Maurice\",\n"
            + "  \"firstname\": \"Benson\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"Jacky\",\n"
            + "  \"primary_email\": \"jacfky@ssgoogslse.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd9\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}";

    String profile5 =
        "{\n"
            + "  \"lastname\": \"Pocket\",\n"
            + "  \"firstname\": \"Poly\",\n"
            + "  \"middlename\": \"Michelle Christopher\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"polyChris@pocket.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n"
            + "}";

    String profile6 =
        "{\n"
            + "  \"lastname\": \"Duan\",\n"
            + "  \"firstname\": \"Yu\",\n"
            + "  \"nickname\": \"Duany20\",\n"
            + "  \"primary_email\": \"duanyu@working.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Covid sucks\",\n"
            + "  \"date_of_birth\": \"1980-11-11\",\n"
            + "  \"gender\": \"male\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile1)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile2)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile3)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile4)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile5)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile6)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String loginInfo =
        "{\n" + "\t\"email\" : \"poly@pocket.com\",\n" + "\t\"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(loginInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    id = Integer.parseInt(body);

    String activityTypeInfo =
            "{\n" + "\t\"activities\" : [\"Run\", \"Bike\", \"Hike\"]\n" + "}";
    mvc.perform(
            MockMvcRequestBuilders.put("/profiles/" + id + "/activity-types")
                    .content(activityTypeInfo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
            .andExpect(status().isOk());
  }

  @Test
  void searchProfileByFullnameReturnOneResult() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?fullname=Yu%20Duan", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByFullnameReturnMultipleResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?fullname=Maurice%20Jack%20Benson", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void searchProfileByFullnameWithSpaceInMiddleNameReturnCorrectFirstResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles?fullname=Poly%20Michelle%20Christopher%20Pocket", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    String middlename = arr.getJSONObject(0).getString("middlename");
    org.junit.jupiter.api.Assertions.assertEquals("Michelle Christopher", middlename);
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void searchProfileByFullnameWithOnlyFirstNameReturnResults() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?fullname=Maurice", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(3, arr.length());
  }

  @Test
  void searchProfileByFullnameReturnNoResults() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?fullname=a", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByFullnameCount() throws Exception {

    for (int i = 0; i<100; i++) {
      Profile profile = new Profile();
      profile.setFirstname("Foo");
      profile.setLastname("Bar");
      Set<Email> addtionalEmails = new HashSet<Email>();
      profile.setEmails(addtionalEmails );
      profileRepository.save(profile);
    }
    long startTime = System.nanoTime();
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles/count?fullname=Foo%20Bar", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals("100", response);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime)/1000000;
    //See how long it takes
    System.out.println(duration);
  }



  @Test
  void searchProfileByPrimaryEmailReturnResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?email=jacky@google.com", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    String email = arr.getJSONObject(0).getString("primary_email");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals("jacky@google.com", email);
  }

  @Test
  void searchProfileByAdditionalEmailReturnResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?email=poly1@pocket.com", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    String email = arr.getJSONObject(0).getString("primary_email");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
    org.junit.jupiter.api.Assertions.assertEquals("poly@pocket.com", email);
  }

  @Test
  void searchProfileByPrimaryEmailReturnNoResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?email=foofake@barbar.com", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByNicknameReturnOneResult() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Duany20", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByNicknameReturnMultipleResults() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Pino", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void searchProfileByNicknameReturnNoResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?nickname=foobarhelloworld", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByNicknameCount() throws Exception {

    for (int i = 0; i<100; i++) {
      Profile profile = new Profile();
      profile.setNickname("FooBar");
      Set<Email> addtionalEmails = new HashSet<Email>();
      profile.setEmails(addtionalEmails );
      profileRepository.save(profile);
    }
    long startTime = System.nanoTime();
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles/count?nickname=FooBar", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals("100", response);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime)/1000000;
    //See how long it takes
    System.out.println(duration);
  }

  @Test
  void searchProfileBySingleActivityType() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?activity=Run", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByMultipleActivityTypeAnd() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?activity=Run%20Bike&method=AND", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByMultipleActivityTypeOr() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?activity=Run%20Bike&method=OR", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByMultipleActivityNoResults() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?activity=Run%20Bike%20Activity&method=AND", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByFullNameAndActivityTypeAnd() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?fullname=Poly&activity=Run%20Bike&method=AND", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByFullNameAndActivityTypeOr() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?fullname=Poly&activity=Run%20Bike&method=OR", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByFullNameAndActivityTypeNoResultsAnd() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?fullname=blahblahblah&activity=Run%20Bike&method=AND", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByFullNameAndActivityTypeNoResultsOr() throws Exception {
    String response =
            mvc.perform(
                    MockMvcRequestBuilders.get("/profiles?fullname=blahblahblah&activity=Run%20Bike&method=OR", id)
                            .session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }

  @Test
  void searchProfileByNicknameAndSingleActivity() throws Exception {
    String response =
            mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Pino&activity=Run", id).session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByNicknameCountAndSingleActivity() throws Exception {
    String response =
            mvc.perform(MockMvcRequestBuilders.get("/profiles/count?nickname=Pino&activity=Run", id).session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    org.junit.jupiter.api.Assertions.assertEquals("1", response);
  }

  @Test
  void searchProfileByNicknameAndMultipleActivityAnd() throws Exception {
    String response =
            mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Pino&activity=Run%20Bike&method=AND", id).session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByNicknameAndMultipleActivityOr() throws Exception {
    String response =
            mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Pino&activity=Run%20Tramping&method=OR", id).session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(1, arr.length());
  }

  @Test
  void searchProfileByNicknameAndActivityNoResult() throws Exception {
    String response =
            mvc.perform(MockMvcRequestBuilders.get("/profiles?nickname=Pino&activity=Tramping", id).session(session))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }
}
