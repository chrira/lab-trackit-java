package ch.acend.trackit.domain;

/** One task in TrackIt. */
public record Task(long id, String title, String project, TaskStatus status) {
}
