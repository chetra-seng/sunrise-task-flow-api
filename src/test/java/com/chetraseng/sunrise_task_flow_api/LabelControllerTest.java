package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class LabelControllerTest extends BaseControllerTest {

  private static final String BASE_URL = "/api/labels";

  @Nested
  @DisplayName("Exercise 5: Label CRUD")
  class LabelCrud {

    @Test
    @DisplayName("GET /api/labels → returns all 6 labels")
    void getAllLabels_returns6() throws Exception {
      mockMvc.perform(get(BASE_URL))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("GET /api/labels/{id} → returns label by ID")
    void getLabelById_existing_returns200() throws Exception {
      mockMvc.perform(get(BASE_URL + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1))
              .andExpect(jsonPath("$.name").value("bug"))
              .andExpect(jsonPath("$.color").value("#FF0000"));
    }

    @Test
    @DisplayName("GET /api/labels/{id} → 404 for non-existing label")
    void getLabelById_nonExisting_returns404() throws Exception {
      mockMvc.perform(get(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/labels → creates label with 201")
    void createLabel_returns201() throws Exception {
      mockMvc.perform(post(BASE_URL)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"refactor","color":"#AABBCC"}
                                    """))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").exists())
              .andExpect(jsonPath("$.name").value("refactor"))
              .andExpect(jsonPath("$.color").value("#AABBCC"));
    }

    @Test
    @DisplayName("PUT /api/labels/{id} → updates existing label")
    void updateLabel_existing_returns200() throws Exception {
      mockMvc.perform(put(BASE_URL + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"critical-bug","color":"#CC0000"}
                                    """))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("critical-bug"))
              .andExpect(jsonPath("$.color").value("#CC0000"));
    }

    @Test
    @DisplayName("PUT /api/labels/{id} → 404 for non-existing label")
    void updateLabel_nonExisting_returns404() throws Exception {
      mockMvc.perform(put(BASE_URL + "/9999")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"ghost","color":"#000000"}
                                    """))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/labels/{id} → deletes with 204")
    void deleteLabel_existing_returns204() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/6"))
              .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/labels/{id} → 404 for non-existing label")
    void deleteLabel_nonExisting_returns404() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Exercise 5: Label Tasks")
  class LabelTasks {

    @Test
    @DisplayName("GET /api/labels/{id}/tasks → returns 1 task with 'bug' label")
    void getTasksByLabel_bug_returns1() throws Exception {
      mockMvc.perform(get(BASE_URL + "/1/tasks"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/labels/{id}/tasks → returns 6 tasks with 'backend' label")
    void getTasksByLabel_backend_returns6() throws Exception {
      mockMvc.perform(get(BASE_URL + "/4/tasks"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("GET /api/labels/{id}/tasks → returns 5 tasks with 'frontend' label")
    void getTasksByLabel_frontend_returns5() throws Exception {
      mockMvc.perform(get(BASE_URL + "/5/tasks"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/labels/{id}/tasks → 404 for non-existing label")
    void getTasksByLabel_nonExisting_returns404() throws Exception {
      mockMvc.perform(get(BASE_URL + "/9999/tasks"))
              .andExpect(status().isNotFound());
    }
  }
}