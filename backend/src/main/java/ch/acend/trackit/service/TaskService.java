package ch.acend.trackit.service;

import ch.acend.trackit.domain.Task;
import ch.acend.trackit.domain.TaskStatus;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * In-memory store for tasks. The bean is a singleton shared across requests, so the
 * list and the id counter are both concurrent. Lab 1.2 moves this into PostgreSQL.
 */
@Service
public class TaskService {

    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Task create(String title, String project) {
        Task task = new Task(nextId.getAndIncrement(), title, project, TaskStatus.OPEN);
        tasks.add(task);
        return task;
    }

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }
}
