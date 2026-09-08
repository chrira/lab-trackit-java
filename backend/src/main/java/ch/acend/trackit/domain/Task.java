package ch.acend.trackit.domain;

public record Task(long id, String title, String project, TaskStatus status) {
}
