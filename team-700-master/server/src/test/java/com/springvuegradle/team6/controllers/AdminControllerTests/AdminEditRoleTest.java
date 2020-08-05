package com.springvuegradle.team6.controllers.AdminControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class AdminEditRoleTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private RoleRepository roleRepository;
    private int userId;
    private MockHttpSession session;
    private boolean dataLoaded = false;


    @BeforeEach
    void createTestUser() throws Exception {
        if (!dataLoaded) {
            profileRepository.deleteAll();
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
            userId = Integer.parseInt(body);

            mvc.perform(MockMvcRequestBuilders
                    .get("/logout/")
                    .session(session)
            ).andExpect(status().isOk());
            dataLoaded = true;
        }
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void addRoleAsAdminReturnStatusOk() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"ROLE_ADMIN\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + userId + "/role";

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void addRoleAsUserReturnStatusForbidden() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"ROLE_ADMIN\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + userId + "/role";

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void addNonValidRoleReturnStatusBadRequest() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"notAValidRole\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + userId + "/role";

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void addRoleToNonValidUserReturnStatusNotFound() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"ROLE_USER\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + 999 + "/role";

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void removeRoleAsAdminReturnStatusOk() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"ROLE_USER\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + userId + "/role";

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@admin.com", roles = {"ADMIN"})
    void removeAdminRoleAsAdminReturnStatusForbidden() throws Exception {
        String jsonString =
                "{\n"
                        + "  \"role\": \"ROLE_ADMIN\"\n"
                        + "}";
        String editRoleUrl = "/admin/profiles/" + userId + "/role";

        Profile profile = profileRepository.findById(userId);
        profile.addRole(roleRepository.findByName("ROLE_ADMIN"));
        profileRepository.save(profile);

        mvc.perform(MockMvcRequestBuilders
                .put(editRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden());
    }
}
