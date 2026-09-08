package ch.acend.trackit.web;

import ch.acend.trackit.domain.Comment;
import ch.acend.trackit.dto.CreateCommentRequest;
import ch.acend.trackit.dto.UpdateCommentRequest;
import ch.acend.trackit.service.CommentNotFoundException;
import ch.acend.trackit.service.CommentService;
import ch.acend.trackit.service.TaskNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@PathVariable long taskId, @Valid @RequestBody CreateCommentRequest request) {
        return commentService.create(taskId, request);
    }

    @GetMapping
    public List<Comment> listComments(@PathVariable long taskId) {
        return commentService.findByTask(taskId);
    }

    @PatchMapping("/{commentId}")
    public Comment updateComment(
            @PathVariable long taskId, @PathVariable long commentId, @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.update(taskId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long taskId, @PathVariable long commentId) {
        commentService.delete(taskId, commentId);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleTaskNotFound() {
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleCommentNotFound() {
    }
}
