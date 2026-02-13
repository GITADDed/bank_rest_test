package com.example.bankcards.controller;

import com.example.bankcards.BankCardsApplication;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.HttpHeaders;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class CardsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper objectMapper;

    private User testUser;

    private String validPan = "1234567890123456";
    private String invalidPan = "invalid_pan";

    private int validExpiryMonth = 10;
    private int validExpiryYear = 2028;
    private int invalidExpiryMonth = 13;
    private int pastExpiryYear = 2020;

    @BeforeEach
    void setUp() {
        testUser = createUser("testuser", "hashedpassword", Set.of(Role.USER));
    }

    @Test
    void getCards() {
    }

    @Test
    void shouldCreateCardWhenUserExist() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), validPan, validExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);

        String actualJson = mockMvc.perform(post("/api/v1/cards")
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                "maskedPan": "**** **** **** 3456",
                "last4": "3456",
                "expiryMonth": 10,
                "expiryYear": 2028,
                "status": "ACTIVE",
                "balance": 0.00
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnValidationErrorWhenPANIsInvalid() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), invalidPan, validExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/cards")
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                    "code": "VALIDATION_ERROR",
                    "message": "Validation failed for one or more fields.",
                    "details": [
                        {
                            "field": "pan",
                            "message": "PAN must be a 16-digit number."
                        }
                    ]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnValidationErrorWhenExpiryDateInPast() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), validPan, validExpiryMonth, pastExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/cards")
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                    "code": "VALIDATION_ERROR",
                    "message": "Validation failed for one or more fields.",
                    "details": [
                        {
                            "field": "expiryYear",
                            "message": "Expiry year must be the current year or a future year."
                        }
                    ]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        CardRequest request = new CardRequest(999L, validPan, validExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/cards")
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                    "code": "NOT_FOUND_ERROR",
                    "message": "Not found some resource.",
                    "details": [
                        {
                            "field": "ownerId",
                            "message": "User with id 999 not found."
                        }
                    ]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    private User createUser(String username, String passwordHash, Set<Role> roles) {
        return userRepository.save(new User(username, passwordHash, roles));
    }
}