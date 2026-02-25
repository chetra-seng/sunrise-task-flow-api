# Task: Build a Task Management REST API

## Objective

Extend the existing Spring Boot project to build a full CRUD REST API for managing tasks.
By the end, you should have all endpoints working, test them manually with Postman, and then verify everything passes the provided JUnit tests.

---

## What You Will Build

A `Task` API with these endpoints:

| Method | URL                        | Description            | Status Code    |
|--------|----------------------------|------------------------|----------------|
| GET    | `/api/tasks`               | List all tasks         | 200            |
| GET    | `/api/tasks/{id}`          | Get task by ID         | 200 / 404      |
| POST   | `/api/tasks`               | Create a new task      | 201 Created    |
| PUT    | `/api/tasks/{id}`          | Update a task          | 200 / 404      |
| PATCH  | `/api/tasks/{id}/complete` | Mark task as complete  | 200 / 404      |
| DELETE | `/api/tasks/{id}`          | Delete a task          | 204 / 404      |
| GET    | `/api/tasks?completed=true`| Filter by status       | 200            |

---

## Step 1: Create the Task Model

Create `src/main/java/.../model/Task.java` with these fields:

| Field | Type | Notes |
|-------|------|-------|
| `id` | `Long` | auto-assigned |
| `title` | `String` | |
| `description` | `String` | |
| `completed` | `boolean` | defaults to `false` |
| `createdAt` | `LocalDateTime` | set at creation time |

Use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`).

Also add a convenience constructor that takes only `(Long id, String title, String description)` and sets `completed = false` and `createdAt = LocalDateTime.now()` automatically.

---

## Step 2: Create the DTOs

DTOs (Data Transfer Objects) separate what the **client sees** from what your **model stores internally**. This is the same pattern used in `UserInfoDto` — notice how it hides the `password` field and renames `name` to `fullName`.

For tasks, you need **two** DTOs:

### TaskRequest (what comes IN)

Create `src/main/java/.../dto/TaskRequest.java` with only the fields the client should send:

- `title` (String)
- `description` (String)

> **Why not include `id` or `completed`?** Because the server controls those — the client shouldn't set them.

### TaskResponse (what goes OUT)

Create `src/main/java/.../dto/TaskResponse.java` with the fields the client should see:

- `id` (Long)
- `title` (String)
- `description` (String)
- `completed` (boolean)
- `createdAt` (LocalDateTime)

> **Why not return the Task model directly?** Right now they look the same, but later you might add internal fields to the model (e.g., `updatedBy`, `version`) that you don't want to expose. Using a DTO keeps your API stable.

**Hint:** Look at how `UserInfoDto` is structured — use the same Lombok annotations.

---

## Step 3: Create a Mock Repository

Since we don't have a database yet, create `src/main/java/.../repository/TaskRepository.java` to act as an in-memory data store.

> **Important:** The store must start **empty** — no pre-loaded data. The tests expect `GET /api/tasks` to return `[]` on a fresh start. Each test creates its own data.

Your repository needs:

- A `Map<Long, Task>` to store tasks (use `ConcurrentHashMap`)
- An `AtomicLong` counter starting at `1` to auto-generate IDs
- **No constructor** (or an empty one) — do NOT pre-load any tasks

Implement these 4 methods:

| Method | What it does |
|--------|-------------|
| `findAll()` | Returns all tasks from the map as a `List<Task>` |
| `findById(Long id)` | Returns `Optional<Task>` — use `Optional.ofNullable(...)` to handle missing keys |
| `save(Task task)` | If the task has no ID, assign one using the counter. Then put it in the map and return it |
| `delete(Long id)` | Remove the task from the map. Return `true` if it existed, `false` if not |

**Hint:** Look at how `UserServiceImpl` stores its list of users. This is similar, but uses a `Map` so you can look up tasks by ID quickly.

The service layer will call this repository — not the `Map` directly.

---

## Step 4: Create the TaskMapper

Create `src/main/java/.../mapper/TaskMapper.java` to convert between the `Task` model and your DTOs.

This follows the same pattern as `UserMapper`. Look at how `UserMapper`:
- Is an **interface** (not a class)
- Uses `@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)` so Spring can inject it
- Has a method `toUserDto(UserModel user)` that returns `UserInfoDto`

Your `TaskMapper` needs one method:

| Method | Input | Output |
|--------|-------|--------|
| `toTaskResponse` | `Task` | `TaskResponse` |

> **Do you need `@Mapping` here?** Look at `UserMapper` — it uses `@Mapping(target = "fullName", source = "name")` because the field names are **different**. If your Task model and TaskResponse have the **same** field names, MapStruct handles it automatically — no `@Mapping` needed.

---

## Step 5: Create the TaskService Interface

Create `src/main/java/.../services/TaskService.java`:

Notice the return types use `TaskResponse` (the DTO), **not** `Task` (the model). The controller should never see the internal model — only the DTO.

```java
public interface TaskService {
    List<TaskResponse> findAll();
    Optional<TaskResponse> findById(Long id);
    TaskResponse create(String title, String description);
    Optional<TaskResponse> update(Long id, String title, String description);
    Optional<TaskResponse> complete(Long id);
    boolean delete(Long id);
}
```

---

## Step 6: Implement TaskServiceImpl

Create `src/main/java/.../services/TaskServiceImpl.java`:

- Annotate with `@Service`
- Inject **both** `TaskRepository` and `TaskMapper` via constructor (see how `UserServiceImpl` injects `UserMapper`)
- Implement all methods from the interface by delegating to the repository

The service methods that return data should **convert** `Task` → `TaskResponse` using the mapper before returning. Look at how `UserServiceImpl.getAllUsers()` uses `userMapper::toUserDto` — you'll do the same with your `TaskMapper`.

Key logic:
- `create` — build a new `Task` using the convenience constructor, save it, then **map to TaskResponse** before returning
- `update` — find by ID, update fields, save, then **map to TaskResponse**
- `complete` — find by ID, set `completed = true`, save, then **map to TaskResponse**
- `delete` — call `repository.delete(id)`, return the boolean result (no mapping needed here)

---

## Step 7: Create the TaskController

Create `src/main/java/.../controllers/TaskController.java`:

- Annotate with `@RestController` and `@RequestMapping("/api/tasks")`
- Inject `TaskService` via constructor (the controller only talks to the service — never to the repository or mapper directly)
- Implement all 7 endpoints from the table above

### New concept: ResponseEntity

In `HelloController` and `UserController`, we just returned a value directly:

```java
@GetMapping("/hello")
public String getHello() {
    return "Hello!";  // Spring automatically sends 200 OK
}
```

That works when every response is **200 OK**. But for a REST API, different actions need different status codes (201 for created, 204 for deleted, 404 for not found). To control the status code, we use `ResponseEntity`:

```java
// Return 200 OK with a body
return ResponseEntity.ok(task);

// Return 201 Created with a body
return ResponseEntity.status(HttpStatus.CREATED).body(task);

// Return 204 No Content (empty body)
return ResponseEntity.noContent().build();

// Return 404 Not Found (empty body)
return ResponseEntity.notFound().build();
```

When you use `ResponseEntity`, your method return type changes. Compare:

```java
// Before (always 200)
@GetMapping("/{id}")
public TaskResponse getTask(@PathVariable Long id) { ... }

// After (can be 200 or 404)
@GetMapping("/{id}")
public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) { ... }
```

### How to handle "found or not found"

Several endpoints need to return **200 if found** or **404 if not found**. Your service returns `Optional<TaskResponse>` — here's how to use it:

```java
@GetMapping("/{id}")
public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
    return taskService.findById(id)
            .map(ResponseEntity::ok)                    // found → 200 with body
            .orElse(ResponseEntity.notFound().build());  // not found → 404
}
```

> **What is `@PathVariable`?** It grabs the `{id}` part from the URL. So `GET /api/tasks/3` means `id = 3`.

### How to receive a JSON body with @RequestBody

POST and PUT requests send data in the **request body** as JSON. To read it, use `@RequestBody` with your `TaskRequest` DTO:

```java
@PostMapping
public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse task = taskService.create(request.getTitle(), request.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
}
```

Here's what happens step by step:
1. The client sends `{"title":"Buy groceries","description":"Milk, eggs"}` in the body
2. `@RequestBody` tells Spring to convert that JSON into a `TaskRequest` object
3. You call `request.getTitle()` and `request.getDescription()` to get the values

> **Without `@RequestBody`, Spring won't read the JSON** — your `title` and `description` will be `null`.

PUT works the same way — it also needs `@RequestBody TaskRequest request` as a parameter.

### Endpoints to implement

Use the patterns above to implement each endpoint:

| Method | Annotation | Returns | Status codes |
|--------|-----------|---------|-------------|
| GET all | `@GetMapping` | `List<TaskResponse>` | always 200 |
| GET by ID | `@GetMapping("/{id}")` | `ResponseEntity<TaskResponse>` | 200 / 404 |
| POST | `@PostMapping` | `ResponseEntity<TaskResponse>` | 201 |
| PUT | `@PutMapping("/{id}")` | `ResponseEntity<TaskResponse>` | 200 / 404 |
| PATCH complete | `@PatchMapping("/{id}/complete")` | `ResponseEntity<TaskResponse>` | 200 / 404 |
| DELETE | `@DeleteMapping("/{id}")` | `ResponseEntity<Void>` | 204 / 404 |

> **Tip for DELETE:** The service returns `boolean` (true = deleted, false = not found). Use an `if/else` to return 204 or 404.

### Filtering with @RequestParam

For `GET /api/tasks`, add an optional `completed` query parameter:

```java
@GetMapping
public List<TaskResponse> getAllTasks(
    @RequestParam(required = false) Boolean completed) {
    return taskService.findAll().stream()
        .filter(t -> completed == null || t.isCompleted() == completed)
        .toList();
}
```

> **What is `@RequestParam`?** It reads query string values from the URL. So `GET /api/tasks?completed=true` means `completed = true`. When `required = false`, it's `null` if not provided.

> **Notice:** The controller works with `TaskResponse` — it never touches the `Task` model. The mapping happens inside the service layer.

---

## Step 8: Test Manually with Postman

Before running the automated tests, verify your API works by hand using **Postman**.

1. **Start the app** — In IntelliJ, click the green **Run** button on `SunriseTaskFlowApiApplication`
2. The server starts on **http://localhost:9999**
3. Open Postman and try these requests in order:

| # | Method | URL | Body (JSON) | What you should see |
|---|--------|-----|-------------|-------------------|
| 1 | GET | `http://localhost:9999/api/tasks` | — | Empty list `[]` |
| 2 | POST | `http://localhost:9999/api/tasks` | `{"title":"Buy groceries","description":"Milk, eggs"}` | 201 Created, returns the task with `id`, `completed: false`, `createdAt` |
| 3 | GET | `http://localhost:9999/api/tasks` | — | List with 1 task |
| 4 | GET | `http://localhost:9999/api/tasks/1` | — | The task you just created |
| 5 | GET | `http://localhost:9999/api/tasks/999` | — | 404 Not Found |
| 6 | PUT | `http://localhost:9999/api/tasks/1` | `{"title":"Buy food","description":"Updated list"}` | 200, title and description changed |
| 7 | PATCH | `http://localhost:9999/api/tasks/1/complete` | — | 200, `completed: true` |
| 8 | GET | `http://localhost:9999/api/tasks?completed=true` | — | Only completed tasks |
| 9 | DELETE | `http://localhost:9999/api/tasks/1` | — | 204 No Content (empty response) |
| 10 | GET | `http://localhost:9999/api/tasks/1` | — | 404 Not Found (it's deleted) |

> **Tip:** For POST and PUT, set the `Content-Type` header to `application/json` in Postman, or select "raw" > "JSON" in the body tab.

If all 10 checks work as expected, you're ready for the automated tests.

---

## Step 9: Run the Automated Tests

Once Postman looks good, run the JUnit tests to make sure everything is correct.

**In IntelliJ:**
1. Open `src/test/java/.../TaskControllerTest.java`
2. Click the green **Run** button next to the class name (runs all tests)
3. You can also click the green button next to any individual test to run just that one

> **Note:** You do NOT need the server running for this — the tests start their own temporary server automatically.

All 15 tests should pass (green checkmarks). Each test is isolated: Spring resets between tests so data never leaks between cases.

If a test fails, read the assertion error carefully — it tells you exactly which endpoint returned the wrong status code or response body.

---

## Checklist Before Running Tests

- [ ] `Task` model created with all 5 fields
- [ ] `TaskRequest` DTO created (title, description)
- [ ] `TaskResponse` DTO created (id, title, description, completed, createdAt)
- [ ] `TaskMapper` created with `toTaskResponse` method
- [ ] `TaskRepository` created with **empty** store and `save`/`findAll`/`findById`/`delete` methods
- [ ] `TaskService` interface returns `TaskResponse` (not `Task`)
- [ ] `TaskServiceImpl` injects `TaskRepository` **and** `TaskMapper`, implements all methods
- [ ] `TaskController` has all 7 endpoints, works with `TaskResponse` only
- [ ] POST returns 201 with body
- [ ] GET by ID returns 404 for missing tasks
- [ ] DELETE returns 204 on success
- [ ] PATCH /complete marks task as completed
- [ ] GET supports `?completed=` filter

## Tests Overview

| Test | What it checks |
|------|---------------|
| `getAllTasks_initially_returnsEmptyList` | GET returns `[]` on fresh start |
| `getAllTasks_afterCreatingTwo_returnsBothTasks` | GET returns all created tasks |
| `createTask_validRequest_returns201WithBody` | POST returns 201, full body with all fields |
| `getTaskById_existingTask_returns200` | GET by ID returns correct task |
| `getTaskById_nonExistingId_returns404` | GET with unknown ID returns 404 |
| `updateTask_existingTask_returns200WithUpdatedFields` | PUT updates title and description |
| `updateTask_nonExistingId_returns404` | PUT with unknown ID returns 404 |
| `completeTask_existingTask_returns200AndCompletedIsTrue` | PATCH sets `completed=true` |
| `completeTask_nonExistingId_returns404` | PATCH with unknown ID returns 404 |
| `deleteTask_existingTask_returns204` | DELETE returns 204 No Content |
| `deleteTask_existingTask_isNoLongerRetrievable` | Deleted task returns 404 on GET |
| `deleteTask_nonExistingId_returns404` | DELETE with unknown ID returns 404 |
| `getAllTasks_filterCompletedTrue_returnsOnlyCompletedTasks` | `?completed=true` filters correctly |
| `getAllTasks_filterCompletedFalse_returnsOnlyPendingTasks` | `?completed=false` filters correctly |
| `getAllTasks_noFilter_returnsAllTasks` | No filter returns all tasks |
