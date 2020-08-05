package com.springvuegradle.team6.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.requests.LoginRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
        "ADMIN_PASSWORD=test"})
public class LoginControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setup() throws Exception {
        MockHttpSession session = new MockHttpSession();
        TestDataGenerator.createJohnDoeUser(mvc, mapper, session);
    }

    @Test
    void loginTestEmptyDataFail() throws Exception {
        mvc.perform(
                post("/login/")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestValidEmailAndPasswordSuccess() throws Exception {
        LoginRequest loginRequestCorrectPass = new LoginRequest();
        loginRequestCorrectPass.email = "johndoe@uclive.ac.nz";
        loginRequestCorrectPass.password = "SuperSecurePassword123";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginRequestCorrectPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void loginTestIncorrectPasswordFail() throws Exception {
        LoginRequest loginRequestIncorrectPass = new LoginRequest();
        loginRequestIncorrectPass.email = "johndoe@uclive.ac.nz";
        loginRequestIncorrectPass.password = "hi";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginRequestIncorrectPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestNoSuchUserFail() throws Exception {
        LoginRequest loginRequestIncorrectUser = new LoginRequest();
        loginRequestIncorrectUser.email = "iDoNotExist@uclive.ac.nz";
        loginRequestIncorrectUser.password = "tester";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginRequestIncorrectUser))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestInvalidEmailFail() throws Exception {
        LoginRequest loginInvalidEmail = new LoginRequest();
        loginInvalidEmail.email = "invalidEmail.com";
        loginInvalidEmail.password = "tester";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginInvalidEmail))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestEmptyPasswordFail() throws Exception {
        LoginRequest loginEmptyPass = new LoginRequest();
        loginEmptyPass.email = "johndoe@uclive.ac.nz";
        loginEmptyPass.password = "";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginEmptyPass))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestEmptyEmailFail() throws Exception {
        LoginRequest loginEmptyUser = new LoginRequest();
        loginEmptyUser.email = "";
        loginEmptyUser.password = "SuperSecurePassword123";
        mvc.perform(
                post("/login/")
                        .content(mapper.writeValueAsString(loginEmptyUser))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError());
    }
}

