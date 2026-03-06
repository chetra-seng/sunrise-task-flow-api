package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.persist(ProjectModel.builder()
            .name("Task Management System")
            .description("Internal task tracker")
            .build());
        entityManager.persist(ProjectModel.builder()
            .name("E-Commerce Platform")
            .description("Online store")
            .build());
        entityManager.persist(ProjectModel.builder()
            .name("Company Website")
            .description("Corporate website")
            .build());
        entityManager.flush();
    }

    // ── Exercise 1: Entity Mapping ───────────────────────────────────────────

    @Test
    void save_assignsId() {
        ProjectModel project = ProjectModel.builder()
            .name("New Project")
            .description("A new project")
            .build();
        ProjectModel saved = projectRepository.save(project);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void save_persistsDescription() {
        ProjectModel project = ProjectModel.builder()
            .name("With Description")
            .description("Detailed project description")
            .build();
        ProjectModel saved = projectRepository.save(project);
        entityManager.flush();
        entityManager.clear();

        ProjectModel found = projectRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getDescription()).isEqualTo("Detailed project description");
    }

    // ── Exercise 2: Derived Queries ──────────────────────────────────────────

    @Test
    void findByName_returnsMatchingProject() {
        Optional<ProjectModel> result = projectRepository.findByName("Task Management System");
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Internal task tracker");
    }

    @Test
    void findByName_returnsEmptyForNonExisting() {
        Optional<ProjectModel> result = projectRepository.findByName("Nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_findsPartialMatch() {
        List<ProjectModel> result = projectRepository.findByNameContainingIgnoreCase("commerce");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("E-Commerce Platform");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMultipleMatches() {
        List<ProjectModel> result = projectRepository.findByNameContainingIgnoreCase("e");
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void existsByName_returnsTrueForExisting() {
        assertThat(projectRepository.existsByName("Company Website")).isTrue();
    }

    @Test
    void existsByName_returnsFalseForNonExisting() {
        assertThat(projectRepository.existsByName("Ghost Project")).isFalse();
    }
}
