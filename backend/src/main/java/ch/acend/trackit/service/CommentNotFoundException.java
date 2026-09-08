package ch.acend.trackit.service;

/** No comment exists with the given id on the given task. */
public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(long taskId, long commentId) {
        super("No comment with id " + commentId + " on task " + taskId);
    }
}
