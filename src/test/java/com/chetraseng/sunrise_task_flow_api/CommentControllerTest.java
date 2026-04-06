package com.chetraseng.sunrise_task_flow_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class CommentControllerTest extends BaseControllerTest {

  @Test
  @DisplayName("GET /api/tasks/{taskId}/comments → returns 2 comments for task 1")
  void getComments_task1_returns2() throws Exception {
    mockMvc.perform(get("/api/tasks/1/comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].content").exists())
            .andExpect(jsonPath("$[0].author").exists());
  }

  @Test
  @DisplayName("GET /api/tasks/{taskId}/comments → returns 0 comments for task 3")
  void getComments_taskWithNoComments_returnsEmpty() throws Exception {
    mockMvc.perform(get("/api/tasks/3/comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /api/tasks/{taskId}/comments → 404 for non-existing task")
  void getComments_nonExistingTask_returns404() throws Exception {
    mockMvc.perform(get("/api/tasks/9999/comments"))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("POST /api/tasks/{taskId}/comments → creates comment with 201")
  void createComment_returns201() throws Exception {
    mockMvc.perform(post("/api/tasks/4/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {"content":"Great progress!","author":"Dave"}
                                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.content").value("Great progress!"))
            .andExpect(jsonPath("$.author").value("Dave"))
            .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  @DisplayName("POST /api/tasks/{taskId}/comments → 404 for non-existing task")
  void createComment_nonExistingTask_returns404() throws Exception {
    mockMvc.perform(post("/api/tasks/9999/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {"content":"Ghost comment","author":"Nobody"}
                                """))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PUT /api/comments/{id} → updates existing comment")
  void updateComment_existing_returns200() throws Exception {
    mockMvc.perform(put("/api/comments/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {"content":"Updated comment","author":"Alice"}
                                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Updated comment"))
            .andExpect(jsonPath("$.author").value("Alice"));
  }

  @Test
  @DisplayName("PUT /api/comments/{id} → 404 for non-existing comment")
  void updateComment_nonExisting_returns404() throws Exception {
    mockMvc.perform(put("/api/comments/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {"content":"Ghost","author":"Nobody"}
                                """))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("DELETE /api/comments/{id} → deletes with 204")
  void deleteComment_existing_returns204() throws Exception {
    mockMvc.perform(delete("/api/comments/1"))
            .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/comments/{id} → 404 for non-existing comment")
  void deleteComment_nonExisting_returns404() throws Exception {
    mockMvc.perform(delete("/api/comments/9999"))
            .andExpect(status().isNotFound());
  }
}