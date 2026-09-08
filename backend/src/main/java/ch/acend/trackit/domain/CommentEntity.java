package ch.acend.trackit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

/**
 * The stored shape of a comment. Deliberately a separate class from the {@link Comment}
 * record: the record is the API shape and must not change when the storage does.
 */
@Entity
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    private String author;

    private String body;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    protected CommentEntity() {
        // JPA requires a no-arg constructor.
    }

    public CommentEntity(Long taskId, String author, String body) {
        this.taskId = taskId;
        this.author = author;
        this.body = body;
    }

    public Comment toDomain() {
        return new Comment(id, taskId, author, body, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
