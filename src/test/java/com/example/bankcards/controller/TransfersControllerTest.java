package com.example.bankcards.controller;


import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class TransfersControllerTest extends IntegrationTestBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JsonMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    TransferRepository transferRepository;

    private User user;
    private Card from;
    private Card to;

    @BeforeEach
    void setup() {

        transferRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();

        user = createUser("user1", "hash", Set.of(Role.USER));

        from = createCard(user,"pan1", "1111", LocalDate.now().getMonthValue(),
                LocalDate.now().getYear() + 1, new BigDecimal("100.00"));

        to = createCard(user, "pan2","2222", LocalDate.now().getMonthValue(),
                LocalDate.now().getYear() + 1, new BigDecimal("10.00"));
    }

    @AfterEach
    void tearDown() {
        transferRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnOkWhenValidInput() throws Exception {
        TransferRequest request =
                new TransferRequest(from.getId(), to.getId(), new BigDecimal("10.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .with(asUser(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Card fromAfter = cardRepository.findById(from.getId()).orElseThrow();
        Card toAfter = cardRepository.findById(to.getId()).orElseThrow();

        assertThat(fromAfter.getBalance()).isEqualByComparingTo("90.00");
        assertThat(toAfter.getBalance()).isEqualByComparingTo("20.00");
        assertThat(transferRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnConflictErrorWhenAmountBiggerThenSourceBalance() throws Exception {
        TransferRequest request =
                new TransferRequest(from.getId(), to.getId(), new BigDecimal("999.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .with(asUser(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(transferRepository.count()).isZero();

        Card fromAfter = cardRepository.findById(from.getId()).orElseThrow();
        assertThat(fromAfter.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void shouldReturnConflictErrorAndMarkCardExpiredWhenCardExpired() throws Exception {
        LocalDate now = LocalDate.now();
        from.setExpiryMonth(now.minusMonths(1).getMonthValue());
        from.setExpiryYear(now.minusMonths(1).getYear());
        from.setStatus(CardStatus.ACTIVE);
        cardRepository.save(from);

        TransferRequest request =
                new TransferRequest(from.getId(), to.getId(), new BigDecimal("10.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .with(asUser(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        Card updated = cardRepository.findById(from.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(CardStatus.EXPIRED);

        assertThat(transferRepository.count()).isZero();
    }

    @Test
    void shouldReturnUnauthorizedWhenWithoutJWT() throws Exception {
        TransferRequest request =
                new TransferRequest(from.getId(), to.getId(), new BigDecimal("10.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenJWTWithoutUidClaim() throws Exception {
        TransferRequest request =
                new TransferRequest(from.getId(), to.getId(), new BigDecimal("10.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private User createUser(String username, String passwordHash, Set<Role> roles) {
        return userRepository.save(new User(username, passwordHash, roles));
    }

    private Card createCard(User owner, String panHash, String last4, Integer expiryMonth, Integer expiryYear, BigDecimal balance) {
        return cardRepository.save(new Card(owner, panHash, last4, expiryMonth, expiryYear, CardStatus.ACTIVE, balance));
    }
    private RequestPostProcessor asUser(User user) {
        return jwt()
                .jwt(j -> j.subject(user.getUsername())
                        .claim("uid", user.getId()))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }
}