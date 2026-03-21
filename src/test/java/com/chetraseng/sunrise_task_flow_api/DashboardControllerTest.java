package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(roles = "ADMIN")
class DashboardControllerTest {

  @Autowired private MockMvc mockMvc;

  // ═════════════════════════════════════════════════════════════════════════
  // Exercise 3: Dashboard Summary (Projections)
  // ═════════════════════════════════════════════════════════════════════════

  @Test
  @DisplayName("GET /api/dashboard/summary → returns correct task counts")
  void getDashboardSummary_returnsCorrectCounts() throws Exception {
    mockMvc
        .perform(get("/api/dashboard/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalTasks").value(12))
        .andExpect(jsonPath("$.todoCount").value(5))
        .andExpect(jsonPath("$.inProgressCount").value(3))
        .andExpect(jsonPath("$.doneCount").value(4))
        .andExpect(jsonPath("$.overdueCount").value(3));
  }

  @Test
  @DisplayName("GET /api/dashboard/summary → projectStats is an array of 3 projects")
  void getDashboardSummary_projectStatsHasAllProjects() throws Exception {
    mockMvc
        .perform(get("/api/dashboard/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.projectStats").isArray())
        .andExpect(jsonPath("$.projectStats", hasSize(3)));
  }

  @Test
  @DisplayName("GET /api/dashboard/summary → each project stat has projectName, taskCount, doneCount")
  void getDashboardSummary_projectStatsHaveCorrectFields() throws Exception {
    mockMvc
        .perform(get("/api/dashboard/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.projectStats[0].projectName").exists())
        .andExpect(jsonPath("$.projectStats[0].taskCount").isNumber())
        .andExpect(jsonPath("$.projectStats[0].doneCount").isNumber());
  }
}
