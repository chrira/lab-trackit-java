## Why

A task records what has to happen, not what the team found out along the way. Right
now there is nowhere in TrackIt to put that: a question, a blocker, a status update.
Comments give a task a running log without changing what a task itself means.

## What Changes

- New `Comment` entity: `id`, `taskId`, `author`, `body`, `createdAt`
- `POST /api/v1/tasks/{taskId}/comments` — add a comment to a task
- `GET /api/v1/tasks/{taskId}/comments` — list a task's comments, newest first
- New migration `V2__create_comment.sql`, with the `comment.task_id` foreign key
  set `ON DELETE CASCADE` so a task's comments cannot outlive the task
- `author` is a required free-text field on the request. TrackIt has no user table
  or authentication, so a comment is attributed to whatever name the client sends,
  not a verified identity
- A comment body is capped at 500 characters

## Capabilities

### New Capabilities
- `task-comments`: adding and listing comments on a task

### Modified Capabilities
(none — `Task` itself is unchanged)

## Impact

- **Backend**: new `domain/Comment.java`, `domain/CommentEntity.java`,
  `repository/CommentRepository.java`, `dto/CreateCommentRequest.java`,
  `service/CommentService.java`, `service/TaskNotFoundException.java`,
  `web/CommentController.java`, plus `V2__create_comment.sql`
- **Frontend**: none. No comment view is added — the task board keeps showing
  tasks only; see Out of Scope in the spec
- **Dependencies**: none added
