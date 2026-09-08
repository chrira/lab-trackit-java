package ch.acend.trackit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String project;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    protected TaskEntity() {
    }

    public TaskEntity(String title, String project, TaskStatus status) {
        this.title = title;
        this.project = project;
        this.status = status;
    }

    public Task toDomain() {
        return new Task(id, title, project, status);
    }
}
