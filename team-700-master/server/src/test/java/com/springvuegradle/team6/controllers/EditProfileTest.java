package com.springvuegradle.team6.controllers;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class EditProfileTest {

    @Autowired
    private MockMvc mvc;

    private int id;

    private MockHttpSession session;

    @BeforeEach
    void setup() throws Exception {
        session = new MockHttpSession();
        String jsonString = "{\r\n  \"lastname\": \"Pocket\",\r\n  \"firstname\": \"Poly\",\r\n  \"middlename\": \"Michelle\",\r\n  \"nickname\": \"Pino\",\r\n  \"primary_email\": \"poly@pocket.com\",\r\n  \"password\": \"Password1\",\r\n  \"bio\": \"Poly Pocket is so tiny.\",\r\n  \"date_of_birth\": \"2000-11-11\",\r\n  \"gender\": \"female\"\r\n}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated()).andDo(print());
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

        String body = mvc.perform(
                get("/profiles/id").session(session)
        ).andReturn().getResponse().getContentAsString();
        id = Integer.parseInt(body);
    }

    @Test
    void editAllReturnStatusOK() throws Exception {
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
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void editMandatoryOnlyReturnStatusOK() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void emptyLastNameReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"\",\n" +
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
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void emptyFirstnameReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void emptyPrimaryEmailReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void primaryEmailInvalidReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"polygon.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void emptyDateOfBirthReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void dateOfBirthInvalidReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"asdfadsfff\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void emptyGenderReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void genderInvalidReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"asdf\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void fitnessLargerThan4ReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 5,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void fitnessLessThan0ReturnBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": -1,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void noLastNameReturnBadRequest() throws Exception {
        String jsonString="{\n" +
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
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void noFirstNameReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void noPrimaryEmailReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void noDateOfBirthReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"gender\": \"male\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void noGenderReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"fitness\": 4,\n" +
                "  \"passports\": [\n" +
                "    \"USA\"\n" +
                "  ]\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void updateActivityTypesReturnStatusOK() throws Exception {
        String updateActivityTypesUrl = "/profiles/{profileId}/activity-types";

        String jsonString = "{\n" +
                "\t\"activities\": [\"Hike\", \"Run\"]\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put(updateActivityTypesUrl, id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void updateActivityTypesReturnBadRequest() throws Exception {
        String updateActivityTypesUrl = "/profiles/{profileId}/activity-types";

        String jsonString = "{\n" +
                "\t\"activities\": [\"Hike\", \"Yoga\"]\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders
                .put(updateActivityTypesUrl, id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isBadRequest());

    }
}
