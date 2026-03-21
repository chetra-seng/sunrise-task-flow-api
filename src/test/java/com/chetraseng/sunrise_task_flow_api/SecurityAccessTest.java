package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    // ═══════════════════════════════════════════════════════════════════
    // Exercise 9: 401 without authentication
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Exercise 9: Unauthenticated requests → 401")
    class Unauthenticated {

        @Test
        @DisplayName("GET /api/tasks without token → 401")
        void getTasks_noToken_returns401() throws Exception {
            mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/tasks without token → 401")
        void createTask_noToken_returns401() throws Exception {
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"No Auth Task\"}"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/projects without token → 401")
        void getProjects_noToken_returns401() throws Exception {
            mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/dashboard/summary without token → 401")
        void getDashboard_noToken_returns401() throws Exception {
            mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("401 response body has status=401 and message field")
        void unauthenticated_errorResponseHasCorrectShape() throws Exception {
            mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Exercise 11: USER role — read access, blocked from writes + dashboard
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Exercise 11: USER role access")
    class UserRole {

        @Test
        @DisplayName("USER can GET /api/tasks → 200")
        @WithMockUser(roles = "USER")
        void userCanViewTasks_returns200() throws Exception {
            mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("USER can GET /api/projects → 200")
        @WithMockUser(roles = "USER")
        void userCanViewProjects_returns200() throws Exception {
            mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("USER can GET /api/labels → 200")
        @WithMockUser(roles = "USER")
        void userCanViewLabels_returns200() throws Exception {
            mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("USER cannot POST /api/tasks → 403")
        @WithMockUser(roles = "USER")
        void userCannotCreateTask_returns403() throws Exception {
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Forbidden Task\",\"description\":\"Should fail\"}"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("USER cannot PUT /api/tasks/{id} → 403")
        @WithMockUser(roles = "USER")
        void userCannotUpdateTask_returns403() throws Exception {
            mockMvc.perform(put("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Forbidden Update\"}"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("USER cannot DELETE /api/tasks/{id} → 403")
        @WithMockUser(roles = "USER")
        void userCannotDeleteTask_returns403() throws Exception {
            mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("USER cannot GET /api/dashboard/summary → 403")
        @WithMockUser(roles = "USER")
        void userCannotViewDashboard_returns403() throws Exception {
            mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("USER cannot POST /api/projects → 403")
        @WithMockUser(roles = "USER")
        void userCannotCreateProject_returns403() throws Exception {
            mockMvc.perform(post("/api/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Forbidden Project\"}"))
                .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // Exercise 11: ADMIN role — full access
    // ═══════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Exercise 11: ADMIN role access")
    class AdminRole {

        @Test
        @DisplayName("ADMIN can GET /api/tasks → 200")
        @WithMockUser(roles = "ADMIN")
        void adminCanViewTasks_returns200() throws Exception {
            mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("ADMIN can POST /api/tasks → 201")
        @WithMockUser(roles = "ADMIN")
        void adminCanCreateTask_returns201() throws Exception {
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"title":"Admin Task","description":"Created by admin","priority":"HIGH"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Admin Task"));
        }

        @Test
        @DisplayName("ADMIN can GET /api/dashboard/summary → 200")
        @WithMockUser(roles = "ADMIN")
        void adminCanViewDashboard_returns200() throws Exception {
            mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").isNumber());
        }

        @Test
        @DisplayName("ADMIN can DELETE /api/projects/{id} → 204")
        @WithMockUser(roles = "ADMIN")
        void adminCanDeleteProject_returns204() throws Exception {
            mockMvc.perform(delete("/api/projects/3"))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("ADMIN can PUT /api/tasks/{id} → 200")
        @WithMockUser(roles = "ADMIN")
        void adminCanUpdateTask_returns200() throws Exception {
            mockMvc.perform(put("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Admin Updated\",\"description\":\"Updated by admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated"));
        }
    }
}
