package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    private UserRequest request;

    private String validUsername = "user1";
    private String validPassword = "12345";

    @BeforeEach
    void setUp() {
        request = new UserRequest(validUsername, validPassword, Set.of(Role.USER));
    }

    @Test
    void shouldCreateUserWhenCorrectInput() throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/users")
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                "username": "user1",
                "role": ["USER"]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}