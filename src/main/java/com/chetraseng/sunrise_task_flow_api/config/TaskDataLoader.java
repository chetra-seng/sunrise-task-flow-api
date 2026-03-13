package com.chetraseng.sunrise_task_flow_api.config;

import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TaskDataLoader implements CommandLineRunner {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;

  @Override
  public void run(String... args) throws Exception {
    List<ProjectModel> projects = List.of(
            createProject("Task Management System"),
            createProject("E-Commerce Platform"),
            createProject("Company Website")
    );

    projectRepository.saveAll(projects);

    List<TaskModel> tasks = List.of(

            createTask("Design login page UI",
                    "Create wireframe and final UI design for the login page.",
                    true,
                    LocalDateTime.of(2026, 3, 1, 9, 15),
                    projects.get(0)),

            createTask("Implement authentication API",
                    "Develop REST endpoint for user authentication using JWT.",
                    false,
                    LocalDateTime.of(2026, 3, 1, 10, 0),
                    projects.get(0)),

            createTask("Set up database schema",
                    "Design and create initial database tables.",
                    true,
                    LocalDateTime.of(2026, 3, 1, 11, 30),
                    projects.get(0)),

            createTask("Create task service layer",
                    "Implement business logic for task management.",
                    false,
                    LocalDateTime.of(2026, 3, 1, 13, 45),
                    projects.get(0)),

            createTask("Product listing page",
                    "Create UI for displaying products.",
                    true,
                    LocalDateTime.of(2026, 3, 2, 9, 0),
                    projects.get(1)),

            createTask("Shopping cart integration",
                    "Implement add-to-cart functionality.",
                    false,
                    LocalDateTime.of(2026, 3, 2, 10, 30),
                    projects.get(1)),

            createTask("Payment gateway integration",
                    "Integrate Stripe payment system.",
                    false,
                    LocalDateTime.of(2026, 3, 2, 14, 15),
                    projects.get(1)),

            createTask("Order history page",
                    "Allow users to view past orders.",
                    true,
                    LocalDateTime.of(2026, 3, 2, 16, 45),
                    projects.get(1)),

            createTask("Homepage redesign",
                    "Modernize homepage layout and branding.",
                    false,
                    LocalDateTime.of(2026, 3, 3, 9, 10),
                    projects.get(2)),

            createTask("Blog module setup",
                    "Implement blog functionality with CRUD operations.",
                    true,
                    LocalDateTime.of(2026, 3, 3, 11, 20),
                    projects.get(2)),

            createTask("SEO optimization",
                    "Improve search engine visibility.",
                    false,
                    LocalDateTime.of(2026, 3, 3, 13, 45),
                    projects.get(2)),

            createTask("Contact form integration",
                    "Add backend email support for contact form.",
                    true,
                    LocalDateTime.of(2026, 3, 3, 15, 30),
                    projects.get(2))
    );

    taskRepository.saveAll(tasks);
  }

  private static TaskModel createTask(String title,
                                      String description,
                                      boolean completed,
                                      LocalDateTime createdAt,
                                      ProjectModel project) {

    TaskModel task = new TaskModel();
    task.setTitle(title);
    task.setDescription(description);
    task.setCompleted(completed);
    task.setCreatedAt(createdAt);
    task.setProject(project);

    return task;
  }

  private static ProjectModel createProject(String name) {
    ProjectModel project = new ProjectModel();
    project.setName(name);
    return project;
  }
}
