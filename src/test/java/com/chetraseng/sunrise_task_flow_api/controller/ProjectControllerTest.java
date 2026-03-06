package com.chetraseng.sunrise_task_flow_api.controller;

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
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/projects";

    private MvcResult createProject(String name, String description) throws Exception {
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"%s","description":"%s"}
                    """.formatted(name, description)))
            .andReturn();
    }

    // ── CRUD Tests ───────────────────────────────────────────────────────────

    @Nested
    class CrudTests {

        @Test
        void getAllProjects_initially_returnsEmptyList() throws Exception {
            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        }

        @Test
        void createProject_returns201WithBody() throws Exception {
            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name":"My Project","description":"A test project"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("My Project"))
                .andExpect(jsonPath("$.description").value("A test project"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskCount").value(0));
        }

        @Test
        void getAllProjects_afterCreating_returnsProjects() throws Exception {
            createProject("Project A", "First project");
            createProject("Project B", "Second project");

            mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Project A", "Project B")));
        }

        @Test
        void getProjectById_existing_returns200() throws Exception {
            createProject("My Project", "Description");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long projectId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(get(BASE_URL + "/" + projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My Project"))
                .andExpect(jsonPath("$.description").value("Description"));
        }

        @Test
        void getProjectById_nonExisting_returns404() throws Exception {
            mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
        }

        @Test
        void updateProject_existing_returns200() throws Exception {
            createProject("Old Name", "Old description");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long projectId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(put(BASE_URL + "/" + projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name":"New Name","description":"New description"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("New description"));
        }

        @Test
        void updateProject_nonExisting_returns404() throws Exception {
            mockMvc.perform(put(BASE_URL + "/9999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name":"Ghost","description":"Not real"}
                        """))
                .andExpect(status().isNotFound());
        }

        @Test
        void deleteProject_existing_returns204() throws Exception {
            createProject("To Delete", "Will be gone");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long projectId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(delete(BASE_URL + "/" + projectId))
                .andExpect(status().isNoContent());
        }

        @Test
        void deleteProject_thenGet_returns404() throws Exception {
            createProject("To Delete", "Will be gone");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long projectId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            mockMvc.perform(delete(BASE_URL + "/" + projectId));

            mockMvc.perform(get(BASE_URL + "/" + projectId))
                .andExpect(status().isNotFound());
        }

        @Test
        void deleteProject_nonExisting_returns404() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
        }
    }

    // ── Task Count Tests ─────────────────────────────────────────────────────

    @Nested
    class TaskCountTests {

        @Test
        void projectResponse_includesTaskCount() throws Exception {
            createProject("Project With Tasks", "Has tasks");

            MvcResult listResult = mockMvc.perform(get(BASE_URL)).andReturn();
            Long projectId = com.jayway.jsonpath.JsonPath.parse(
                listResult.getResponse().getContentAsString()).read("$[0].id", Long.class);

            // Create tasks in this project
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Task 1","description":"First","projectId":%d}
                        """.formatted(projectId)));
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"title":"Task 2","description":"Second","projectId":%d}
                        """.formatted(projectId)));

            mockMvc.perform(get(BASE_URL + "/" + projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskCount").value(2));
        }
    }
}
