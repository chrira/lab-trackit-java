## Why

Comments exist in the API but nowhere in the browser: the board has no link to a
single task, so nobody can see a task's comments, add one, fix a typo in one, or
remove one they no longer want. A comment posted today is stuck as written forever.

## What Changes

- Each task on the board links to a task detail page
- New task detail page: shows the task, lists its comments, and has a form to
  add one
- A comment's body can be edited after it is posted
- A comment can be deleted
- `PATCH /api/v1/tasks/{taskId}/comments/{commentId}` — edit a comment's body
- `DELETE /api/v1/tasks/{taskId}/comments/{commentId}` — remove a comment
- **BREAKING**: none — these are new endpoints, existing ones are unchanged

## Capabilities

### New Capabilities
(none)

### Modified Capabilities
- `task-comments`: adds editing and deleting a comment, and adds the frontend
  behavior for viewing, adding, editing, and deleting comments on a task's
  detail page

## Impact

- **Backend**: `web/CommentController.java` gains `PATCH`/`DELETE` handlers;
  `service/CommentService.java` gains `update`/`delete`; new
  `dto/UpdateCommentRequest.java`; new `service/CommentNotFoundException.java`
  for editing/deleting a comment that does not belong to the task
- **Frontend**: new `views/TaskDetailView.vue`; new `api/comments.ts`; a route
  for `/tasks/:id` in `router/index.ts`; `TaskBoardView.vue` links each row to
  its detail page
- **Out of scope**: editing a task itself; a comment's author cannot be
  changed, only its body; no edit history or audit trail; no pagination of
  comments; no confirmation dialog before delete (this is a small internal
  tool, not a public product)
