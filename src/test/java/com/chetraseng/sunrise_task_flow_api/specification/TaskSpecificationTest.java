package com.chetraseng.sunrise_task_flow_api.specification;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskSpecificationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProjectModel projectA;
    private ProjectModel projectB;

    @BeforeEach
    void setUp() {
        projectA = ProjectModel.builder().name("Project Alpha").description("First project").build();
        projectB = ProjectModel.builder().name("Project Beta").description("Second project").build();
        entityManager.persist(projectA);
        entityManager.persist(projectB);

        entityManager.persist(TaskModel.builder()
            .title("Design UI mockups")
            .completed(true)
            .priority(Priority.HIGH)
            .status(TaskStatus.DONE)
            .dueDate(LocalDate.of(2026, 3, 10))
            .project(projectA)
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Build REST API")
            .completed(false)
            .priority(Priority.URGENT)
            .status(TaskStatus.IN_PROGRESS)
            .dueDate(LocalDate.of(2026, 3, 20))
            .project(projectA)
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Write API documentation")
            .completed(false)
            .priority(Priority.LOW)
            .status(TaskStatus.TODO)
            .dueDate(LocalDate.of(2026, 4, 1))
            .project(projectB)
            .build());

        entityManager.persist(TaskModel.builder()
            .title("Deploy to staging")
            .completed(false)
            .priority(Priority.MEDIUM)
            .status(TaskStatus.TODO)
            .dueDate(LocalDate.of(2026, 3, 28))
            .project(projectB)
            .build());

        entityManager.flush();
    }

    @Test
    void hasStatus_filtersCorrectly() {
        List<TaskModel> result = taskRepository.findAll(TaskSpecification.hasStatus(TaskStatus.TODO));
        assertThat(result).hasSize(2);
    }

    @Test
    void hasPriority_filtersCorrectly() {
        List<TaskModel> result = taskRepository.findAll(TaskSpecification.hasPriority(Priority.URGENT));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Build REST API");
    }

    @Test
    void isCompleted_filtersCorrectly() {
        List<TaskModel> completed = taskRepository.findAll(TaskSpecification.isCompleted(true));
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getTitle()).isEqualTo("Design UI mockups");
    }

    @Test
    void titleContains_caseInsensitive() {
        List<TaskModel> result = taskRepository.findAll(TaskSpecification.titleContains("api"));
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TaskModel::getTitle)
            .containsExactlyInAnyOrder("Build REST API", "Write API documentation");
    }

    @Test
    void inProject_filtersCorrectly() {
        List<TaskModel> result = taskRepository.findAll(TaskSpecification.inProject(projectA.getId()));
        assertThat(result).hasSize(2);
    }

    @Test
    void dueBefore_filtersCorrectly() {
        List<TaskModel> result = taskRepository.findAll(
            TaskSpecification.dueBefore(LocalDate.of(2026, 3, 15)));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Design UI mockups");
    }

    @Test
    void composedSpec_statusAndPriority() {
        Specification<TaskModel> spec = TaskSpecification.hasStatus(TaskStatus.TODO)
            .and(TaskSpecification.hasPriority(Priority.LOW));

        List<TaskModel> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write API documentation");
    }

    @Test
    void composedSpec_incompleteInProject() {
        Specification<TaskModel> spec = TaskSpecification.isCompleted(false)
            .and(TaskSpecification.inProject(projectA.getId()));

        List<TaskModel> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Build REST API");
    }

    @Test
    void composedSpec_titleAndStatus() {
        Specification<TaskModel> spec = TaskSpecification.titleContains("api")
            .and(TaskSpecification.hasStatus(TaskStatus.IN_PROGRESS));

        List<TaskModel> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Build REST API");
    }

    @Test
    void composedSpec_withPagination() {
        Specification<TaskModel> spec = TaskSpecification.isCompleted(false);

        Page<TaskModel> page = taskRepository.findAll(spec, PageRequest.of(0, 2));
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void composedSpec_allFilters() {
        Specification<TaskModel> spec = TaskSpecification.isCompleted(false)
            .and(TaskSpecification.hasStatus(TaskStatus.TODO))
            .and(TaskSpecification.inProject(projectB.getId()))
            .and(TaskSpecification.hasPriority(Priority.LOW));

        List<TaskModel> result = taskRepository.findAll(spec);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write API documentation");
    }

    @Test
    void noFilters_returnsAll() {
        List<TaskModel> result = taskRepository.findAll((Specification<TaskModel>) null);
        assertThat(result).hasSize(4);
    }
}
