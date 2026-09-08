package ch.acend.trackit.service;

import ch.acend.trackit.domain.Task;
import ch.acend.trackit.domain.TaskEntity;
import ch.acend.trackit.domain.TaskStatus;
import ch.acend.trackit.dto.CreateTaskRequest;
import ch.acend.trackit.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for tasks, backed by PostgreSQL. */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task create(CreateTaskRequest request) {
        TaskEntity entity = new TaskEntity(request.title(), request.project(), TaskStatus.OPEN);
        return taskRepository.save(entity).toDomain();
    }

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll().stream()
                .map(TaskEntity::toDomain)
                .toList();
    }
}
