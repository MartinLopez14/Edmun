package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.location.Location;
import com.springvuegradle.team6.models.location.NamedLocation;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import com.springvuegradle.team6.requests.EditPasswordRequest;
import com.springvuegradle.team6.requests.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
class UserProfileControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private CreateProfileRequest getDummyProfile() {
        CreateProfileRequest validRequest = new CreateProfileRequest();
        validRequest.firstname = "John";
        validRequest.middlename = "S";
        validRequest.lastname = "Doe";
        validRequest.nickname = "BigJ";
        validRequest.bio = "Just another plain jane";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "1999-12-20";
        validRequest.gender = "male";
        validRequest.fitness = 0;
        return validRequest;
    }

    @Test
    void createProfileWithEmptyRequest() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();

        // Empty test
        mvc.perform(
                post(createProfileUrl)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }
    @Test
    void createProfileWithValidRequest() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Success Case
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    void createProfileWithEmptyLastName() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Empty lastname
        validRequest.lastname = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileWithEmptyFirstName() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Empty firstname
        validRequest.lastname = "Doe";
        validRequest.firstname = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    void createProfileWithEmptyPrimaryEmail() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();

        // Empty primary email
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileWithEmptyPassword() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Empty password
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileWithEmptyDateOfBirth() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Empty date of birth
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileWithEmptyGender() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest validRequest = getDummyProfile();


        // Empty Gender
        validRequest.lastname = "Doe";
        validRequest.firstname = "John";
        validRequest.email = "johndoe@uclive.ac.nz";
        validRequest.password = "SuperSecurePassword123";
        validRequest.dob = "12-03-2000";
        validRequest.gender = "male";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileEmailExists() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();

        // Success Case
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        // Email already exists because we just added it
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidPassword() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.password = "jacky";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidDateFormat() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.dob = "1985/12/20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidDateRange() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.dob = "2021-12-20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        request.email = "anotheremail@gmail.com";
        request.dob = "1800-12-20";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidEmail() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        request.email = "test.com";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createProfileInvalidNames() throws Exception {
        String createProfileUrl = "/profiles";
        CreateProfileRequest request = getDummyProfile();
        // Invalid nickname
        request.nickname = "#mynickname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Invalid first name
        request.nickname = "nick";
        request.firstname = "#firstname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // Invalid last name
        request.firstname = "firstname";
        request.lastname = "@lastname";
        mvc.perform(
                post(createProfileUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void editPasswordUserDoesNotExistReturn4xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Passwords don't match
        EditPasswordRequest request = new EditPasswordRequest();

        // Try a case for user that does not exist and not logged in...
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                put("/profiles/999/password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void editPasswordPasswordsDontMatchReturn4xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Passwords don't match
        EditPasswordRequest request = new EditPasswordRequest();

        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String editPassUrl = "/profiles/" + id + "/password";
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1235";

        mvc.perform(
                put(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void editPasswordOldPasswordIncorrectReturn4xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Passwords don't match
        EditPasswordRequest request = new EditPasswordRequest();

        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String editPassUrl = "/profiles/" + id + "/password";
        // Old password isn't correct
        request.oldpassword = "SuperSecurePassword12";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                put(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void editPasswordNoUppercaseReturn4xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Passwords don't match
        EditPasswordRequest request = new EditPasswordRequest();

        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String editPassUrl = "/profiles/" + id + "/password";

        // Password dosent have uppercase
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "supersecurepassword1234";
        request.repeatedpassword = "supersecurepassword1234";

        mvc.perform(
                put(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void editPasswordNoNumberReturn4xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        // Passwords don't match
        EditPasswordRequest request = new EditPasswordRequest();

        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String editPassUrl = "/profiles/" + id + "/password";

        // Password dosent have number
        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "supersecurepassworD";
        request.repeatedpassword = "supersecurepassworD";

        mvc.perform(
                put(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void editPasswordSuccessCaseReturn2xxStatusCode() throws Exception {
        MockHttpSession session = new MockHttpSession();
        EditPasswordRequest request = new EditPasswordRequest();

        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String editPassUrl = "/profiles/" + id + "/password";

        request.oldpassword = "SuperSecurePassword123";
        request.newpassword = "SuperSecurePassword1234";
        request.repeatedpassword = "SuperSecurePassword1234";

        mvc.perform(
                put(editPassUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editPassports() throws Exception {
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d";
        updateUrl = String.format(updateUrl, id);

        // Edit only passports while keeping other fields same
        EditProfileRequest request = new EditProfileRequest();
        request.firstname = "John";
        request.middlename = "S";
        request.lastname = "Doe";
        request.nickname = "BigJ";
        request.bio = "Just another plain jane";
        request.primaryemail = "johndoe@uclive.ac.nz";
        request.dob = "2000-11-11";
        request.gender = "male";
        request.fitness = 0;
        request.passports = new ArrayList<>();
        request.passports.add("NZL");

        mvc.perform(
                put(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editEmails() throws Exception {
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d";
        updateUrl = String.format(updateUrl, id);

        // Sets Primary email and additional email while keeping other fields same
        EditProfileRequest request = new EditProfileRequest();
        request.firstname = "John";
        request.middlename = "S";
        request.lastname = "Doe";
        request.nickname = "BigJ";
        request.bio = "Just another plain jane";
        request.dob = "2000-11-11";
        request.gender = "male";
        request.fitness = 0;
        request.primaryemail = "valid@email.com";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworld@gmail.com");


        mvc.perform(
                put(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());

        // Bad primary
        request = new EditProfileRequest();
        request.primaryemail = "validemailcom";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworld@gmail.com");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());

        //  Bad Additional
        request = new EditProfileRequest();
        request.primaryemail = "valid@email.com";
        request.additionalemail = new ArrayList<String>();
        request.additionalemail.add("test@gmail.com");
        request.additionalemail.add("helloworldgmailcom");

        mvc.perform(
                patch(updateUrl)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void updateLocation() throws Exception{
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d/location";
        updateUrl = String.format(updateUrl, id);

        NamedLocation location = new NamedLocation();
        location.setCountry("New Zealand");
        location.setState("Canterbury");
        location.setCity("Christchurch");

        mvc.perform(
                put(updateUrl)
                        .content(mapper.writeValueAsString(location))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void deleteLocation() throws Exception{
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);

        String updateUrl = "/profiles/%d/location";
        updateUrl = String.format(updateUrl, id);

        mvc.perform(
                delete(updateUrl)
                        .session(session)
        ).andExpect(status().isOk());
    }
}