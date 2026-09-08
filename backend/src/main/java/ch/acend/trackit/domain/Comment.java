package ch.acend.trackit.domain;

import java.time.Instant;

/** One comment on a task in TrackIt. */
public record Comment(long id, long taskId, String author, String body, Instant createdAt) {
}
