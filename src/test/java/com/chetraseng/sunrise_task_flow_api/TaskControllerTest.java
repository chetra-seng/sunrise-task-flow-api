package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class TaskControllerTest extends BaseControllerTest {

  private static final String BASE_URL = "/api/tasks";

  @Nested
  @DisplayName("Exercise 1: Task CRUD")
  class TaskCrud {

    @Test
    @DisplayName("GET /api/tasks → returns all 12 seeded tasks")
    void getAllTasks_returns12Tasks() throws Exception {
      mockMvc.perform(get(BASE_URL))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(12)));
    }

    @Test
    @DisplayName("GET /api/tasks → response includes status, priority, labelNames, commentCount")
    void getAllTasks_responseHasNewFields() throws Exception {
      mockMvc.perform(get(BASE_URL))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].status").exists())
              .andExpect(jsonPath("$[0].priority").exists())
              .andExpect(jsonPath("$[0].labelNames").exists())
              .andExpect(jsonPath("$[0].commentCount").isNumber());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} → returns task by ID with correct fields")
    void getTaskById_existingTask_returns200() throws Exception {
      mockMvc.perform(get(BASE_URL + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1))
              .andExpect(jsonPath("$.title").value("Design login page UI"))
              .andExpect(jsonPath("$.status").value("DONE"))
              .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} → 404 for non-existing task")
    void getTaskById_nonExisting_returns404() throws Exception {
      mockMvc.perform(get(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks → creates task with 201")
    void createTask_returns201() throws Exception {
      mockMvc.perform(post(BASE_URL)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"title":"New Task","description":"A new task","priority":"HIGH","status":"TODO"}
                                    """))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").exists())
              .andExpect(jsonPath("$.title").value("New Task"))
              .andExpect(jsonPath("$.priority").value("HIGH"))
              .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} → updates existing task")
    void updateTask_existingTask_returns200() throws Exception {
      mockMvc.perform(put(BASE_URL + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"title":"Updated Title","description":"Updated desc"}
                                    """))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.title").value("Updated Title"))
              .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} → 404 for non-existing task")
    void updateTask_nonExisting_returns404() throws Exception {
      mockMvc.perform(put(BASE_URL + "/9999")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content("""
                                    {"title":"Ghost","description":"does not exist"}
                                    """))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} → deletes with 204")
    void deleteTask_existingTask_returns204() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/1"))
              .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} → 404 for non-existing task")
    void deleteTask_nonExisting_returns404() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/9999"))
              .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Exercise 3: Custom Query Endpoint")
  class CustomQueries {

    @Test
    @DisplayName("GET /api/tasks/overdue → returns 3 overdue tasks (not DONE)")
    void getOverdueTasks_returns3() throws Exception {
      mockMvc.perform(get(BASE_URL + "/overdue"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(3)))
              .andExpect(jsonPath("$[*].status", not(hasItem("DONE"))));
    }
  }

  @Nested
  @DisplayName("Exercise 4: Filter + Pagination")
  class FilterAndPagination {

    @Test
    @DisplayName("GET /api/tasks/filter?status=TODO&page=0&size=5 → paginated TODO tasks")
    void filterByStatus_todo_returnsPaginated() throws Exception {
      mockMvc.perform(get(BASE_URL + "/filter")
                      .param("status", "TODO")
                      .param("page", "0")
                      .param("size", "5"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data").isArray())
              .andExpect(jsonPath("$.pagination").exists())
              .andExpect(jsonPath("$.pagination.total").value(5))
              .andExpect(jsonPath("$.data", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/tasks/filter?priority=URGENT → filters by priority")
    void filterByPriority_urgent_returnsUrgentTasks() throws Exception {
      mockMvc.perform(get(BASE_URL + "/filter")
                      .param("priority", "URGENT")
                      .param("page", "0")
                      .param("size", "10"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data", hasSize(2)))
              .andExpect(jsonPath("$.pagination.total").value(2));
    }

    @Test
    @DisplayName("GET /api/tasks/filter?title=login → filters by title keyword")
    void filterByTitle_login_returnsMatchingTasks() throws Exception {
      mockMvc.perform(get(BASE_URL + "/filter")
                      .param("title", "login")
                      .param("page", "0")
                      .param("size", "10"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/tasks/filter?projectId=1 → filters by project")
    void filterByProjectId_returns4Tasks() throws Exception {
      mockMvc.perform(get(BASE_URL + "/filter")
                      .param("projectId", "1")
                      .param("page", "0")
                      .param("size", "10"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data", hasSize(4)))
              .andExpect(jsonPath("$.pagination.total").value(4));
    }

    @Test
    @DisplayName("GET /api/tasks/filter → no filters returns all 12 tasks")
    void filterNoParams_returnsAll() throws Exception {
      mockMvc.perform(get(BASE_URL + "/filter")
                      .param("page", "0")
                      .param("size", "20"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data", hasSize(12)))
              .andExpect(jsonPath("$.pagination.total").value(12));
    }
  }

  @Nested
  @DisplayName("Exercise 5: Task Label Management")
  class TaskLabels {

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status?status=DONE → updates task status")
    void updateStatus_toDone_returns200() throws Exception {
      mockMvc.perform(patch(BASE_URL + "/4/status").param("status", "DONE"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status → 404 for non-existing task")
    void updateStatus_nonExisting_returns404() throws Exception {
      mockMvc.perform(patch(BASE_URL + "/9999/status").param("status", "DONE"))
              .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks/{taskId}/labels/{labelId} → adds label to task")
    void addLabel_toTask_returns200() throws Exception {
      mockMvc.perform(post(BASE_URL + "/11/labels/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.labelNames", hasItem("bug")));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{taskId}/labels/{labelId} → removes label from task")
    void removeLabel_fromTask_returns200() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/1/labels/5"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.labelNames", not(hasItem("frontend"))));
    }

    @Test
    @DisplayName("POST /api/tasks/{taskId}/labels/{labelId} → 404 for non-existing task")
    void addLabel_nonExistingTask_returns404() throws Exception {
      mockMvc.perform(post(BASE_URL + "/9999/labels/1"))
              .andExpect(status().isNotFound());
    }
  }
}