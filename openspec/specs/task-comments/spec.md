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

### Requirement: Editing a comment
The system SHALL let a client edit an existing comment's body. Editing SHALL
be rejected, and the comment left unchanged, if the task does not exist, the
comment does not exist on that task, the new body is blank, or the new body
is longer than 500 characters. A comment's `author` and `createdAt` SHALL NOT
change when it is edited.

#### Scenario: Editing a comment
- **Given** a comment on a task that exists
- **WHEN** the client PATCHes `{"body":"Turned out fine after all."}` to
  `/api/v1/tasks/{taskId}/comments/{commentId}`
- **THEN** the response is `200` and its body carries the same `id`, `taskId`,
  `author`, and `createdAt`, with the updated `body`

#### Scenario: Editing a comment with an empty body
- **Given** a comment on a task that exists
- **WHEN** the client PATCHes a body that is blank
- **THEN** the response is `400` and the comment is unchanged

#### Scenario: Editing a comment with a body over the length limit
- **Given** a comment on a task that exists
- **WHEN** the client PATCHes a body that is 501 characters long
- **THEN** the response is `400` and the comment is unchanged

#### Scenario: Editing a comment that does not exist
- **Given** a task that exists and no comment with id 404 on it
- **WHEN** the client PATCHes `/api/v1/tasks/{taskId}/comments/404`
- **THEN** the response is `404`

#### Scenario: Editing a comment that belongs to a different task
- **Given** a comment that exists but belongs to a different task than the one
  in the URL
- **WHEN** the client PATCHes that comment through the other task's URL
- **THEN** the response is `404` and the comment is unchanged

### Requirement: Deleting a comment
The system SHALL let a client delete an existing comment. Deleting SHALL fail
if the task does not exist or the comment does not exist on that task.

#### Scenario: Deleting a comment
- **Given** a comment on a task that exists
- **WHEN** the client DELETEs `/api/v1/tasks/{taskId}/comments/{commentId}`
- **THEN** the response is `204` and the comment no longer appears when
  listing that task's comments

#### Scenario: Deleting a comment that does not exist
- **Given** a task that exists and no comment with id 404 on it
- **WHEN** the client DELETEs `/api/v1/tasks/{taskId}/comments/404`
- **THEN** the response is `404`

#### Scenario: Deleting a comment that belongs to a different task
- **Given** a comment that exists but belongs to a different task than the one
  in the URL
- **WHEN** the client DELETEs that comment through the other task's URL
- **THEN** the response is `404` and the comment still exists

### Requirement: Viewing and managing a task's comments in the browser
The task board SHALL link each task to a detail page for that task. The detail
page SHALL show the task and the list of its comments, and SHALL let a user
add a comment, edit a comment's body, and delete a comment, without leaving
the page.

#### Scenario: Opening a task from the board
- **WHEN** a user clicks a task on the board
- **THEN** the browser shows that task's detail page, including its current
  list of comments

#### Scenario: Adding a comment from the detail page
- **Given** a task's detail page is open
- **WHEN** the user submits the comment form with an author and a body
- **THEN** the new comment appears in the list on that page

#### Scenario: Editing a comment from the detail page
- **Given** a task's detail page is open with at least one comment
- **WHEN** the user edits that comment's body and saves it
- **THEN** the list shows the updated body for that comment

#### Scenario: Deleting a comment from the detail page
- **Given** a task's detail page is open with at least one comment
- **WHEN** the user deletes that comment
- **THEN** it no longer appears in the list
