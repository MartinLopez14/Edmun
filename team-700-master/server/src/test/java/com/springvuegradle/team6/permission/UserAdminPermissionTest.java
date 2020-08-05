package com.springvuegradle.team6.permission;

import com.springvuegradle.team6.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAdminPermissionTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private ActivityRepository activityRepository;

    private String anotherUserId;

    private MockHttpSession session;

    private String activityId;

    @BeforeAll
    void createDummyUser() throws Exception {
        String jsonString = "{\n"
                + "  \"lastname\": \"Houston\",\n"
                + "  \"firstname\": \"Jacky\",\n"
                + "  \"middlename\": \"Hello\",\n"
                + "  \"nickname\": \"Pizza\",\n"
                + "  \"primary_email\": \"pizza@dominoes.com\",\n"
                + "\"additional_email\": [\n"
                + "    \"velvety@burger.com\",\n"
                + "    \"burger@fuuel.com\",\n"
                + "    \"macccass@kfc.com\"\n"
                + "  ],\n"
                + "  \"password\": \"Houston123\",\n"
                + "  \"bio\": \"This is a bio\",\n"
                + "  \"date_of_birth\": \"1999-08-08\",\n"
                + "  \"gender\": \"male\"\n"
                + "}";
        anotherUserId = mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @BeforeAll
    void createUserAdminAndLogInAsUserAdmin() throws Exception {
        profileRepository.deleteAll();
        session = new MockHttpSession();
        String jsonString = "{\n"
                + "  \"lastname\": \"Dallas\",\n"
                + "  \"firstname\": \"Joe\",\n"
                + "  \"middlename\": \"Exotic\",\n"
                + "  \"nickname\": \"Tiger\",\n"
                + "  \"primary_email\": \"poly@pocket.com\",\n"
                + "\"additional_email\": [\n"
                + "    \"velvety123@burger.com\",\n"
                + "    \"burger123@fuuel.com\",\n"
                + "    \"macccass123@kfc.com\"\n"
                + "  ],\n"
                + "  \"password\": \"Password1\",\n"
                + "  \"bio\": \"This is a tiger king bio\",\n"
                + "  \"date_of_birth\": \"1979-08-08\",\n"
                + "  \"gender\": \"male\"\n"
                + "}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .with(user("admin").roles("USER_ADMIN"))
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());

        String loginInfo = "{\n" +
                "\t\"email\" : \"poly@pocket.com\",\n" +
                "\t\"password\": \"Password1\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content(loginInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"USER", "USER_ADMIN"})
    void EditAnotherUserPrimaryEmailReturnCorrectUserIdWhenSearchByPrimaryEmail() throws Exception {
        String updateEmailsUrl = "/profiles/" + anotherUserId + "/emails";
        String jsonString = "{\n" +
                "  \"primary_email\": \"poly123@pocket.com\",\n" +
                "  \"additional_email\": [\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put(updateEmailsUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
        Optional<Email> emailObject = emailRepository.findByAddress("poly123@pocket.com");
        Profile userProfile = profileRepository.findByEmailsContains(emailObject.get());
        org.junit.jupiter.api.Assertions.assertEquals(anotherUserId, userProfile.getId().toString());
    }

    @Test
    @WithMockUser(roles = {"USER", "USER_ADMIN"})
    void EditAnotherUserAdditionalEmailReturnCorrectUserIdWhenSearchByAdditionalEmail() throws Exception {
        String updateEmailsUrl = "/profiles/" + anotherUserId + "/emails";
        String jsonString = "{\n" +
                "  \"primary_email\": \"poly123@pocket.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"carlsjr@burger.com\"\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put(updateEmailsUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
        Optional<Email> emailObject = emailRepository.findByAddress("carlsjr@burger.com");
        Profile anotherUserProfile = profileRepository.findByEmailsContains(emailObject.get());
        org.junit.jupiter.api.Assertions.assertEquals(anotherUserId, anotherUserProfile.getId().toString());
    }

    @Test
    @WithMockUser(roles = {"USER", "USER_ADMIN"})
    void ChangeAnotherUserPasswordWithOldPasswordReturnTrueWhenComparingAnotherUserCurrentPasswordAgainstNewPassword() throws Exception {
        String jsonString = "{\n" +
                "\t\"old_password\": \"Houston123\",\n" +
                "\t\"new_password\": \"Covid123\",\n" +
                "\t\"repeat_password\": \"Covid123\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/password", anotherUserId)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        Profile anotherUserProfile = profileRepository.findById(Integer.parseInt(anotherUserId));
        org.junit.jupiter.api.Assertions.assertTrue(anotherUserProfile.comparePassword("Covid123"));
    }

    @Test
    @WithMockUser(roles = {"USER", "USER_ADMIN"})
    void CreateActivityForAnotherUserReturnCorrectActivityIdWhenSearchForAnotherUserActivities() throws Exception {
        String jsonActivityString =
                "{\n"
                        + "  \"activity_name\": \"Kaikoura Coast Track race\",\n"
                        + "  \"activity_type\":[ \n"
                        + "    \"Walk\"\n"
                        + "  ],\n"
                        + "  \"continuous\": true\n"
                        + "}";
        activityId = mvc.perform(
                MockMvcRequestBuilders.post("/profiles/{profileId}/activities", anotherUserId)
                        .content(jsonActivityString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        List<Activity> anotherUserActivities = activityRepository.findByProfile_IdAndArchivedFalse(Integer.parseInt(anotherUserId));
        Assertions.assertEquals(activityId, anotherUserActivities.get(0).getId().toString());
    }


}
