package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    UserRepository userRepository;

    private UserRequest request;

    private String validUsername = "user1";
    private String validPassword = "12345";

    private int page = 0;
    private int size = 10;
    private int totalUsers = 5;

    @BeforeEach
    void setUp() {
        request = new UserRequest(validUsername, validPassword, Set.of(Role.USER));
    }

    @Test
    void shouldCreateUserWhenCorrectInput() throws Exception {
        String requestJson = objectMapper.writeValueAsString(request);
        String actualJson = mockMvc.perform(post("/api/v1/admin/users")
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

    @Test
    void shouldReturnPageOfUsersWhenCorrectInput() throws Exception {
        List<User> createdUsers = createUsers(totalUsers);

        List<String> expectedUsernames = createdUsers.stream()
                .map(User::getUsername)
                .sorted(Comparator.reverseOrder()).toList();

        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "" + page)
                        .param("size", "" + size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(totalUsers))
                .andExpect(jsonPath("$.items.length()").value(totalUsers))
                .andExpect(jsonPath("$.items[*].username", Matchers.contains(expectedUsernames.toArray())));
    }

    @Test
    void shouldReturnUserByIdWhenCorrectInput() throws Exception {
        User createdUser = createUser(validUsername, validPassword, Set.of(Role.USER));

        String actualJson = mockMvc.perform(get("/api/v1/admin/users/{id}", createdUser.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
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

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/{id}", 999L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0].field").value("id"))
                .andExpect(jsonPath("$.details[0].message").value("User with id 999 not found."));
    }

    @Test
    void shouldUpdateUserRoleWhenCorrectInput() throws Exception {
        User createdUser = createUser(validUsername, validPassword, Set.of(Role.USER));

        String requestJson = """
                {
                "roles": ["ADMIN"]
                }
                """;

        String actualJson = mockMvc.perform(patch("/api/v1/admin/users/{id}/roles", createdUser.getId())
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedJson = """
                {
                "username": "user1",
                "role": ["ADMIN"]
                }
                """;
        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldReturnErrorWhenUpdateUserRoleWithUserDoesNotExist() throws Exception {
        String requestJson = """
                {
                "roles": ["ADMIN"]
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/users/{id}/roles", 999L)
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0].field").value("id"))
                .andExpect(jsonPath("$.details[0].message").value("User with id 999 not found."));
    }

    @Test
    void shouldReturnErrorWhenUpdateUserRoleWithInvalidRole() throws Exception {
        User createdUser = createUser(validUsername, validPassword, Set.of(Role.USER));

        String requestJson = """
                {
                "roles": ["NON_EXISTING_ROLE"]
                }
                """;

        mockMvc.perform(patch("/api/v1/admin/users/{id}/roles", createdUser.getId())
                        .content(requestJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserWhenCorrectInput() throws Exception {
        User createdUser = createUser(validUsername, validPassword, Set.of(Role.USER));

        mockMvc.perform(delete("/api/v1/admin/users/{id}", createdUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteUserWithUserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0].field").value("id"))
                .andExpect(jsonPath("$.details[0].message").value("User with id 999 not found."));
    }

    @Test
    void shouldReturnNotFoundWhenDeleteUserWithAlreadyDeletedUser() throws Exception {
        User createdUser = createDeletedUser(validUsername, validPassword, Set.of(Role.USER));

        mockMvc.perform(delete("/api/v1/admin/users/{id}", createdUser.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0].field").value("id"))
                .andExpect(jsonPath("$.details[0].message").value("User with id " + createdUser.getId() + " not found."));
    }

    private User createUser(String username, String password, Set<Role> roles) {
        return userRepository.save(new User(username, password, roles));
    }

    private User createDeletedUser(String username, String password, Set<Role> roles) {
        User user = new User(username, password, roles);
        user.setDeleted(true);
        return userRepository.save(user);
    }

    private ArrayList<User> createUsers(int count) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = createUser("user" + i, "12345", Set.of(Role.USER));
            users.add(user);
        }
        return users;
    }
}