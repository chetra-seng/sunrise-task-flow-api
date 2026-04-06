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
class ProjectControllerTest extends BaseControllerTest {

  private static final String BASE_URL = "/api/projects";

  @Nested
  @DisplayName("Exercise 2: Project CRUD")
  class ProjectCrud {

    @Test
    @DisplayName("GET /api/projects → returns all 3 projects with taskCount")
    void getAllProjects_returns3() throws Exception {
      mockMvc.perform(get(BASE_URL))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(3)))
              .andExpect(jsonPath("$[0].taskCount").isNumber())
              .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("GET /api/projects/{id} → returns project by ID with taskCount")
    void getProjectById_existing_returns200() throws Exception {
      mockMvc.perform(get(BASE_URL + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1))
              .andExpect(jsonPath("$.name").value("Task Management System"))
              .andExpect(jsonPath("$.taskCount").value(4));
    }

    @Test
    @DisplayName("GET /api/projects/{id} → 404 for non-existing project")
    void getProjectById_nonExisting_returns404() throws Exception {
      mockMvc.perform(get(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/projects → creates project with 201")
    void createProject_returns201() throws Exception {
      mockMvc.perform(post(BASE_URL)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"New Project"}
                                    """))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").exists())
              .andExpect(jsonPath("$.name").value("New Project"))
              .andExpect(jsonPath("$.taskCount").value(0));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} → updates existing project")
    void updateProject_existing_returns200() throws Exception {
      mockMvc.perform(put(BASE_URL + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"Renamed Project"}
                                    """))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("Renamed Project"));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} → 404 for non-existing project")
    void updateProject_nonExisting_returns404() throws Exception {
      mockMvc.perform(put(BASE_URL + "/9999")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"name":"Ghost"}
                                    """))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} → deletes with 204")
    void deleteProject_existing_returns204() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/3"))
              .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} → 404 for non-existing project")
    void deleteProject_nonExisting_returns404() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Exercise 2: Project Tasks")
  class ProjectTasks {

    @Test
    @DisplayName("GET /api/projects/{id}/tasks → returns 4 tasks for project 1")
    void getProjectTasks_project1_returns4Tasks() throws Exception {
      mockMvc.perform(get(BASE_URL + "/1/tasks"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @DisplayName("GET /api/projects/{id}/tasks → returns 4 tasks for project 2")
    void getProjectTasks_project2_returns4Tasks() throws Exception {
      mockMvc.perform(get(BASE_URL + "/2/tasks"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @DisplayName("GET /api/projects/{id}/tasks → 404 for non-existing project")
    void getProjectTasks_nonExisting_returns404() throws Exception {
      mockMvc.perform(get(BASE_URL + "/9999/tasks"))
              .andExpect(status().isNotFound());
    }
  }
}