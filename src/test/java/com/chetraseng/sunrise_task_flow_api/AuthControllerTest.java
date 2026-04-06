package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    private static final String VALID_REGISTER_BODY = """
            {
                "email": "alice@example.com",
                "password": "password123",
                "firstName": "Alice",
                "lastName": "Smith"
            }
            """;

    @Nested
    @DisplayName("Exercise 9: POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("valid request → 201 with token, email, and role=USER")
        void register_validRequest_returns201WithToken() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.expiresIn").isNumber())
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("duplicate email → 409 Conflict")
        void register_duplicateEmail_returns409() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isCreated());

            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("missing email → 400 with field error")
        void register_missingEmail_returns400() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "password": "password123",
                                "firstName": "Alice",
                                "lastName": "Smith"
                            }
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
        }

        @Test
        @DisplayName("invalid email format → 400")
        void register_invalidEmailFormat_returns400() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "email": "not-an-email",
                                "password": "password123",
                                "firstName": "Alice",
                                "lastName": "Smith"
                            }
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
        }

        @Test
        @DisplayName("password too short (< 8 chars) → 400 with field error")
        void register_shortPassword_returns400() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "email": "alice@example.com",
                                "password": "abc",
                                "firstName": "Alice",
                                "lastName": "Smith"
                            }
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[*].field", hasItem("password")));
        }

        @Test
        @DisplayName("blank firstName → 400 with field error")
        void register_blankFirstName_returns400() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "email": "alice@example.com",
                                "password": "password123",
                                "firstName": "",
                                "lastName": "Smith"
                            }
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[*].field", hasItem("firstName")));
        }
    }

    @Nested
    @DisplayName("Exercise 9: POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("valid credentials → 200 with token and refreshToken")
        void login_validCredentials_returns200WithToken() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isCreated());

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {"email": "alice@example.com", "password": "password123"}
                            """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.expiresIn").isNumber())
                    .andExpect(jsonPath("$.email").value("alice@example.com"));
        }

        @Test
        @DisplayName("wrong password → 401")
        void login_wrongPassword_returns401() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isCreated());

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {"email": "alice@example.com", "password": "wrongpassword"}
                            """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("non-existent user → 401")
        void login_nonExistentUser_returns401() throws Exception {
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {"email": "nobody@example.com", "password": "password123"}
                            """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("missing email → 400")
        void login_missingEmail_returns400() throws Exception {
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {"password": "password123"}
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
        }

        @Test
        @DisplayName("login token can access protected endpoint")
        void login_tokenCanAccessProtectedEndpoint() throws Exception {
            mockMvc.perform(post(REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_REGISTER_BODY))
                    .andExpect(status().isCreated());

            MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {"email": "alice@example.com", "password": "password123"}
                            """))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResult.getResponse().getContentAsString();
            String token = com.jayway.jsonpath.JsonPath.read(responseBody, "$.token");

            mockMvc.perform(get("/api/tasks")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }
}