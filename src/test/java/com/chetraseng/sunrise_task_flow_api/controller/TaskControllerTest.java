package com.chetraseng.sunrise_task_flow_api.controller;

import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    private static final String BASE_URL = "/api/tasks";

    private Long projectId;

    @BeforeEach
    void setUp() {
        ProjectModel project = ProjectModel.builder()
            .name("Test Project")
            .description("For testing")
            .build();
        projectId = projectRepository.save(project).getId();
    }

    private MvcResult createTask(String title, String description) throws Exception {
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"%s","description":"%s"}
                    """.formatted(title, description)))
            .andReturn();
    }

    private MvcResult createTaskWithDetails(String title, String priority, String status,
                                             String dueDate) throws Exception {
        String body = """
            {"title":"%s","description":"test","priority":"%s","status":"%s","dueDate":"%s","projectId":%d}
            """.formatted(title, priority, status, dueDate, projectId);
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn();
    }

    // ── Exercise 4 & 5: Basic CRUD with ResponseEntity ───────────────────────

    @Nested
    class CrudTests {

        @Test
        void getAllTasks_initially_returnsEmptyList() throws Exception {
            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        }

        @Test
        void getAllTasks_afterCreating_returnsTasks() throws Exception {
            createTask("Task One", "First task");
            createTask("Task Two", "Second task");

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Task One", "Task Two")));
        }

        @Test
        void createTask_validRequest_returns201WithBody() throws Exception {
            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Learn Spring Boot","description":"Complete the course"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.description").value("Complete the course"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        void createTask_withPriorityAndStatus_returnsCorrectValues() throws Exception {
            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Urgent task","priority":"URGENT","status":"IN_PROGRESS","dueDate":"2026-04-01","projectId":%d}
                        """.formatted(projectId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority").value("URGENT"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.dueDate").value("2026-04-01"))
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.projectName").value("Test Project"));
        }

        @Test
        void getTaskById_existingTask_returns200() throws Exception {
            createTask("My Task", "Some description");

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

            // Get first task's ID from list
            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            String body = listResult.getResponse().getContentAsString();
            Long taskId = com.jayway.jsonpath.JsonPath.parse(body).read("$[0].id", Long.class);

            mockMvc.perform(get(BASE_URL + "/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Task"));
        }

        @Test
        void getTaskById_nonExistingId_returns404() throws Exception {
            mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
        }

        @Test
        void updateTask_existingTask_returns200WithUpdatedFields() throws Exception {
            createTask("Original Title", "Original description");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long taskId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(put(BASE_URL + "/" + taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Updated Title","description":"Updated description","priority":"HIGH","status":"IN_REVIEW"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("IN_REVIEW"));
        }

        @Test
        void updateTask_nonExistingId_returns404() throws Exception {
            mockMvc.perform(put(BASE_URL + "/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Ghost","description":"does not exist"}
                        """))
                .andExpect(status().isNotFound());
        }

        @Test
        void completeTask_existingTask_returns200AndCompleted() throws Exception {
            createTask("Task to complete", "Do this");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long taskId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(patch(BASE_URL + "/" + taskId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.status").value("DONE"));
        }

        @Test
        void completeTask_nonExistingId_returns404() throws Exception {
            mockMvc.perform(patch(BASE_URL + "/9999/complete"))
                .andExpect(status().isNotFound());
        }

        @Test
        void deleteTask_existingTask_returns204() throws Exception {
            createTask("Task to delete", "Will be gone");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long taskId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(delete(BASE_URL + "/" + taskId))
                .andExpect(status().isNoContent());
        }

        @Test
        void deleteTask_thenGet_returns404() throws Exception {
            createTask("Task to delete", "Will be gone");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long taskId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(delete(BASE_URL + "/" + taskId));

            mockMvc.perform(get(BASE_URL + "/" + taskId))
                .andExpect(status().isNotFound());
        }

        @Test
        void deleteTask_nonExistingId_returns404() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
        }
    }

    // ── Exercise 5: Filter Endpoint ──────────────────────────────────────────

    @Nested
    class FilterTests {

        @Test
        void filterByCompleted_returnsOnlyCompleted() throws Exception {
            createTask("Pending task", "Not done");
            createTask("Done task", "Finished");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long secondId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[1].id", Long.class);

            mockMvc.perform(patch(BASE_URL + "/" + secondId + "/complete"));

            mockMvc.perform(get(BASE_URL + "?completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed").value(true));
        }

        @Test
        void filterByCompletedFalse_returnsPendingOnly() throws Exception {
            createTask("Pending task", "Not done");
            createTask("Done task", "Finished");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long secondId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[1].id", Long.class);

            mockMvc.perform(patch(BASE_URL + "/" + secondId + "/complete"));

            mockMvc.perform(get(BASE_URL + "?completed=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed").value(false));
        }

        @Test
        void getAllTasks_noFilter_returnsAll() throws Exception {
            createTask("Task A", "First");
            createTask("Task B", "Second");

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void filterByCompletedAndTitle_returnsMatching() throws Exception {
            createTask("Spring Boot Task", "Learn spring");
            createTask("React Task", "Learn react");

            mockMvc.perform(get(BASE_URL + "/filter?completed=false&title=Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Task"));
        }
    }

    // ── Exercise 6: Pagination ───────────────────────────────────────────────

    @Nested
    class PaginationTests {

        @Test
        void getTasksPaged_returnsPagedResults() throws Exception {
            for (int i = 1; i <= 5; i++) {
                createTask("Task " + i, "Description " + i);
            }

            mockMvc.perform(get(BASE_URL + "/paged?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0));
        }

        @Test
        void getTasksPaged_secondPage_returnsCorrectContent() throws Exception {
            for (int i = 1; i <= 5; i++) {
                createTask("Task " + i, "Description " + i);
            }

            mockMvc.perform(get(BASE_URL + "/paged?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.number").value(1));
        }

        @Test
        void getTasksPaged_defaultParams_works() throws Exception {
            createTask("Task 1", "Desc");

            mockMvc.perform(get(BASE_URL + "/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    // ── Exercise 7: Specification Search ─────────────────────────────────────

    @Nested
    class SearchTests {

        @Test
        void search_byKeyword_returnsMatching() throws Exception {
            createTaskWithDetails("Design UI", "HIGH", "DONE", "2026-03-10");
            createTaskWithDetails("Build API", "URGENT", "IN_PROGRESS", "2026-03-20");
            createTaskWithDetails("Write docs", "LOW", "TODO", "2026-04-01");

            mockMvc.perform(get(BASE_URL + "/search?keyword=API"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Build API"));
        }

        @Test
        void search_byStatus_returnsMatching() throws Exception {
            createTaskWithDetails("Task A", "HIGH", "TODO", "2026-03-10");
            createTaskWithDetails("Task B", "LOW", "IN_PROGRESS", "2026-03-20");

            mockMvc.perform(get(BASE_URL + "/search?status=TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Task A"));
        }

        @Test
        void search_byPriority_returnsMatching() throws Exception {
            createTaskWithDetails("Low task", "LOW", "TODO", "2026-03-10");
            createTaskWithDetails("Urgent task", "URGENT", "TODO", "2026-03-20");

            mockMvc.perform(get(BASE_URL + "/search?priority=URGENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Urgent task"));
        }

        @Test
        void search_combinedFilters_returnsMatching() throws Exception {
            createTaskWithDetails("High TODO", "HIGH", "TODO", "2026-03-10");
            createTaskWithDetails("High DONE", "HIGH", "DONE", "2026-03-15");
            createTaskWithDetails("Low TODO", "LOW", "TODO", "2026-03-20");

            mockMvc.perform(get(BASE_URL + "/search?priority=HIGH&status=TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("High TODO"));
        }

        @Test
        void search_byProjectId_returnsMatching() throws Exception {
            createTaskWithDetails("Project task", "MEDIUM", "TODO", "2026-03-10");
            createTask("No project task", "Orphan");

            mockMvc.perform(get(BASE_URL + "/search?projectId=" + projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Project task"));
        }

        @Test
        void search_noFilters_returnsAll() throws Exception {
            createTask("Task A", "First");
            createTask("Task B", "Second");

            mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
        }

        @Test
        void search_withPagination_paginatesResults() throws Exception {
            for (int i = 1; i <= 5; i++) {
                createTask("Task " + i, "Desc");
            }

            mockMvc.perform(get(BASE_URL + "/search?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5));
        }
    }
}
