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
