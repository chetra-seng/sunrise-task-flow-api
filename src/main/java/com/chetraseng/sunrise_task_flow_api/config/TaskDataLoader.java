package com.chetraseng.sunrise_task_flow_api.config;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class TaskDataLoader implements CommandLineRunner {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;

  @Override
  public void run(String... args) {
    List<ProjectModel> projects = List.of(
        createProject("Task Management System", "Internal tool for managing team tasks and sprints"),
        createProject("E-Commerce Platform", "Online store with product catalog and checkout"),
        createProject("Company Website", "Corporate website with blog and contact form")
    );

    projectRepository.saveAll(projects);

    List<TaskModel> tasks = List.of(
        createTask("Design login page UI",
            "Create wireframe and final UI design for the login page.",
            true, Priority.HIGH, TaskStatus.DONE,
            LocalDate.of(2026, 3, 5),
            LocalDateTime.of(2026, 3, 1, 9, 15),
            projects.get(0)),

        createTask("Implement authentication API",
            "Develop REST endpoint for user authentication using JWT.",
            false, Priority.URGENT, TaskStatus.IN_PROGRESS,
            LocalDate.of(2026, 3, 10),
            LocalDateTime.of(2026, 3, 1, 10, 0),
            projects.get(0)),

        createTask("Set up database schema",
            "Design and create initial database tables.",
            true, Priority.HIGH, TaskStatus.DONE,
            LocalDate.of(2026, 3, 3),
            LocalDateTime.of(2026, 3, 1, 11, 30),
            projects.get(0)),

        createTask("Create task service layer",
            "Implement business logic for task management.",
            false, Priority.MEDIUM, TaskStatus.TODO,
            LocalDate.of(2026, 3, 15),
            LocalDateTime.of(2026, 3, 1, 13, 45),
            projects.get(0)),

        createTask("Product listing page",
            "Create UI for displaying products.",
            true, Priority.MEDIUM, TaskStatus.DONE,
            LocalDate.of(2026, 3, 8),
            LocalDateTime.of(2026, 3, 2, 9, 0),
            projects.get(1)),

        createTask("Shopping cart integration",
            "Implement add-to-cart functionality.",
            false, Priority.HIGH, TaskStatus.IN_REVIEW,
            LocalDate.of(2026, 3, 12),
            LocalDateTime.of(2026, 3, 2, 10, 30),
            projects.get(1)),

        createTask("Payment gateway integration",
            "Integrate Stripe payment system.",
            false, Priority.URGENT, TaskStatus.TODO,
            LocalDate.of(2026, 3, 20),
            LocalDateTime.of(2026, 3, 2, 14, 15),
            projects.get(1)),

        createTask("Order history page",
            "Allow users to view past orders.",
            true, Priority.LOW, TaskStatus.DONE,
            LocalDate.of(2026, 3, 6),
            LocalDateTime.of(2026, 3, 2, 16, 45),
            projects.get(1)),

        createTask("Homepage redesign",
            "Modernize homepage layout and branding.",
            false, Priority.MEDIUM, TaskStatus.IN_PROGRESS,
            LocalDate.of(2026, 3, 18),
            LocalDateTime.of(2026, 3, 3, 9, 10),
            projects.get(2)),

        createTask("Blog module setup",
            "Implement blog functionality with CRUD operations.",
            true, Priority.LOW, TaskStatus.DONE,
            LocalDate.of(2026, 3, 4),
            LocalDateTime.of(2026, 3, 3, 11, 20),
            projects.get(2)),

        createTask("SEO optimization",
            "Improve search engine visibility.",
            false, Priority.LOW, TaskStatus.TODO,
            LocalDate.of(2026, 3, 25),
            LocalDateTime.of(2026, 3, 3, 13, 45),
            projects.get(2)),

        createTask("Contact form integration",
            "Add backend email support for contact form.",
            true, Priority.MEDIUM, TaskStatus.DONE,
            LocalDate.of(2026, 3, 7),
            LocalDateTime.of(2026, 3, 3, 15, 30),
            projects.get(2))
    );

    taskRepository.saveAll(tasks);
  }

  private static TaskModel createTask(String title, String description, boolean completed,
                                       Priority priority, TaskStatus status, LocalDate dueDate,
                                       LocalDateTime createdAt, ProjectModel project) {
    TaskModel task = new TaskModel();
    task.setTitle(title);
    task.setDescription(description);
    task.setCompleted(completed);
    task.setPriority(priority);
    task.setStatus(status);
    task.setDueDate(dueDate);
    task.setCreatedAt(createdAt);
    task.setProject(project);
    return task;
  }

  private static ProjectModel createProject(String name, String description) {
    ProjectModel project = new ProjectModel();
    project.setName(name);
    project.setDescription(description);
    return project;
  }
}
