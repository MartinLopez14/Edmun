package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ProfileRepository;
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
public class EditEmailsTest {

    @Autowired
    private ProfileRepository profileRepository;

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
    void editPrimaryEmailReturnStatusOk() throws Exception {
        String jsonString="{\n" +
                "  \"primary_email\": \"triplej@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/emails", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void noPrimaryEmailReturnStatusBadRequest() throws Exception {
        String jsonString="{\n" +
                "  \"additional_email\": [\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/emails", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void addAdditionalEmailsReturnStatusOK() throws Exception {
        String jsonString="{\n" +
                "  \"primary_email\": \"triplej@google.com\",\n" +
                "  \"additional_email\": [\n" +
                "    \"triplej@xtra.co.nz\",\n" +
                "    \"triplej@msn.com\",\n" +
                "    \"tnt@tntc.ca\"\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put("/profiles/{profileId}/emails", id)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }
}
