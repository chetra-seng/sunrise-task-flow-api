package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProjectModel project;

    @BeforeEach
    void setUp() {
        project = ProjectModel.builder().name("Test Project").description("A test project").build();
        entityManager.persist(project);

        entityManager.persist(TaskModel.builder()
            .title("Design homepage")
            .description("Create wireframe")
            .completed(true)
            .priority(Priority.HIGH)
            .status(TaskStatus.DONE)
            .dueDate(LocalDate.of(2026, 3, 10))
            .project(project)
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Implement API")
            .description("Build REST endpoints")
            .completed(false)
            .priority(Priority.URGENT)
            .status(TaskStatus.IN_PROGRESS)
            .dueDate(LocalDate.of(2026, 3, 20))
            .project(project)
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Write tests")
            .description("Add unit and integration tests")
            .completed(false)
            .priority(Priority.MEDIUM)
            .status(TaskStatus.TODO)
            .dueDate(LocalDate.of(2026, 3, 25))
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Deploy application")
            .description("Set up CI/CD pipeline")
            .completed(false)
            .priority(Priority.LOW)
            .status(TaskStatus.TODO)
            .dueDate(LocalDate.of(2026, 4, 1))
            .build());

        entityManager.flush();
    }

    // ── Exercise 1: Entity Mapping ───────────────────────────────────────────

    @Nested
    class EntityMappingTests {

        @Test
        void save_assignsId() {
            TaskModel task = TaskModel.builder()
                .title("New task")
                .build();
            TaskModel saved = taskRepository.save(task);
            assertThat(saved.getId()).isNotNull();
        }

        @Test
        void save_setsDefaultValues() {
            TaskModel task = TaskModel.builder()
                .title("New task")
                .build();
            TaskModel saved = taskRepository.save(task);
            assertThat(saved.getCompleted()).isFalse();
            assertThat(saved.getPriority()).isEqualTo(Priority.MEDIUM);
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        void save_persistsAllFields() {
            TaskModel task = TaskModel.builder()
                .title("Full task")
                .description("With all fields")
                .priority(Priority.URGENT)
                .status(TaskStatus.IN_REVIEW)
                .dueDate(LocalDate.of(2026, 6, 15))
                .project(project)
                .build();
            TaskModel saved = taskRepository.save(task);
            entityManager.flush();
            entityManager.clear();

            TaskModel found = taskRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getTitle()).isEqualTo("Full task");
            assertThat(found.getDescription()).isEqualTo("With all fields");
            assertThat(found.getPriority()).isEqualTo(Priority.URGENT);
            assertThat(found.getStatus()).isEqualTo(TaskStatus.IN_REVIEW);
            assertThat(found.getDueDate()).isEqualTo(LocalDate.of(2026, 6, 15));
            assertThat(found.getCreatedAt()).isNotNull();
            assertThat(found.getProject().getId()).isEqualTo(project.getId());
        }

        @Test
        void save_projectRelationship_persistsForeignKey() {
            TaskModel task = TaskModel.builder()
                .title("Project task")
                .project(project)
                .build();
            TaskModel saved = taskRepository.save(task);
            entityManager.flush();
            entityManager.clear();

            TaskModel found = taskRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getProject()).isNotNull();
            assertThat(found.getProject().getName()).isEqualTo("Test Project");
        }
    }

    // ── Exercise 2: Derived Queries ──────────────────────────────────────────

    @Nested
    class DerivedQueryTests {

        @Test
        void findAllByCompleted_returnsOnlyMatching() {
            List<TaskModel> completed = taskRepository.findAllByCompleted(true);
            assertThat(completed).hasSize(1);
            assertThat(completed.get(0).getTitle()).isEqualTo("Design homepage");
        }

        @Test
        void findAllByCompleted_returnsFalseOnly() {
            List<TaskModel> incomplete = taskRepository.findAllByCompleted(false);
            assertThat(incomplete).hasSize(3);
        }

        @Test
        void findAllByStatus_returnsMatchingStatus() {
            List<TaskModel> todoTasks = taskRepository.findAllByStatus(TaskStatus.TODO);
            assertThat(todoTasks).hasSize(2);
        }

        @Test
        void findAllByPriority_returnsMatchingPriority() {
            List<TaskModel> urgent = taskRepository.findAllByPriority(Priority.URGENT);
            assertThat(urgent).hasSize(1);
            assertThat(urgent.get(0).getTitle()).isEqualTo("Implement API");
        }

        @Test
        void findAllByPriorityAndStatus_returnsCombinedFilter() {
            List<TaskModel> result = taskRepository.findAllByPriorityAndStatus(
                Priority.MEDIUM, TaskStatus.TODO);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Write tests");
        }

        @Test
        void findAllByTitleContainingIgnoreCase_findsPartialMatch() {
            List<TaskModel> result = taskRepository.findAllByTitleContainingIgnoreCase("impl");
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Implement API");
        }

        @Test
        void findAllByDueDateBefore_returnsTasksDueBefore() {
            List<TaskModel> result = taskRepository.findAllByDueDateBefore(
                LocalDate.of(2026, 3, 15));
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Design homepage");
        }

        @Test
        void countByCompleted_returnsCorrectCount() {
            long completedCount = taskRepository.countByCompleted(true);
            long incompleteCount = taskRepository.countByCompleted(false);
            assertThat(completedCount).isEqualTo(1);
            assertThat(incompleteCount).isEqualTo(3);
        }

        @Test
        void countByStatus_returnsCorrectCount() {
            long todoCount = taskRepository.countByStatus(TaskStatus.TODO);
            assertThat(todoCount).isEqualTo(2);
        }

        @Test
        void existsByTitle_returnsTrueForExisting() {
            assertThat(taskRepository.existsByTitle("Design homepage")).isTrue();
            assertThat(taskRepository.existsByTitle("Nonexistent")).isFalse();
        }
    }

    // ── Exercise 3: Custom Queries ───────────────────────────────────────────

    @Nested
    class CustomQueryTests {

        @Test
        void findByStatusAndPriorityOrdered_returnsFilteredAndSorted() {
            List<TaskModel> result = taskRepository.findByStatusAndPriorityOrdered(
                TaskStatus.TODO, Priority.MEDIUM);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Write tests");
        }

        @Test
        void findByIdWithProject_loadsProjectEagerly() {
            TaskModel task = taskRepository.findAllByCompleted(true).get(0);
            Optional<TaskModel> result = taskRepository.findByIdWithProject(task.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getProject()).isNotNull();
            assertThat(result.get().getProject().getName()).isEqualTo("Test Project");
        }

        @Test
        void findByProjectName_returnsTasksInProject() {
            List<TaskModel> result = taskRepository.findByProjectName("Test Project");
            assertThat(result).hasSize(2);
        }

        @Test
        void search_findsByKeywordAndCompletion() {
            List<TaskModel> result = taskRepository.search("API", false);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Implement API");
        }

        @Test
        void findSummariesByProject_returnsProjection() {
            var summaries = taskRepository.findSummariesByProject(project.getId());
            assertThat(summaries).hasSize(2);
            assertThat(summaries).allSatisfy(s -> {
                assertThat(s.getId()).isNotNull();
                assertThat(s.getTitle()).isNotNull();
            });
        }

        @Test
        void updateStatus_changesStatusAndReturnsCount() {
            TaskModel task = taskRepository.findAllByStatus(TaskStatus.TODO).get(0);
            int updated = taskRepository.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);
            assertThat(updated).isEqualTo(1);

            entityManager.flush();
            entityManager.clear();

            TaskModel reloaded = taskRepository.findById(task.getId()).orElseThrow();
            assertThat(reloaded.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }

        @Test
        void deleteAllCompleted_removesCompletedTasks() {
            int deleted = taskRepository.deleteAllCompleted();
            assertThat(deleted).isEqualTo(1);

            entityManager.flush();
            entityManager.clear();

            assertThat(taskRepository.findAll()).hasSize(3);
            assertThat(taskRepository.findAllByCompleted(true)).isEmpty();
        }
    }

    // ── Exercise 6: Pagination ───────────────────────────────────────────────

    @Nested
    class PaginationTests {

        @Test
        void findAll_paginated_returnsCorrectPage() {
            Page<TaskModel> page = taskRepository.findAll(PageRequest.of(0, 2));
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(4);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }

        @Test
        void findAllByCompleted_paginated_filtersAndPaginates() {
            Page<TaskModel> page = taskRepository.findAllByCompleted(false, PageRequest.of(0, 2));
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(3);
        }

        @Test
        void findAllByStatus_paginated_filtersAndPaginates() {
            Page<TaskModel> page = taskRepository.findAllByStatus(TaskStatus.TODO, PageRequest.of(0, 10));
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getTotalElements()).isEqualTo(2);
        }
    }
}
