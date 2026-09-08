package ch.acend.trackit.service;

/** No task exists with the given id. */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long taskId) {
        super("No task with id " + taskId);
    }
}
