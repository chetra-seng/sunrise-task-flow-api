package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityAccessTest extends BaseControllerTest {

    // ── Unauthenticated → 401 ────────────────────────────────────────────────

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

    // ── USER role ────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER can GET /api/tasks → 200")
    void userCanViewTasks_returns200() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER can GET /api/projects → 200")
    void userCanViewProjects_returns200() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER can GET /api/labels → 200")
    void userCanViewLabels_returns200() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER cannot POST /api/tasks → 403")
    void userCannotCreateTask_returns403() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Forbidden Task\",\"description\":\"Should fail\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER cannot PUT /api/tasks/{id} → 403")
    void userCannotUpdateTask_returns403() throws Exception {
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Forbidden Update\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER cannot DELETE /api/tasks/{id} → 403")
    void userCannotDeleteTask_returns403() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER cannot GET /api/dashboard/summary → 403")
    void userCannotViewDashboard_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER cannot POST /api/projects → 403")
    void userCannotCreateProject_returns403() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Forbidden Project\"}"))
                .andExpect(status().isForbidden());
    }

    // ── ADMIN role ───────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN can GET /api/tasks → 200")
    void adminCanViewTasks_returns200() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN can POST /api/tasks → 201")
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN can GET /api/dashboard/summary → 200")
    void adminCanViewDashboard_returns200() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN can DELETE /api/projects/{id} → 204")
    void adminCanDeleteProject_returns204() throws Exception {
        mockMvc.perform(delete("/api/projects/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN can PUT /api/tasks/{id} → 200")
    void adminCanUpdateTask_returns200() throws Exception {
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Admin Updated\",\"description\":\"Updated by admin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated"));
    }
}