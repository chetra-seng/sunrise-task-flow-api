# Spring Data JPA — Hands-On Exercise

## Task Flow API: A ClickUp-Like Task Management System

Build a RESTful task management API using **Spring Data JPA**. You will practice:
- Derived query methods
- Custom `@Query` (JPQL and native SQL)
- JPA Specifications for dynamic filtering
- Pagination
- Interface-based projections
- `@ManyToMany` and `@OneToMany` relationships

---

## Getting Started

```bash
git checkout exercise/spring-data-jpa
```

### Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 4 | Application framework |
| Spring Data JPA + Hibernate | Data access |
| PostgreSQL | Dev database (`-Dspring.profiles.active=dev`) |
| H2 | Test database (auto-configured via `src/test/resources`) |
| MapStruct | Object mapping |
| Lombok | Boilerplate reduction |

### Validating Your Work

```bash
./mvnw test
```

Tests run against an **H2 in-memory database** seeded with fixture data — no PostgreSQL needed. Each test class covers one exercise. Run a single exercise's tests with:

```bash
./mvnw test -Dtest="TaskControllerTest"
./mvnw test -Dtest="TaskControllerTest#TaskCrud"   # just one nested class
```

---

## What's Already Provided

| File | Status | Notes |
|---|---|---|
| `model/Priority.java` | ✅ Complete | Enum: `LOW, MEDIUM, HIGH, URGENT` |
| `model/TaskStatus.java` | ✅ Complete | Enum: `TODO, IN_PROGRESS, DONE` |
| `model/ProjectModel.java` | ✅ Complete | Entity with `id`, `name`, `createdAt`, `tasks` (OneToMany) |
| `model/TaskModel.java` | 🔧 Skeleton | Basic fields provided — you add more |
| `dto/TaskRequest.java` | 🔧 Skeleton | `title`, `description` provided — you add more |
| `dto/TaskResponse.java` | 🔧 Skeleton | Basic fields provided — you add more |
| `dto/FilterTaskDto.java` | 🔧 Skeleton | `projectId`, `title` provided — you add more |
| `dto/Pagination.java` | ✅ Complete | `page`, `size`, `total`, `totalPage` |
| `dto/PaginationResponse<T>` | ✅ Complete | Wraps `data` (list) + `pagination` metadata |
| `dto/ErrorResponse.java` | ✅ Complete | Standard error envelope |
| `mapper/TaskMapper.java` | 🔧 Skeleton | `projectName`/`projectId` mappings provided — you add more |
| `repository/TaskRepository.java` | 🔧 Skeleton | Extends JPA + Specification executors — you add methods |
| `repository/ProjectRepository.java` | 🔧 Skeleton | Extends JpaRepository — you add methods |
| `spec/TaskSpec.java` | 🔧 Skeleton | `containsTitle`, `equalProjectId` provided — you add more |
| `controllers/TaskController.java` | 🔧 Skeleton | Empty controller — you implement all endpoints |
| `exception/ResourceNotFoundException.java` | ✅ Complete | Throw this for 404s |
| `exception/GlobalExceptionHandler.java` | ✅ Complete | Maps `ResourceNotFoundException` → 404 |

---

## Exercise 1: Task CRUD + Derived Queries

**Concepts:** Derived query methods, `ResponseEntity`, enum handling

---

### Step 1.1 — Enhance `TaskModel`

Open `model/TaskModel.java`. The fields `id`, `title`, `description`, `createdAt`, and `project` (ManyToOne) are already there. Add:

| Field | Type | Annotations |
|---|---|---|
| `status` | `TaskStatus` | `@Enumerated(EnumType.STRING)`, `@Column(nullable = false)`, default value `TaskStatus.TODO` |
| `priority` | `Priority` | `@Enumerated(EnumType.STRING)`, `@Column(nullable = false)`, default value `Priority.MEDIUM` |
| `dueDate` | `LocalDate` | No special annotation needed |

> You will add `labels` and `comments` in Exercises 5 and 6.

---

### Step 1.2 — Update the DTOs

**`TaskRequest.java`** — Add these fields (the TODOs are already in the file):

| Field | Type |
|---|---|
| `projectId` | `Long` |
| `priority` | `Priority` |
| `status` | `TaskStatus` |
| `dueDate` | `LocalDate` |

**`TaskResponse.java`** — Add these fields (the TODOs are already in the file):

| Field | Type |
|---|---|
| `status` | `TaskStatus` |
| `priority` | `Priority` |
| `dueDate` | `LocalDate` |
| `labelNames` | `List<String>` |
| `commentCount` | `int` |

---

### Step 1.3 — Update `TaskMapper`

Open `mapper/TaskMapper.java`. The mappings for `projectName` and `projectId` are already there. Add the two mappings described in the TODO comments:

**`labelNames`** — convert `task.labels` into a list of name strings:

```java
@Named("labelsToNames")
default List<String> labelsToNames(List<LabelModel> labels) {
    if (labels == null) return List.of();
    return labels.stream().map(LabelModel::getName).toList();
}

// Then annotate toTaskResponse with:
@Mapping(target = "labelNames", source = "labels", qualifiedByName = "labelsToNames")
```

**`commentCount`** — count `task.comments`:

```java
@Mapping(target = "commentCount",
         expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
```

> **Note:** These mappings depend on the `labels` and `comments` fields you will add to `TaskModel` in Exercises 5 and 6. Implement the mappings now — they will produce real values once those exercises are complete.

---

### Step 1.4 — Add Derived Queries to `TaskRepository`

Open `repository/TaskRepository.java` and implement the methods listed in the TODO comments. Spring Data JPA generates the SQL automatically from the method name — you only write the signature:

| Method | Return Type | Used By |
|---|---|---|
| `findByProjectId(Long projectId)` | `List<TaskModel>` | `GET /api/projects/{id}/tasks` |
| `findByStatus(TaskStatus status)` | `List<TaskModel>` | Practice |
| `findByPriority(Priority priority)` | `List<TaskModel>` | Practice |
| `findByDueDateBefore(LocalDate date)` | `List<TaskModel>` | Practice |
| `countByStatus(TaskStatus status)` | `long` | `GET /api/dashboard/summary` |

---

### Step 1.5 — Create `TaskService` + `TaskServiceImpl`

Create `services/TaskService.java` — a plain Java interface:

```java
public interface TaskService {
    List<TaskResponse> findAll();
    TaskResponse findById(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    void delete(Long id);
}
```

Then create `services/TaskServiceImpl.java` annotated with `@Service`:

| Method | What It Does |
|---|---|
| `findAll()` | `repository.findAll()`, map each to `TaskResponse` |
| `findById(Long id)` | `repository.findById(id)`, throw `ResourceNotFoundException` if empty |
| `create(TaskRequest)` | Build a `TaskModel`, if `projectId` is set look up the project and assign it, save and map |
| `update(Long id, TaskRequest)` | Find by ID or throw 404, update `title`, `description`, `status`, `priority`, `dueDate`, save and map |
| `delete(Long id)` | Find by ID or throw 404, then `repository.deleteById(id)` |

---

### Step 1.6 — Implement Endpoints in `TaskController`

Open `controllers/TaskController.java`. Inject your `TaskService` using constructor injection (`@RequiredArgsConstructor`). All methods must return `ResponseEntity<T>`. Implement the endpoints listed in the TODO comments:

| Method | Path | Response Body | Status |
|---|---|---|---|
| `GET` | `/api/tasks` | `List<TaskResponse>` | 200 |
| `GET` | `/api/tasks/{id}` | `TaskResponse` | 200 / 404 |
| `POST` | `/api/tasks` | `TaskResponse` | **201** |
| `PUT` | `/api/tasks/{id}` | `TaskResponse` | 200 / 404 |
| `DELETE` | `/api/tasks/{id}` | — | **204** / 404 |

```java
// 201 Created
ResponseEntity.status(HttpStatus.CREATED).body(result)

// 204 No Content
ResponseEntity.noContent().build()
```

### ✅ Verify

```bash
./mvnw test -Dtest="TaskControllerTest#TaskCrud"
```

---

## Exercise 2: Project CRUD + Table Join

**Concepts:** Second entity CRUD, navigating `@OneToMany` / `@ManyToOne`

---

### Step 2.1 — Create Project DTOs

**`dto/ProjectRequest.java`**

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class ProjectRequest {
    private String name;
}
```

**`dto/ProjectResponse.java`**

| Field | Type |
|---|---|
| `id` | `Long` |
| `name` | `String` |
| `createdAt` | `LocalDateTime` |
| `taskCount` | `int` |

---

### Step 2.2 — Create `ProjectMapper`

Create `mapper/ProjectMapper.java` using MapStruct. The `taskCount` field must be derived from the tasks list — `ProjectModel` already has `getTasks()`:

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    @Mapping(target = "taskCount",
             expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    ProjectResponse toProjectResponse(ProjectModel project);
}
```

---

### Step 2.3 — Add Derived Queries to `ProjectRepository`

Open `repository/ProjectRepository.java` and implement the two TODOs:

| Method | Return Type |
|---|---|
| `findByName(String name)` | `Optional<ProjectModel>` |
| `existsByName(String name)` | `boolean` |

---

### Step 2.4 — Create `ProjectService` + `ProjectServiceImpl`

Create `services/ProjectService.java`:

```java
public interface ProjectService {
    List<ProjectResponse> findAll();
    ProjectResponse findById(Long id);
    ProjectResponse create(ProjectRequest request);
    ProjectResponse update(Long id, ProjectRequest request);
    void delete(Long id);
    List<TaskResponse> findTasksByProjectId(Long id);
}
```

Then implement in `services/ProjectServiceImpl.java`:

| Method | What It Does |
|---|---|
| `findAll()` | Return all projects mapped to `ProjectResponse` |
| `findById(Long id)` | Find or throw 404 |
| `create(ProjectRequest)` | Build and save a new `ProjectModel` |
| `update(Long id, ProjectRequest)` | Find or throw 404, update `name`, save |
| `delete(Long id)` | Find or throw 404, delete |
| `findTasksByProjectId(Long id)` | Verify project exists (throw 404 if not), call `taskRepository.findByProjectId(id)`, map to `TaskResponse` |

---

### Step 2.5 — Create `ProjectController`

Create `controllers/ProjectController.java` with `@RequestMapping("/api/projects")`:

| Method | Path | Response Body | Status |
|---|---|---|---|
| `GET` | `/api/projects` | `List<ProjectResponse>` | 200 |
| `GET` | `/api/projects/{id}` | `ProjectResponse` | 200 / 404 |
| `POST` | `/api/projects` | `ProjectResponse` | **201** |
| `PUT` | `/api/projects/{id}` | `ProjectResponse` | 200 / 404 |
| `DELETE` | `/api/projects/{id}` | — | **204** / 404 |
| `GET` | `/api/projects/{id}/tasks` | `List<TaskResponse>` | 200 / 404 |

### ✅ Verify

```bash
./mvnw test -Dtest="ProjectControllerTest"
```

---

## Exercise 3: Custom `@Query` + Dashboard Projections

**Concepts:** JPQL, native SQL, interface-based projections, aggregate queries

---

### Step 3.1 — Add `findOverdueTasks` to `TaskRepository`

The TODO and hint are already in `repository/TaskRepository.java`. Implement the JPQL query that returns tasks where `dueDate < today` AND `status != DONE`:

```java
@Query("SELECT t FROM TaskModel t " +
       "WHERE t.dueDate < :today " +
       "AND t.status <> com.chetraseng.sunrise_task_flow_api.model.TaskStatus.DONE")
List<TaskModel> findOverdueTasks(@Param("today") LocalDate today);
```

---

### Step 3.2 — Add Overdue Endpoint

Add to `TaskService`:
```java
List<TaskResponse> findOverdueTasks();
```

Implement in `TaskServiceImpl` by calling `taskRepository.findOverdueTasks(LocalDate.now())` and mapping the results.

Add to `TaskController` (the TODO is already there):

| Method | Path | Response Body | Status |
|---|---|---|---|
| `GET` | `/api/tasks/overdue` | `List<TaskResponse>` | 200 |

---

### Step 3.3 — Create the `ProjectStatsView` Projection Interface

**Interface-based projections** let Spring Data JPA map query results directly to a Java interface — no concrete class needed. Spring creates a proxy at runtime. The SQL column **aliases must exactly match the getter names**.

Create `dto/ProjectStatsView.java`:

```java
public interface ProjectStatsView {
    String getProjectName();
    long getTaskCount();
    long getDoneCount();
}
```

---

### Step 3.4 — Add `getProjectStats()` to `ProjectRepository`

The TODO and full hint are already in `repository/ProjectRepository.java`. Implement the native SQL query — note how `AS projectName`, `AS taskCount`, `AS doneCount` map to `getProjectName()`, `getTaskCount()`, `getDoneCount()` in the interface:

```java
@Query(nativeQuery = true, value = """
    SELECT p.name          AS projectName,
           COUNT(t.id)     AS taskCount,
           SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount
    FROM   projects p
    LEFT JOIN tasks t ON t.project_id = p.id
    GROUP BY p.id, p.name
    """)
List<ProjectStatsView> getProjectStats();
```

---

### Step 3.5 — Create `DashboardResponse`

Create `dto/DashboardResponse.java`. A Java record works well here:

```java
public record DashboardResponse(
    long totalTasks,
    long todoCount,
    long inProgressCount,
    long doneCount,
    long overdueCount,
    List<ProjectStatsView> projectStats
) {}
```

---

### Step 3.6 — Create `DashboardService` + `DashboardServiceImpl`

Create `services/DashboardService.java`:

```java
public interface DashboardService {
    DashboardResponse getSummary();
}
```

Implement in `services/DashboardServiceImpl.java`. Inject both `TaskRepository` and `ProjectRepository`:

| Response Field | Source |
|---|---|
| `totalTasks` | `taskRepository.count()` |
| `todoCount` | `taskRepository.countByStatus(TaskStatus.TODO)` |
| `inProgressCount` | `taskRepository.countByStatus(TaskStatus.IN_PROGRESS)` |
| `doneCount` | `taskRepository.countByStatus(TaskStatus.DONE)` |
| `overdueCount` | `taskRepository.findOverdueTasks(LocalDate.now()).size()` |
| `projectStats` | `projectRepository.getProjectStats()` |

---

### Step 3.7 — Create `DashboardController`

Create `controllers/DashboardController.java` with `@RequestMapping("/api/dashboard")`:

| Method | Path | Response Body | Status |
|---|---|---|---|
| `GET` | `/api/dashboard/summary` | `DashboardResponse` | 200 |

### ✅ Verify

```bash
./mvnw test -Dtest="TaskControllerTest#CustomQueries"
./mvnw test -Dtest="DashboardControllerTest"
```

---

## Exercise 4: Specifications + Pagination

**Concepts:** Dynamic query building with `Specification`, composing predicates, paginated responses

---

### Step 4.1 — Add Specification Methods to `TaskSpec`

Open `spec/TaskSpec.java`. The `containsTitle` and `equalProjectId` specs are already implemented. Add the four methods listed in the TODO comments:

| Method | Hint |
|---|---|
| `hasStatus(TaskStatus status)` | `cb.equal(root.get("status"), status)` |
| `hasPriority(Priority priority)` | `cb.equal(root.get("priority"), priority)` |
| `dueBefore(LocalDate date)` | `cb.lessThan(root.get("dueDate"), date)` |
| `hasLabel(Long labelId)` | Requires a JOIN — see hint in the file |

```java
// hasLabel — hint already in TaskSpec.java:
public static Specification<TaskModel> hasLabel(Long labelId) {
    return (root, query, cb) -> {
        var labelJoin = root.join("labels");
        return cb.equal(labelJoin.get("id"), labelId);
    };
}
```

---

### Step 4.2 — Update `FilterTaskDto`

Open `dto/FilterTaskDto.java`. The `projectId` and `title` fields are already there. Add the four fields listed in the TODO comments:

| Field | Type |
|---|---|
| `status` | `TaskStatus` |
| `priority` | `Priority` |
| `dueBefore` | `LocalDate` |
| `labelId` | `Long` |

---

### Step 4.3 — Add `filterTasks` to `TaskService` + `TaskServiceImpl`

Add to `TaskService`:

```java
PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination);
```

Implement in `TaskServiceImpl`. Build a `Specification` dynamically — only add a clause when the filter field is non-null:

```java
Specification<TaskModel> spec = Specification.unrestricted();

if (filter.getTitle() != null)
    spec = spec.and(TaskSpec.containsTitle(filter.getTitle()));
if (filter.getProjectId() != null)
    spec = spec.and(TaskSpec.equalProjectId(filter.getProjectId()));
if (filter.getStatus() != null)
    spec = spec.and(TaskSpec.hasStatus(filter.getStatus()));
if (filter.getPriority() != null)
    spec = spec.and(TaskSpec.hasPriority(filter.getPriority()));
if (filter.getDueBefore() != null)
    spec = spec.and(TaskSpec.dueBefore(filter.getDueBefore()));
if (filter.getLabelId() != null)
    spec = spec.and(TaskSpec.hasLabel(filter.getLabelId()));
```

Execute with pagination and build the response:

```java
Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(),
                                   Sort.by("id").descending());
Page<TaskModel> page = taskRepository.findAll(spec, pageable);

List<TaskResponse> data = page.getContent().stream()
    .map(taskMapper::toTaskResponse)
    .toList();

Pagination meta = new Pagination();
meta.setPage(pagination.getPage());
meta.setSize(pagination.getSize());
meta.setTotal(page.getTotalElements());
meta.setTotalPage(page.getTotalPages());

return new PaginationResponse<>(data, meta);
```

---

### Step 4.4 — Add Filter Endpoint to `TaskController`

The TODO is already in `TaskController.java`. Spring binds all query params to `FilterTaskDto` and `Pagination` automatically — no `@RequestParam` needed on the method params:

| Method | Path | Query Params | Response Body |
|---|---|---|---|
| `GET` | `/api/tasks/filter` | `status`, `priority`, `title`, `projectId`, `dueBefore`, `labelId`, `page`, `size` | `PaginationResponse<TaskResponse>` |

```java
@GetMapping("/filter")
public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(
    FilterTaskDto filter, Pagination pagination) { ... }
```

### ✅ Verify

```bash
./mvnw test -Dtest="TaskControllerTest#FilterAndPagination"
```

---

## Exercise 5: Labels — ManyToMany Relationships

**Concepts:** `@ManyToMany`, join table, managing associations from the owning side

---

### Step 5.1 — Create `LabelModel`

Create `model/LabelModel.java`:

```java
@Entity
@Table(name = "labels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LabelModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 7)
    private String color;   // e.g. "#FF0000"

    @ManyToMany(mappedBy = "labels")
    private List<TaskModel> tasks;
}
```

---

### Step 5.2 — Add `labels` to `TaskModel`

The TODO is already in `model/TaskModel.java`. Add:

```java
@ManyToMany
@JoinTable(
    name = "task_labels",
    joinColumns = @JoinColumn(name = "task_id"),
    inverseJoinColumns = @JoinColumn(name = "label_id")
)
private List<LabelModel> labels = new ArrayList<>();
```

`TaskModel` is the **owning side** because it holds the `@JoinTable`. Modifications to `labels` on a `TaskModel` are what actually persist to the join table.

---

### Step 5.3 — Create Label DTOs

**`dto/LabelRequest.java`**: fields `name` (String) and `color` (String)

**`dto/LabelResponse.java`**: fields `id` (Long), `name` (String), `color` (String)

---

### Step 5.4 — Create `LabelMapper`

Create `mapper/LabelMapper.java` using MapStruct. All fields map directly:

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabelMapper {
    LabelResponse toLabelResponse(LabelModel label);
}
```

---

### Step 5.5 — Create `LabelRepository`

Create `repository/LabelRepository.java` extending `JpaRepository<LabelModel, Long>`:

| Method | Return Type |
|---|---|
| `findByName(String name)` | `Optional<LabelModel>` |
| `existsByName(String name)` | `boolean` |

---

### Step 5.6 — Create `LabelService` + `LabelServiceImpl`

Create `services/LabelService.java`:

```java
public interface LabelService {
    List<LabelResponse> findAll();
    LabelResponse findById(Long id);
    LabelResponse create(LabelRequest request);
    LabelResponse update(Long id, LabelRequest request);
    void delete(Long id);
    List<TaskResponse> findTasksByLabelId(Long id);
}
```

Implement in `services/LabelServiceImpl.java`. `findTasksByLabelId` should verify the label exists (throw 404 if not), then return `label.getTasks()` mapped to `TaskResponse`.

---

### Step 5.7 — Add Label Methods to `TaskService` + `TaskServiceImpl`

Add to `TaskService`:

```java
TaskResponse updateStatus(Long id, TaskStatus status);
TaskResponse addLabel(Long taskId, Long labelId);
TaskResponse removeLabel(Long taskId, Long labelId);
```

Implement in `TaskServiceImpl`:

| Method | What It Does |
|---|---|
| `updateStatus(Long id, TaskStatus)` | Find task or throw 404, `task.setStatus(status)`, save |
| `addLabel(Long taskId, Long labelId)` | Find both entities (throw 404 for either), add label to `task.getLabels()`, save task |
| `removeLabel(Long taskId, Long labelId)` | Find both entities, remove label from `task.getLabels()`, save task |

---

### Step 5.8 — Create `LabelController`

Create `controllers/LabelController.java` with `@RequestMapping("/api/labels")`:

| Method | Path | Response Body | Status |
|---|---|---|---|
| `GET` | `/api/labels` | `List<LabelResponse>` | 200 |
| `GET` | `/api/labels/{id}` | `LabelResponse` | 200 / 404 |
| `POST` | `/api/labels` | `LabelResponse` | **201** |
| `PUT` | `/api/labels/{id}` | `LabelResponse` | 200 / 404 |
| `DELETE` | `/api/labels/{id}` | — | **204** / 404 |
| `GET` | `/api/labels/{id}/tasks` | `List<TaskResponse>` | 200 / 404 |

---

### Step 5.9 — Add Label Endpoints to `TaskController`

The TODOs are already in `controllers/TaskController.java`. Add:

| Method | Path | Notes | Response Body | Status |
|---|---|---|---|---|
| `PATCH` | `/api/tasks/{id}/status` | `status` as `@RequestParam` | `TaskResponse` | 200 / 404 |
| `POST` | `/api/tasks/{taskId}/labels/{labelId}` | — | `TaskResponse` | 200 / 404 |
| `DELETE` | `/api/tasks/{taskId}/labels/{labelId}` | — | `TaskResponse` | 200 / 404 |

### ✅ Verify

```bash
./mvnw test -Dtest="LabelControllerTest"
./mvnw test -Dtest="TaskControllerTest#TaskLabels"
```

---

## Exercise 6: Comments — OneToMany Nested Resource

**Concepts:** Nested REST resource, `@ManyToOne` from child side, ordered derived query

---

### Step 6.1 — Create `CommentModel`

Create `model/CommentModel.java`:

```java
@Entity
@Table(name = "comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(length = 50)
    private String author;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskModel task;
}
```

---

### Step 6.2 — Add `comments` to `TaskModel`

The TODO is already in `model/TaskModel.java`. Add:

```java
@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
private List<CommentModel> comments = new ArrayList<>();
```

`cascade = CascadeType.ALL` means deleting a task also deletes its comments. `orphanRemoval = true` deletes a comment if it's removed from the list.

---

### Step 6.3 — Create Comment DTOs

**`dto/CommentRequest.java`**: fields `content` (String), `author` (String)

**`dto/CommentResponse.java`**: fields `id` (Long), `content` (String), `author` (String), `createdAt` (LocalDateTime)

---

### Step 6.4 — Create `CommentMapper`

Create `mapper/CommentMapper.java` using MapStruct. All fields map directly:

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    CommentResponse toCommentResponse(CommentModel comment);
}
```

---

### Step 6.5 — Create `CommentRepository`

Create `repository/CommentRepository.java` extending `JpaRepository<CommentModel, Long>`:

| Method | Return Type | Notes |
|---|---|---|
| `findByTaskIdOrderByCreatedAtDesc(Long taskId)` | `List<CommentModel>` | Returns newest first |
| `countByTaskId(Long taskId)` | `long` | |

---

### Step 6.6 — Create `CommentService` + `CommentServiceImpl`

Create `services/CommentService.java`:

```java
public interface CommentService {
    List<CommentResponse> findByTaskId(Long taskId);
    CommentResponse create(Long taskId, CommentRequest request);
    CommentResponse update(Long id, CommentRequest request);
    void delete(Long id);
}
```

Implement in `services/CommentServiceImpl.java`:

| Method | What It Does |
|---|---|
| `findByTaskId(Long taskId)` | Verify task exists (throw 404 if not), call `commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)`, map results |
| `create(Long taskId, CommentRequest)` | Verify task exists, build `CommentModel` with `task` set, save |
| `update(Long id, CommentRequest)` | Find comment or throw 404, update `content` and `author`, save |
| `delete(Long id)` | Find comment or throw 404, delete |

---

### Step 6.7 — Create `CommentController`

Create `controllers/CommentController.java`. Comments are nested under tasks for reads/creates, but updated/deleted by their own ID:

| Method | Path | Request Body | Response Body | Status |
|---|---|---|---|---|
| `GET` | `/api/tasks/{taskId}/comments` | — | `List<CommentResponse>` | 200 / 404 |
| `POST` | `/api/tasks/{taskId}/comments` | `CommentRequest` | `CommentResponse` | **201** / 404 |
| `PUT` | `/api/comments/{id}` | `CommentRequest` | `CommentResponse` | 200 / 404 |
| `DELETE` | `/api/comments/{id}` | — | — | **204** / 404 |

### ✅ Verify

```bash
./mvnw test -Dtest="CommentControllerTest"
```

---

## Complete Endpoint Summary (27 endpoints)

| Method | Path | Exercise |
|---|---|---|
| GET | `/api/tasks` | 1 |
| GET | `/api/tasks/{id}` | 1 |
| POST | `/api/tasks` | 1 |
| PUT | `/api/tasks/{id}` | 1 |
| DELETE | `/api/tasks/{id}` | 1 |
| GET | `/api/tasks/overdue` | 3 |
| GET | `/api/tasks/filter?status=&priority=&title=&projectId=&dueBefore=&labelId=&page=&size=` | 4 |
| PATCH | `/api/tasks/{id}/status?status=` | 5 |
| POST | `/api/tasks/{taskId}/labels/{labelId}` | 5 |
| DELETE | `/api/tasks/{taskId}/labels/{labelId}` | 5 |
| GET | `/api/tasks/{taskId}/comments` | 6 |
| POST | `/api/tasks/{taskId}/comments` | 6 |
| GET | `/api/projects` | 2 |
| GET | `/api/projects/{id}` | 2 |
| POST | `/api/projects` | 2 |
| PUT | `/api/projects/{id}` | 2 |
| DELETE | `/api/projects/{id}` | 2 |
| GET | `/api/projects/{id}/tasks` | 2 |
| GET | `/api/labels` | 5 |
| GET | `/api/labels/{id}` | 5 |
| POST | `/api/labels` | 5 |
| PUT | `/api/labels/{id}` | 5 |
| DELETE | `/api/labels/{id}` | 5 |
| GET | `/api/labels/{id}/tasks` | 5 |
| GET | `/api/dashboard/summary` | 3 |
| PUT | `/api/comments/{id}` | 6 |
| DELETE | `/api/comments/{id}` | 6 |

---

## Files to Create

| File | Exercise |
|---|---|
| `model/LabelModel.java` | 5 |
| `model/CommentModel.java` | 6 |
| `dto/ProjectRequest.java` | 2 |
| `dto/ProjectResponse.java` | 2 |
| `dto/LabelRequest.java` | 5 |
| `dto/LabelResponse.java` | 5 |
| `dto/CommentRequest.java` | 6 |
| `dto/CommentResponse.java` | 6 |
| `dto/DashboardResponse.java` | 3 |
| `dto/ProjectStatsView.java` | 3 |
| `mapper/ProjectMapper.java` | 2 |
| `mapper/LabelMapper.java` | 5 |
| `mapper/CommentMapper.java` | 6 |
| `repository/LabelRepository.java` | 5 |
| `repository/CommentRepository.java` | 6 |
| `services/TaskService.java` | 1 |
| `services/TaskServiceImpl.java` | 1 |
| `services/ProjectService.java` | 2 |
| `services/ProjectServiceImpl.java` | 2 |
| `services/DashboardService.java` | 3 |
| `services/DashboardServiceImpl.java` | 3 |
| `services/LabelService.java` | 5 |
| `services/LabelServiceImpl.java` | 5 |
| `services/CommentService.java` | 6 |
| `services/CommentServiceImpl.java` | 6 |
| `controllers/ProjectController.java` | 2 |
| `controllers/DashboardController.java` | 3 |
| `controllers/LabelController.java` | 5 |
| `controllers/CommentController.java` | 6 |

## Files to Modify

| File | What to Add | Exercise |
|---|---|---|
| `model/TaskModel.java` | `status`, `priority`, `dueDate` | 1 |
| `model/TaskModel.java` | `labels` (ManyToMany) | 5 |
| `model/TaskModel.java` | `comments` (OneToMany) | 6 |
| `dto/TaskRequest.java` | `projectId`, `priority`, `status`, `dueDate` | 1 |
| `dto/TaskResponse.java` | `status`, `priority`, `dueDate`, `labelNames`, `commentCount` | 1 |
| `dto/FilterTaskDto.java` | `status`, `priority`, `dueBefore`, `labelId` | 4 |
| `mapper/TaskMapper.java` | `labelNames` and `commentCount` mappings | 1 |
| `repository/TaskRepository.java` | Derived queries + `findOverdueTasks` | 1, 3 |
| `repository/ProjectRepository.java` | Derived queries + `getProjectStats()` | 2, 3 |
| `spec/TaskSpec.java` | `hasStatus`, `hasPriority`, `dueBefore`, `hasLabel` | 4 |
| `controllers/TaskController.java` | All task endpoints | 1, 3, 4, 5 |

---

## Seed Data Reference

### Projects (3)

| ID | Name |
|---|---|
| 1 | Task Management System |
| 2 | E-Commerce Platform |
| 3 | Company Website |

### Labels (6)

| ID | Name | Color |
|---|---|---|
| 1 | bug | #FF0000 |
| 2 | feature | #00FF00 |
| 3 | urgent | #FF6600 |
| 4 | backend | #0000FF |
| 5 | frontend | #FF00FF |
| 6 | documentation | #FFFF00 |

### Tasks (12)

| ID | Title | Status | Priority | Overdue? | Project | Labels |
|---|---|---|---|---|---|---|
| 1 | Design login page UI | DONE | HIGH | No | 1 | frontend, feature |
| 2 | Implement authentication API | IN_PROGRESS | URGENT | No | 1 | backend, feature |
| 3 | Set up database schema | DONE | HIGH | No | 1 | backend |
| 4 | Create task service layer | TODO | MEDIUM | No | 1 | backend, feature |
| 5 | Product listing page | DONE | MEDIUM | No | 2 | frontend |
| 6 | Shopping cart integration | IN_PROGRESS | HIGH | No | 2 | frontend, feature |
| 7 | Payment gateway integration | TODO | URGENT | **Yes** | 2 | backend, urgent |
| 8 | Order history page | DONE | LOW | No | 2 | frontend |
| 9 | Homepage redesign | TODO | MEDIUM | No | 3 | frontend, feature |
| 10 | Blog module setup | IN_PROGRESS | MEDIUM | **Yes** | 3 | backend, documentation |
| 11 | SEO optimization | TODO | LOW | No | 3 | documentation |
| 12 | Contact form integration | TODO | HIGH | **Yes** | 3 | backend, bug |

### Comments (9)

| Task | Author | Content |
|---|---|---|
| 1 | Alice | Wireframe looks great! |
| 1 | Bob | Let's finalize the color scheme. |
| 2 | Charlie | JWT implementation is tricky. |
| 2 | Alice | Should we use refresh tokens? |
| 5 | Bob | Grid layout looks clean. |
| 6 | Alice | Cart state should persist. |
| 6 | Charlie | Consider using localStorage. |
| 9 | Bob | Check competitor websites. |
| 12 | Charlie | Email delivery is failing on staging. |

### Quick Reference Counts

| Query | Count |
|---|---|
| Total tasks | 12 |
| Status TODO | 5 |
| Status IN_PROGRESS | 3 |
| Status DONE | 4 |
| Priority URGENT | 2 |
| Priority LOW | 2 |
| Overdue (due before today, not DONE) | 3 |
| Project 1 tasks | 4 (2 DONE) |
| Project 2 tasks | 4 (1 DONE) |
| Project 3 tasks | 4 (0 DONE) |
| Label "backend" tasks | 6 |
| Label "frontend" tasks | 5 |
| Label "bug" tasks | 1 (task 12) |
| Task 1 comments | 2 |
| Task 3 comments | 0 |

### Dashboard Summary Response

```json
{
  "totalTasks": 12,
  "todoCount": 5,
  "inProgressCount": 3,
  "doneCount": 4,
  "overdueCount": 3,
  "projectStats": [
    { "projectName": "Task Management System", "taskCount": 4, "doneCount": 2 },
    { "projectName": "E-Commerce Platform",    "taskCount": 4, "doneCount": 1 },
    { "projectName": "Company Website",        "taskCount": 4, "doneCount": 0 }
  ]
}
```

---

## Spring Security — Exercises 7–11

**Concepts:** JWT authentication, role-based authorization, input validation, task/comment ownership

---

## Authorization Model

| Role | Access |
|---|---|
| (unauthenticated) | `/api/auth/**` only |
| USER | All `GET` endpoints (except `/api/dashboard/**`) |
| ADMIN | All endpoints including dashboard + all writes |

---

## How to Extract the Current User from a JWT Token

After `JwtAuthenticationFilter` validates the token and stores the authentication in `SecurityContextHolder`, any service can retrieve the currently logged-in user:

```java
// Direct approach (in a service or controller):
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
UserModel currentUser = (UserModel) auth.getPrincipal();
String email = currentUser.getEmail();
```

> **Why this works:** `JwtAuthenticationFilter` creates a `UsernamePasswordAuthenticationToken` using the `UserModel` object (returned by `UserDetailsService`) as the principal. Casting `auth.getPrincipal()` to `UserModel` is safe because `JwtAuthenticationFilter` always stores the full `UserModel`, not just a String.

### SecurityUtils Helper (Exercise 11)

Rather than repeating the cast in every service, you will create a `SecurityUtils` component:

```java
@Component
public class SecurityUtils {
    public Optional<UserModel> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof UserModel)) {
            return Optional.empty();
        }
        return Optional.of((UserModel) auth.getPrincipal());
    }

    public UserModel requireCurrentUser() {
        return getCurrentUser()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }
}
```

> **Note:** The `instanceof` check is important because `@WithMockUser` in tests stores the username as a `String`, not a `UserModel`. Without this check your tests will throw `ClassCastException`.

Use in a service:
```java
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final SecurityUtils securityUtils;

    @Override
    public TaskResponse create(TaskRequest request) {
        UserModel currentUser = securityUtils.requireCurrentUser();
        TaskModel task = new TaskModel();
        task.setOwner(currentUser);
        // ... rest of create logic
    }
}
```

---

## Exercise 7: Security Dependencies + User Foundation

**Concepts:** Spring Security auto-configuration, `UserDetails`, BCrypt

---

### Step 7.1 — Add Maven Dependencies

Add to `pom.xml` inside `<dependencies>`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Security Test (@WithMockUser) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JWT (JJWT 0.12.5) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>

<!-- Input Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

### Step 7.2 — Create `Role` Enum

Create `model/Role.java`:

```java
public enum Role {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
```

Spring Security's `hasRole("ADMIN")` checks for `ROLE_ADMIN` authority — `getAuthority()` provides that prefix.

---

### Step 7.3 — Create `UserModel` Entity

Create `model/UserModel.java`. It implements `UserDetails` so Spring Security can load and verify it:

```java
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean enabled = true;
    private boolean accountLocked = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !accountLocked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
```

> `getUsername()` returns `email` — email is the username identity in this system.

---

### Step 7.4 — Create `UserRepository`

Create `repository/UserRepository.java`:

```java
@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

---

### Step 7.5 — Create Auth DTOs

Create in `dto/` package using `@Data @NoArgsConstructor @AllArgsConstructor` (consistent with codebase style):

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
}
```

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
```

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private String email;
    private String role;
}
```

---

### Step 7.6 — Create `CustomUserDetailsService`

Create `security/CustomUserDetailsService.java`:

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found: " + username));
    }
}
```

### ✅ Verify

After adding the dependency (Step 7.1) and restarting the app, ALL endpoints will return 401:

```bash
curl http://localhost:8080/api/tasks
# → 401 Unauthorized (Spring Security default page or JSON)
```

This is expected! Spring Security auto-protects all endpoints when the starter is on the classpath.

---

## Exercise 8: JWT Service + Authentication Filter

**Concepts:** JWT generation/validation, `OncePerRequestFilter`, `SecurityContextHolder`

---

### Step 8.1 — Add JWT Configuration

Add to `application.properties` (or `application-dev.properties`):

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

> **Important:** In production, use `jwt.secret=${JWT_SECRET}` and set the secret via environment variable. Never commit real secrets.

---

### Step 8.2 — Create `JwtProperties`

Create `config/JwtProperties.java`:

```java
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class JwtProperties {

    private String secret;
    private long expiration = 86400000;       // 24 hours
    private long refreshExpiration = 604800000; // 7 days
}
```

Also add `@EnableConfigurationProperties(JwtProperties.class)` to your main application class.

---

### Step 8.3 — Create `JwtService`

Create `security/JwtService.java`:

```java
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
            .signWith(getSigningKey())
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
```

---

### Step 8.4 — Create `JwtAuthenticationFilter`

Create `security/JwtAuthenticationFilter.java`. This filter runs once per request, extracts the JWT from the `Authorization` header, validates it, and loads the user into the `SecurityContextHolder`:

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String email = jwtService.extractUsername(jwt);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

> **Key concept:** The filter does not reject invalid tokens — it simply does nothing, leaving the request unauthenticated. The `SecurityConfig` (Exercise 9) decides which endpoints require authentication.

---

### Step 8.5 — Create `JwtAuthenticationEntryPoint`

Create `security/JwtAuthenticationEntryPoint.java`. This is called when an unauthenticated request hits a protected endpoint — it returns a JSON 401 using the existing `ErrorResponse`:

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = new ErrorResponse(
            401, "Authentication required", LocalDateTime.now());
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
```

---

## Exercise 9: SecurityConfig + Auth Endpoints

**Concepts:** `SecurityFilterChain`, `AuthenticationManager`, `PasswordEncoder`, auth endpoints

---

### Step 9.1 — Create `SecurityConfig`

Create `config/SecurityConfig.java`. Start with a basic configuration that requires authentication for all endpoints except `/api/auth/**`. You will expand the role-based rules in Exercise 11:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

### Step 9.2 — Create `AuthService` + `AuthServiceImpl`

Create `services/AuthService.java`:

```java
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
```

Create `services/AuthServiceImpl.java`:

```java
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Email already exists: " + request.getEmail());
        }
        UserModel user = new UserModel();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()));
        UserModel user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(UserModel user) {
        return new AuthResponse(
            jwtService.generateToken(user),
            jwtService.generateRefreshToken(user),
            jwtProperties.getExpiration(),
            user.getEmail(),
            user.getRole().name()
        );
    }
}
```

---

### Step 9.3 — Create `AuthController`

Create `controllers/AuthController.java`:

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

---

### Step 9.4 — Fix Existing Tests

Adding Spring Security causes all existing tests to fail with 401. Add `@WithMockUser(roles = "ADMIN")` at the class level to each of the 5 existing test classes:

```java
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser(roles = "ADMIN")    // ← add this
class TaskControllerTest { ... }
```

Apply to: `TaskControllerTest`, `ProjectControllerTest`, `LabelControllerTest`, `CommentControllerTest`, `DashboardControllerTest`.

> **Why this works:** `@WithMockUser` bypasses the `JwtAuthenticationFilter` entirely and directly injects a mock `Authentication` into `SecurityContextHolder`. The mock user has no database representation — it only exists for the duration of the test.

### ✅ Verify

```bash
# All 35 original tests should pass again
./mvnw test

# Auth endpoints
./mvnw test -Dtest="AuthControllerTest"
```

---

## Exercise 10: Input Validation

**Concepts:** Bean Validation (`@Valid`, constraint annotations), `MethodArgumentNotValidException`

---

### Step 10.1 — Add Validation to `TaskRequest`

Open `dto/TaskRequest.java` and add constraint annotations:

```java
@Data @NoArgsConstructor @AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Positive(message = "Project ID must be positive")
    private Long projectId;

    private Priority priority;
    private TaskStatus status;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
}
```

---

### Step 10.2 — Add Validation to Other DTOs

**`dto/ProjectRequest.java`** — add `@NotBlank` on `name`

**`dto/CommentRequest.java`** — add `@NotBlank(message = "Content is required")` on `content`

**`dto/LabelRequest.java`** — add `@NotBlank(message = "Name is required")` on `name`

---

### Step 10.3 — Add `@Valid` to Controllers

In every controller, add `@Valid` to `@RequestBody` parameters:

```java
// TaskController
@PostMapping
public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) { ... }

@PutMapping("/{id}")
public ResponseEntity<TaskResponse> update(@PathVariable Long id,
        @Valid @RequestBody TaskRequest request) { ... }
```

Do the same for `ProjectController`, `CommentController`, and `LabelController`.

---

### Step 10.4 — Extend `ErrorResponse`

Update `dto/ErrorResponse.java` to support field-level validation errors:

```java
public class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    private List<FieldError> errors;

    // Existing constructor (unchanged)
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // New constructor for validation errors
    public ErrorResponse(int status, String message,
            LocalDateTime timestamp, List<FieldError> errors) {
        this(status, message, timestamp);
        this.errors = errors;
    }
}
```

Also add a `FieldError` record (can be a nested record or a separate file):

```java
public record FieldError(String field, String message) {}
```

Add getters for all fields (or annotate with `@Getter` if the class does not already use it).

---

### Step 10.5 — Update `GlobalExceptionHandler`

Add two new handlers to `exception/GlobalExceptionHandler.java` alongside the existing `ResourceNotFoundException` handler:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> new ErrorResponse.FieldError(e.getField(), e.getDefaultMessage()))
        .toList();
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(400, "Validation failed", LocalDateTime.now(), errors));
}

@ExceptionHandler(AuthenticationException.class)
public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse(401, ex.getMessage(), LocalDateTime.now()));
}
```

### ✅ Verify

```bash
# POST with empty body should return 400 with field errors
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{}'

# Expected:
# { "status": 400, "message": "Validation failed",
#   "errors": [{"field": "title", "message": "Title is required"}] }
```

---

## Exercise 11: Role-Based Authorization + Task & Comment Ownership

**Concepts:** Role-based access control, SecurityContext, extracting the current user from token

---

### Step 11.1 — Create `SecurityUtils`

Create `security/SecurityUtils.java`. This component provides a reusable way to retrieve the currently authenticated user from the `SecurityContextHolder`:

```java
@Component
public class SecurityUtils {

    /**
     * Returns the currently authenticated user, or empty if unauthenticated
     * or using @WithMockUser (which stores a String principal, not UserModel).
     */
    public Optional<UserModel> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof UserModel)) {
            return Optional.empty();
        }
        return Optional.of((UserModel) auth.getPrincipal());
    }

    public UserModel requireCurrentUser() {
        return getCurrentUser()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser()
            .map(user -> user.getRole() == Role.ADMIN)
            .orElse(false);
    }
}
```

---

### Step 11.2 — Add `owner` to `TaskModel`

Add to `model/TaskModel.java`:

```java
@ManyToOne
@JoinColumn(name = "owner_id")
private UserModel owner;
```

This adds a nullable `owner_id` FK column. Existing seeded tasks will have `null` owner, which is fine.

---

### Step 11.3 — Add `ownerEmail` to `TaskResponse` + Mapper

Add to `dto/TaskResponse.java`:
```java
private String ownerEmail;
```

Update `mapper/TaskMapper.java` — add this mapping to the `toTaskResponse` method:
```java
@Mapping(target = "ownerEmail", source = "owner.email")
```

MapStruct handles `null` safely — if `owner` is null, `ownerEmail` will be null.

---

### Step 11.4 — Set Owner in `TaskServiceImpl`

Inject `SecurityUtils` and set `owner` when creating a task. Open `services/TaskServiceImpl.java`:

```java
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    // ... existing fields ...
    private final SecurityUtils securityUtils;  // Add this

    @Override
    public TaskResponse create(TaskRequest request) {
        TaskModel task = new TaskModel();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        // ... existing field assignments ...
        securityUtils.getCurrentUser().ifPresent(task::setOwner);  // Set owner
        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
}
```

> **Why `getCurrentUser()` not `requireCurrentUser()`?** The `ADMIN` role security rule already guarantees the user is authenticated before reaching `create()`. Using `getCurrentUser()` (returning `Optional`) with `ifPresent` is also more resilient when tests use `@WithMockUser` (which stores a String principal, not a `UserModel`).

---

### Step 11.5 — Add `user` to `CommentModel`

Add to `model/CommentModel.java` (alongside the existing `author` String field):

```java
@ManyToOne
@JoinColumn(name = "user_id")
private UserModel user;
```

The existing `author` String field is kept for backward compatibility with seeded data.

---

### Step 11.6 — Add `authorEmail` to `CommentResponse` + Mapper

Add to `dto/CommentResponse.java`:
```java
private String authorEmail;
```

Update `mapper/CommentMapper.java`:
```java
@Mapping(target = "authorEmail", source = "user.email")
CommentResponse toCommentResponse(CommentModel comment);
```

---

### Step 11.7 — Set User in `CommentServiceImpl`

Inject `SecurityUtils` and set `user` when creating a comment. Open `services/CommentServiceImpl.java`:

```java
@Override
public CommentResponse create(Long taskId, CommentRequest request) {
    TaskModel task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
    CommentModel comment = new CommentModel();
    comment.setContent(request.getContent());
    comment.setAuthor(request.getAuthor());  // Keep existing author field
    comment.setTask(task);
    securityUtils.getCurrentUser().ifPresent(comment::setUser);  // Link to user
    return commentMapper.toCommentResponse(commentRepository.save(comment));
}
```

---

### Step 11.8 — Expand `SecurityConfig` with Role-Based Rules

Update `config/SecurityConfig.java` — replace the `.authorizeHttpRequests` block:

```java
.authorizeHttpRequests(auth -> auth
    // Public — no authentication required
    .requestMatchers("/api/auth/**").permitAll()

    // ADMIN only — dashboard
    .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasRole("ADMIN")

    // ADMIN only — all mutating operations
    .requestMatchers(HttpMethod.POST,   "/api/tasks/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,    "/api/tasks/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PATCH,  "/api/tasks/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.POST,   "/api/projects/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,    "/api/projects/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.POST,   "/api/labels/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,    "/api/labels/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/labels/**").hasRole("ADMIN")

    .requestMatchers(HttpMethod.POST,   "/api/comments/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PUT,    "/api/comments/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasRole("ADMIN")

    // Authenticated users (USER or ADMIN) — all remaining GETs
    .anyRequest().authenticated()
)
```

> **Order matters:** Rules are evaluated top-to-bottom. More specific rules (`/api/dashboard/**`) must appear before the catch-all `anyRequest()`.

### ✅ Verify

```bash
./mvnw test -Dtest="SecurityAccessTest"
./mvnw test  # All tests pass
```

Manual test with different roles:

```bash
# Register as USER
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"password123"}' | jq -r .token)

# USER can view tasks → 200
curl http://localhost:8080/api/tasks -H "Authorization: Bearer $TOKEN"

# USER cannot create a task → 403
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Forbidden"}'

# USER cannot view dashboard → 403
curl http://localhost:8080/api/dashboard/summary -H "Authorization: Bearer $TOKEN"
```

---

## Updated Endpoint Summary (31 endpoints)

| Method | Path | Auth Required | Role |
|---|---|---|---|
| POST | `/api/auth/register` | No | — |
| POST | `/api/auth/login` | No | — |
| GET | `/api/tasks` | Yes | USER or ADMIN |
| GET | `/api/tasks/{id}` | Yes | USER or ADMIN |
| POST | `/api/tasks` | Yes | ADMIN |
| PUT | `/api/tasks/{id}` | Yes | ADMIN |
| DELETE | `/api/tasks/{id}` | Yes | ADMIN |
| GET | `/api/tasks/overdue` | Yes | USER or ADMIN |
| GET | `/api/tasks/filter` | Yes | USER or ADMIN |
| PATCH | `/api/tasks/{id}/status` | Yes | ADMIN |
| POST | `/api/tasks/{taskId}/labels/{labelId}` | Yes | ADMIN |
| DELETE | `/api/tasks/{taskId}/labels/{labelId}` | Yes | ADMIN |
| GET | `/api/tasks/{taskId}/comments` | Yes | USER or ADMIN |
| POST | `/api/tasks/{taskId}/comments` | Yes | ADMIN |
| GET | `/api/projects` | Yes | USER or ADMIN |
| GET | `/api/projects/{id}` | Yes | USER or ADMIN |
| POST | `/api/projects` | Yes | ADMIN |
| PUT | `/api/projects/{id}` | Yes | ADMIN |
| DELETE | `/api/projects/{id}` | Yes | ADMIN |
| GET | `/api/projects/{id}/tasks` | Yes | USER or ADMIN |
| GET | `/api/labels` | Yes | USER or ADMIN |
| GET | `/api/labels/{id}` | Yes | USER or ADMIN |
| POST | `/api/labels` | Yes | ADMIN |
| PUT | `/api/labels/{id}` | Yes | ADMIN |
| DELETE | `/api/labels/{id}` | Yes | ADMIN |
| GET | `/api/labels/{id}/tasks` | Yes | USER or ADMIN |
| GET | `/api/dashboard/summary` | Yes | ADMIN |
| PUT | `/api/comments/{id}` | Yes | ADMIN |
| DELETE | `/api/comments/{id}` | Yes | ADMIN |

---

## New Files to Create (Exercises 7–11)

| File | Exercise |
|---|---|
| `model/Role.java` | 7 |
| `model/UserModel.java` | 7 |
| `repository/UserRepository.java` | 7 |
| `dto/RegisterRequest.java` | 7 |
| `dto/LoginRequest.java` | 7 |
| `dto/AuthResponse.java` | 7 |
| `security/CustomUserDetailsService.java` | 7 |
| `config/JwtProperties.java` | 8 |
| `security/JwtService.java` | 8 |
| `security/JwtAuthenticationFilter.java` | 8 |
| `security/JwtAuthenticationEntryPoint.java` | 8 |
| `config/SecurityConfig.java` | 9 |
| `services/AuthService.java` | 9 |
| `services/AuthServiceImpl.java` | 9 |
| `controllers/AuthController.java` | 9 |
| `security/SecurityUtils.java` | 11 |

## Files to Modify (Exercises 7–11)

| File | What to Add | Exercise |
|---|---|---|
| `pom.xml` | security, jjwt, validation, spring-security-test deps | 7 |
| `application.properties` | `jwt.*` config | 8 |
| `SunriseTaskFlowApiApplication.java` | `@EnableConfigurationProperties(JwtProperties.class)` | 8 |
| `dto/ErrorResponse.java` | `errors` field + `FieldError` record | 10 |
| `exception/GlobalExceptionHandler.java` | validation + auth exception handlers | 10 |
| `dto/TaskRequest.java` | `@NotBlank`, `@Size`, `@Positive`, `@FutureOrPresent` | 10 |
| `dto/ProjectRequest.java` | `@NotBlank` on `name` | 10 |
| `dto/CommentRequest.java` | `@NotBlank` on `content` | 10 |
| `dto/LabelRequest.java` | `@NotBlank` on `name` | 10 |
| `controllers/TaskController.java` | `@Valid` on all `@RequestBody` | 10 |
| `controllers/ProjectController.java` | `@Valid` on all `@RequestBody` | 10 |
| `controllers/CommentController.java` | `@Valid` on all `@RequestBody` | 10 |
| `controllers/LabelController.java` | `@Valid` on all `@RequestBody` | 10 |
| `config/SecurityConfig.java` | Role-based `.authorizeHttpRequests` | 11 |
| `model/TaskModel.java` | `owner` ManyToOne to `UserModel` | 11 |
| `dto/TaskResponse.java` | `ownerEmail` String | 11 |
| `mapper/TaskMapper.java` | `@Mapping(target="ownerEmail", source="owner.email")` | 11 |
| `services/TaskServiceImpl.java` | inject `SecurityUtils`, set owner on create | 11 |
| `model/CommentModel.java` | `user` ManyToOne to `UserModel` (nullable) | 11 |
| `dto/CommentResponse.java` | `authorEmail` String | 11 |
| `mapper/CommentMapper.java` | `@Mapping(target="authorEmail", source="user.email")` | 11 |
| `services/CommentServiceImpl.java` | inject `SecurityUtils`, set user on create | 11 |
| `TaskControllerTest.java` | `@WithMockUser(roles = "ADMIN")` at class level | 9 |
| `ProjectControllerTest.java` | `@WithMockUser(roles = "ADMIN")` at class level | 9 |
| `LabelControllerTest.java` | `@WithMockUser(roles = "ADMIN")` at class level | 9 |
| `CommentControllerTest.java` | `@WithMockUser(roles = "ADMIN")` at class level | 9 |
| `DashboardControllerTest.java` | `@WithMockUser(roles = "ADMIN")` at class level | 9 |
