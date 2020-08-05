package com.springvuegradle.team6.controllers.AdminControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.requests.CreateProfileRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.json.JSONArray;
import org.json.JSONObject;



@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper mapper;

    private int id;

    @BeforeEach
    void createJohnDoeUser() throws Exception {
        profileRepository.deleteAll();
        CreateProfileRequest validRequest = new CreateProfileRequest();
        String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly1@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        String result = mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        id = Integer.parseInt(result);
    }

    @AfterEach
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteProfileWithValidEmail() throws Exception {

        String deleteProfileUrl = "/admin/profiles";

        //Delete existing primary email
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteProfileUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @AfterEach
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteProfileWithInvalidEmail() throws Exception {

        String deleteProfileUrl = "/admin/profiles";

        // Incorrect email associated to user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"incorrect@gmail.com\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteProfileUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addNewRoleToExistingUser() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }



    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addNewRoleToNonExistingUser() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        // Add new role to non-existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"user@doesnotexist.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());


    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addDuplicateRoleToExistingUser() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        // Add duplicate role to existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addInvalidRoleToExistingUser() throws Exception {
        String addRoleUrl = "/admin/profiles/role";

        // Add invalid role to existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_INVALID\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .post(addRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteExistingRoleFromExistingUser() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete existing role from existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteNonExistingRoleFromExistingUser() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete non-existing role from existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_ADMIN\"\n" +
                "}";


        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteInvalidRoleFromExistingUser() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete invalid role
        String jsonString = "{\n" +
                "\t\"primary_email\": \"poly1@pocket.com\",\n" +
                "\t\"role_name\": \"ROLE_INVALID\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteValidRoleFromNonExistingUser() throws Exception {
        String deleteRoleUrl = "/admin/profiles/role";

        // Delete role from non=existing user
        String jsonString = "{\n" +
                "\t\"primary_email\": \"incorrect@gmail.com\",\n" +
                "\t\"role_name\": \"ROLE_USER\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .delete(deleteRoleUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getRoleOfExistingUser() throws Exception {
        String getRoleUrl = "/admin/role/" + id;
        mvc.perform(MockMvcRequestBuilders
                .get(getRoleUrl)
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getRoleOfNotExistingUser() throws Exception {
        String getRoleUrl = "/admin/role/12342342345235434765426468732764367675676745";
        mvc.perform(MockMvcRequestBuilders
                .get(getRoleUrl)
        ).andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void checkCorrectRoleIsReturned() throws Exception {
        String getRoleUrl = "/admin/role/" + id;
        String response =
            mvc.perform(MockMvcRequestBuilders
                    .get(getRoleUrl)
            ).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        String expectedResult = "[{\"roleName\":\"ROLE_USER\"}]";
        org.junit.jupiter.api.Assertions.assertEquals(expectedResult, response);
    }

}
