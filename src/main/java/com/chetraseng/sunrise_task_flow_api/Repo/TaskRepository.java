package com.chetraseng.sunrise_task_flow_api.Repo;

import com.chetraseng.sunrise_task_flow_api.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {
    private final Map<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

//    public TaskRepository() {
//        Task t1 = new Task(nextId.getAndIncrement(), "Buy groceries", "Milk, eggs, bread");
//        Task t2 = new Task(nextId.getAndIncrement(), "Read a book", "Finish Clean Code");
//        Task t3 = new Task(nextId.getAndIncrement(), "Exercise", "30 min run");
//        t3.setCompleted(true);
//        store.put(t1.getId(), t1);
//        store.put(t2.getId(), t2);
//        store.put(t3.getId(), t3);
//    }

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Task save(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId.getAndIncrement());
        }
        store.put(task.getId(), task);
        return task;
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}
