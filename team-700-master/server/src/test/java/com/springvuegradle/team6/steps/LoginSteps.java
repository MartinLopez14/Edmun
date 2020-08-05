package com.springvuegradle.team6.steps;

import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginSteps {

  public String jsonString;
  public MockHttpSession session;
  public ResultActions mvcResponse;
  public String profileId;

  @Autowired private MockMvc mvc;

  @Given("I create a user with email {string} and password {string}")
  public void i_create_a_user_with_email_and_password(String email, String password) throws Exception {
    jsonString =
            "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \"" + email + "\",\r\n  \"password\": \"" + password + "\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
    String createProfileUrl = "/profiles";
    profileId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createProfileUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }


  @Given("I log in with email {string} and password {string}")
  public void i_log_in_with_email_and_password(String email, String password) throws Exception {
    session = new MockHttpSession();
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
    mvcResponse = mvc.perform(
            MockMvcRequestBuilders.post(loginUrl)
                    .content(jsonString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session));
  }

  @Given("I registered a user with email {string} and password {string}")
  public void i_registered_a_user_with_email_and_password(String email, String password)
          throws Exception {
    jsonString =
            "{\r\n  \"lastname\": \"Test\",\r\n  \"firstname\": \"Cucumber\",\r\n  \"middlename\": \"Z\",\r\n  \"nickname\": \"BigTest\",\r\n  \"primary_email\": \""
                    + email
                    + "\",\r\n  \"password\": \""
                    + password
                    + "\",\r\n  \"bio\": \"Test is plain\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"nonbinary\"\r\n}";
    String createProfileUrl = "/profiles";
    profileId =
            mvc.perform(
                    MockMvcRequestBuilders.post(createProfileUrl)
                            .content(jsonString)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
  }

  @Given("I log in as a user with email {string} and password {string}")
  public void i_log_in_as_a_user_with_email_and_password(String email, String password)
          throws Exception {
    session = new MockHttpSession();
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
  }
}
