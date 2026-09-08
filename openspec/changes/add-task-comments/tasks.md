## 1. Persistence

- [ ] 1.1 Add `backend/src/main/resources/db/migration/V2__create_comment.sql`
      creating the `comment` table (`id` BIGSERIAL PK, `task_id` BIGINT NOT
      NULL REFERENCES `task(id)` ON DELETE CASCADE, `author` VARCHAR(255) NOT
      NULL, `body` VARCHAR(500) NOT NULL, `created_at` TIMESTAMPTZ NOT NULL)
      and verify `./mvnw -q test` still starts Flyway cleanly
- [ ] 1.2 Add `domain/Comment.java` as a record `(long id, long taskId, String
      author, String body, Instant createdAt)` and verify it compiles with no
      framework annotations
- [ ] 1.3 Add `domain/CommentEntity.java` as the separate JPA entity mapped to
      `comment`, with a `toDomain()` method, mirroring `TaskEntity`, and verify
      it compiles
- [ ] 1.4 Add `repository/CommentRepository.java` extending
      `JpaRepository<CommentEntity, Long>` with
      `findByTaskIdOrderByCreatedAtDesc(Long taskId)` and verify it compiles

## 2. Request validation

- [ ] 2.1 Add `dto/CreateCommentRequest.java` with `@NotBlank String author`
      and `@NotBlank @Size(max = 500) String body` and verify it compiles

## 3. Tests first

- [ ] 3.1 Add `web/CommentControllerTest.java` using `@WebMvcTest
      (CommentController.class)` and `@MockitoBean CommentService`, with one
      `@Test` per scenario in `specs/task-comments/spec.md`: posting a valid
      comment returns 201 with the comment body; posting without an author
      returns 400; posting an empty body returns 400; posting a body over 500
      characters returns 400; posting to an unknown task returns 404; listing
      returns comments newest first; listing an unknown task returns 404. (A
      task with no comments and the cascade-delete requirement have no MockMvc
      test — the first only needs an empty-list assertion the other scenarios
      already exercise via mocking, the second is a database-level guarantee
      a MockMvc test cannot reach.) Verify the class fails to compile/run
      until `CommentController` and `CommentService` exist

## 4. Business logic and web layer

- [ ] 4.1 Add `service/TaskNotFoundException.java`, an unchecked exception
      carrying the missing task id, and verify it compiles
- [ ] 4.2 Add `service/CommentService.java` (constructor-injected
      `CommentRepository` and `TaskRepository`) with `create(long taskId,
      CreateCommentRequest request)` and `findByTask(long taskId)`, each
      throwing `TaskNotFoundException` when the task does not exist, and
      verify it compiles
- [ ] 4.3 Add `web/CommentController.java` at
      `@RequestMapping("/api/v1/tasks/{taskId}/comments")` with the `POST` and
      `GET` handlers and an `@ExceptionHandler(TaskNotFoundException.class)`
      returning 404, and verify `./mvnw -q test` passes every test in
      `CommentControllerTest`

## 5. Verification

- [ ] 5.1 Run `./mvnw -q test` for the whole backend suite and verify it is
      green
- [ ] 5.2 Update the endpoints table in `docs/architecture.md` with the two
      new routes and verify the table lists `POST` and `GET`
      `/api/v1/tasks/{taskId}/comments`
