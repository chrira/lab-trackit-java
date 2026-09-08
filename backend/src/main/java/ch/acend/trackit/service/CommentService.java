package ch.acend.trackit.service;

import ch.acend.trackit.domain.Comment;
import ch.acend.trackit.domain.CommentEntity;
import ch.acend.trackit.dto.CreateCommentRequest;
import ch.acend.trackit.repository.CommentRepository;
import ch.acend.trackit.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for comments on a task. */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Comment create(long taskId, CreateCommentRequest request) {
        requireTask(taskId);
        CommentEntity saved =
                commentRepository.save(new CommentEntity(taskId, request.author(), request.body()));
        return saved.toDomain();
    }

    @Transactional(readOnly = true)
    public List<Comment> findByTask(long taskId) {
        requireTask(taskId);
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(CommentEntity::toDomain)
                .toList();
    }

    private void requireTask(long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
    }
}
