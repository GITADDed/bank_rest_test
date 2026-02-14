package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        int page = 0;
        int size = 10;
        int totalCards = 20;

        ArrayList<Card> createdCards = createCards(testUser, totalCards);

        List<Integer> expectedIds = createdCards.stream()
                .map(Card::getId)
                .sorted(Comparator.reverseOrder())
                .limit(size)
                .map(Long::intValue)
                .toList();

        mockMvc.perform(get("/api/v1/admin/cards")
                        .param("page", "" + page)
                        .param("size", "" + size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(totalCards))
                .andExpect(jsonPath("$.items.length()").value(size))
                .andExpect(jsonPath("$.items[*].id", Matchers.contains(expectedIds.toArray())));
    }

    @Test
    void shouldCreateCardWhenUserExist() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), validPan, validExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);

        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
    void shouldReturnValidationErrorWhenPANIsNull() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), null, validExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
                            "message": "PAN must be not null or empty."
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
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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

    @Test
    void shouldReturnValidationErrorWhenExpiryMonthIsInvalid() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), validPan, invalidExpiryMonth, validExpiryYear);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
                            "message": "Expiry month must be between 1 and 12."
                        }
                    ]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnValidationErrorWhenExpiryYearIsNegative() throws Exception {
        CardRequest request = new CardRequest(testUser.getId(), validPan, validExpiryMonth, -1);

        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/admin/cards")
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
                            "message": "Expiry year must be positive."
                        }
                    ]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnUserCardsWhenRequestUserCards() throws Exception {
        int page = 0;
        int size = 10;
        int totalCards = 5;

        User otherUser = createUser("otheruser", "hashedpassword", Set.of(Role.USER));

        ArrayList<Card> createdCardsForTestUser = createCards(testUser, totalCards);
        ArrayList<Card> createdCardsForOtherUser = createCards(otherUser, totalCards);
        ArrayList<Card> createdCards = new ArrayList<>();

        createdCards.addAll(createdCardsForTestUser);
        createdCards.addAll(createdCardsForOtherUser);

        List<Integer> expectedIds = createdCardsForTestUser.stream()
                .map(Card::getId)
                .sorted(Comparator.reverseOrder())
                .map(Long::intValue)
                .toList();

        List<Integer> unexpectedIds = createdCardsForOtherUser.stream()
                .map(Card::getId)
                .sorted(Comparator.reverseOrder())
                .map(Long::intValue)
                .toList();

        mockMvc.perform(get("/api/v1/cards")
                        .header("X-User-Id", testUser.getId().toString())
                        .param("page", "" + page)
                        .param("size", "" + size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(totalCards))
                .andExpect(jsonPath("$.items.length()").value(totalCards))
                .andExpect(jsonPath("$.items[*].id", Matchers.contains(expectedIds.toArray())))
                .andExpect(jsonPath("$.items[*].id", Matchers.not(Matchers.contains(unexpectedIds.toArray()))));
    }

    @Test
    void shouldReturnCardWithChangedStatusWhenUpdateCardStatus() throws Exception {
        Card card = createCard(testUser, "1234", validExpiryMonth, validExpiryYear);

        String actualJson = mockMvc.perform(patch("/api/v1/admin/cards/" + card.getId() + "/status")
                        .content(objectMapper.writeValueAsString(CardStatus.INACTIVE))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                    "id": %d,
                    "maskedPan": "**** **** **** 1234",
                    "last4": "1234",
                    "expiryMonth": 10,
                    "expiryYear": 2028,
                    "status": "INACTIVE",
                    "balance": 0.00
                }
                """.formatted(card.getId());
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    private User createUser(String username, String passwordHash, Set<Role> roles) {
        return userRepository.save(new User(username, passwordHash, roles));
    }

    private Card createCard(User owner, String last4, Integer expiryMonth, Integer expiryYear) {
        return cardRepository.save(new Card(owner, last4, expiryMonth, expiryYear, CardStatus.ACTIVE, BigDecimal.ZERO));
    }

    private ArrayList<Card> createCards(User owner, int count) {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(createCard(owner, 1000 + i + "", validExpiryMonth, validExpiryYear));
        }
        return cards;
    }
}