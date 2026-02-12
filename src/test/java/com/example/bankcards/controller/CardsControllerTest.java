package com.example.bankcards.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CardsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCards() {
    }

    @Test
    void createCard() throws Exception {
        String requestJson = """
                {
                    "ownerId": 1,
                    "pan": "1234567890123456",
                    "expiryMonth": 10,
                    "expiryYear": 2028
                }
                """;
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
}