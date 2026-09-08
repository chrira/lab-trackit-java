# task-comments Specification

## Purpose
Lets a team record what it found out about a task — questions, blockers, status
updates — without changing what the task itself represents.

## Requirements

### Requirement: Adding a comment to a task
The system SHALL let a client add a comment to an existing task by POSTing an
author and a body. The comment SHALL be rejected, and nothing stored, if the
task does not exist, the author is blank, the body is blank, or the body is
longer than 500 characters.

#### Scenario: Commenting on a task
- **WHEN** the client POSTs `{"author":"jo","body":"Blocked on the design review."}`
  to `/api/v1/tasks/{taskId}/comments` for a task that exists
- **THEN** the response is `201` and its body carries `id`, `taskId`, `author`,
  `body`, and `createdAt`

#### Scenario: Commenting without an author
- **Given** a task that exists
- **WHEN** the client POSTs a comment whose `author` is blank
- **THEN** the response is `400` and no comment is stored

#### Scenario: Commenting with an empty body
- **Given** a task that exists
- **WHEN** the client POSTs a comment whose `body` is blank
- **THEN** the response is `400` and no comment is stored

#### Scenario: A comment body over the length limit
- **Given** a task that exists
- **WHEN** the client POSTs a comment whose `body` is 501 characters long
- **THEN** the response is `400` and no comment is stored

#### Scenario: Commenting on a task that does not exist
- **Given** no task with id 404
- **WHEN** the client POSTs a valid comment to `/api/v1/tasks/404/comments`
- **THEN** the response is `404` and no comment is stored

### Requirement: Listing the comments on a task
The system SHALL let a client list the comments on an existing task, newest
first. Listing SHALL fail if the task does not exist.

#### Scenario: Listing comments returns them newest first
- **Given** a task with two comments, the first posted before the second
- **WHEN** the client GETs `/api/v1/tasks/{taskId}/comments`
- **THEN** the response is `200` and the second comment appears before the first

#### Scenario: A task with no comments
- **Given** a task that exists and has no comments
- **WHEN** the client GETs `/api/v1/tasks/{taskId}/comments`
- **THEN** the response is `200` with an empty list

#### Scenario: Listing comments for a task that does not exist
- **Given** no task with id 404
- **WHEN** the client GETs `/api/v1/tasks/404/comments`
- **THEN** the response is `404`

### Requirement: Comments do not outlive their task
The system SHALL remove a task's comments whenever that task is deleted, so no
comment can reference a task that no longer exists.

#### Scenario: Deleting a task removes its comments
- **Given** a task with at least one comment
- **WHEN** the task row is deleted
- **THEN** its comments are deleted along with it
