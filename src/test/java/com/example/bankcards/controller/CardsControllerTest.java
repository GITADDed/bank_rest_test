package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private CardRepository cardRepository;

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
    void shouldGetTenCardsWhenPageZeroAndSizeTen() throws Exception {
        ArrayList<Card> createdCards = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            createdCards.add(createCard(testUser, 4000 + i + "", validExpiryMonth, validExpiryYear));
        }

        List<Integer> expectedIds = createdCards.stream()
                .map(Card::getId)
                .sorted(Comparator.reverseOrder())
                .limit(10)
                .map(Long::intValue)
                .toList();

        mockMvc.perform(get("/api/v1/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(20))
                .andExpect(jsonPath("$.items.length()").value(10))
                .andExpect(jsonPath("$.items[*].id", Matchers.contains(expectedIds.toArray())));
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
    void shouldReturnValidationErrorWhenExpiryYearIsCurrentButMonthInPast() throws Exception {
        int currentYear = java.time.LocalDate.now().getYear();
        int pastMonth = java.time.LocalDate.now().getMonthValue() - 1;

        CardRequest request = new CardRequest(testUser.getId(), validPan, pastMonth, currentYear);

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
                            "field": "expiryMonth",
                            "message": "Expiry month must be the current month or a future month."
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

    private Card createCard(User owner, String last4, Integer expiryMonth, Integer expiryYear) {
        return cardRepository.save(new Card(owner, last4, expiryMonth, expiryYear, CardStatus.ACTIVE, BigDecimal.ZERO));
    }
}