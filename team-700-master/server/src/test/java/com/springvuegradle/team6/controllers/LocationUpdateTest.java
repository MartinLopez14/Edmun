package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class LocationUpdateTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    
    @Test
    void testEditProfileLocationUpdate() throws Exception {
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ],\n" +
                "  \"location\": {\n" +
                "    \"country\": \"NZ\",\n" +
                "    \"state\": \"Canterbury\",\n" +
                "    \"city\": \"Christchurch\"\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ],\n" +
                "  \"location\": {\n" +
                "    \"country\": \"NZ\",\n" +
                "    \"city\": \"Christchurch\"\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void updateLocation() throws Exception {
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

       String  jsonString ="{\n" +
                "    \"country\": \"NZ\",\n" +
                "    \"state\": \"Canterbury\",\n" +
                "    \"city\": \"Christchurch\"\n" +
               "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        jsonString ="{\n" +
                "    \"country\": \"NZ\",\n" +
                "    \"city\": \"Christchurch\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void deleteLocation() throws Exception {
        MockHttpSession session = new MockHttpSession();
        int id = TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
        TestDataGenerator.loginJohnDoeUser(mvc, mapper, session);

        String  jsonString ="{\n" +
                "    \"country\": \"NZ\",\n" +
                "    \"state\": \"Canterbury\",\n" +
                "    \"city\": \"Christchurch\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/location", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());

        // Test delete
        mvc.perform(MockMvcRequestBuilders
                .delete("/profiles/{profileId}/location", id)
                .session(session)
        ).andExpect(status().isOk());
    }
}
