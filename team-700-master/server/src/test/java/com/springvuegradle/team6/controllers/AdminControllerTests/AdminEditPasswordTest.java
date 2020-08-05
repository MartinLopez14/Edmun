package com.springvuegradle.team6.controllers.AdminControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class AdminEditPasswordTest {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MockMvc mvc;

    private int id;

    @Autowired
    private ObjectMapper mapper;

    private MockHttpSession session;


    @BeforeEach
    void createJohnDoeUser() throws Exception {
        session = new MockHttpSession();

        CreateProfileRequest validRequest = new CreateProfileRequest();
        String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly1@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());

        String body = mvc.perform(
                get("/profiles/id").session(session)
        ).andReturn().getResponse().getContentAsString();
        id = Integer.parseInt(body);

        mvc.perform(MockMvcRequestBuilders
                .get("/logout/")
                .session(session)
        ).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void editUserPasswordAsAdminReturnStatusOk() throws Exception {
        String editPasswordUrl = "/admin/profiles/"+id+"/password";

        String jsonString =
                "{\n"
                        + "  \"new_password\": \"mynewpwdA1\",\n"
                        + "  \"repeat_password\": \"mynewpwdA1\"\n"
                        + "}";

        mvc.perform(MockMvcRequestBuilders
                .put(editPasswordUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Profile profile = profileRepository.findById(id);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(passwordMatch);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void editUserPasswordPasswordDoNotMatchAsAdminReturnStatusBadRequest() throws Exception {
        String editPasswordUrl = "/admin/profiles/"+id+"/password";

        String jsonString =
                "{\n"
                        + "  \"new_password\": \"mynewpwdA12\",\n"
                        + "  \"repeat_password\": \"mynewpwdA1\"\n"
                        + "}";

        mvc.perform(MockMvcRequestBuilders
                .put(editPasswordUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        Profile profile = profileRepository.findById(id);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
        org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
    }

    @Test
    void editUserPasswordNotLoggedInReturnStatusForbidden() throws Exception {
        String editPasswordUrl = "/admin/profiles/"+id+"/password";

        String jsonString =
                "{\n"
                        + "  \"new_password\": \"mynewpwdA12\",\n"
                        + "  \"repeat_password\": \"mynewpwdA1\"\n"
                        + "}";

        mvc.perform(MockMvcRequestBuilders
                .put(editPasswordUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

        Profile profile = profileRepository.findById(id);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
        org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void editUserPasswordUserDoesNotExistInReturnStatusNotFound() throws Exception {
        String editPasswordUrl = "/admin/profiles/"+9999+"/password";

        String jsonString =
                "{\n"
                        + "  \"new_password\": \"mynewpwdA12\",\n"
                        + "  \"repeat_password\": \"mynewpwdA1\"\n"
                        + "}";

        mvc.perform(MockMvcRequestBuilders
                .put(editPasswordUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "poly@pocket.com")
    void editUserPasswordAsUserReturnsStatusForbidden() throws Exception {
        String editPasswordUrl = "/admin/profiles/"+id+"/password";

        String jsonString =
                "{\n"
                        + "  \"new_password\": \"mynewpwdA1\",\n"
                        + "  \"repeat_password\": \"mynewpwdA1\"\n"
                        + "}";

        mvc.perform(MockMvcRequestBuilders
                .put(editPasswordUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());

        Profile profile = profileRepository.findById(id);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatch = passwordEncoder.matches("mynewpwdA1", profile.getPassword());
        org.junit.jupiter.api.Assertions.assertFalse(passwordMatch);
    }
}
