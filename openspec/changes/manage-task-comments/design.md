## Context

See proposal.md - Why. `CommentController`/`CommentService` today only support
create and list (`add-task-comments`, archived). The frontend has one view,
`TaskBoardView.vue`, with no per-task route and no comment UI at all
(`docs/architecture.md`, `LAB.md` both note the comment endpoints were
deliberately left unsurfaced in the browser). This change adds the missing
write paths on the backend and the missing screen on the frontend, touching
both layers for one user-facing outcome.

## Goals / Non-Goals

**Goals:**
- A comment can be corrected or removed after posting, from the API and the browser
- A task has a browsable detail page, reachable from the board

**Non-Goals:**
- Editing a task itself (title/project/status) from the detail page
- An edit history, audit trail, or "edited" marker on a comment
- Changing a comment's author after creation
- Pagination or infinite scroll for comments
- A confirmation dialog before delete

## Decisions

- **No new `GET /api/v1/tasks/{id}` endpoint.** There is no single-task
  fetch today, only `GET /api/v1/tasks` (all tasks). Adding one would touch
  the `task` capability, which this change does not otherwise modify.
  `TaskDetailView.vue` instead calls the existing `listTasks()` and finds the
  task with the matching id client-side, same as the board already does with
  the full list. Alternative considered and rejected: a single-task endpoint
  is the more scalable answer, but it's out of scope for a change about
  comments and would need its own spec delta on `task`.
- **PATCH edits only `body`, never `author`.** The author identifies who
  wrote the comment; letting it change would let someone rewrite who said
  something. `UpdateCommentRequest` has one field: `body`.
- **A comment posted through the wrong task's URL is a 404, not a 400.**
  `CommentService.update`/`delete` look the comment up by id, then check
  `comment.taskId() == taskId`; a mismatch throws the same
  `CommentNotFoundException` as "no such comment," rather than a separate
  error shape. A client with the wrong task id in the URL gets no signal
  about whether the comment exists elsewhere - alternative considered and
  rejected: leaking that distinction isn't worth a second exception type
  for an internal tool.
- **Delete returns `204` with no body**, matching `DELETE`'s usual REST
  convention and requiring no response DTO.
- **One detail view, not a modal from the board.** `TaskDetailView.vue` gets
  its own route (`/tasks/:id`) rather than an in-place expand-in-list panel,
  so a comment thread is linkable and matches the "one component per route"
  house rule.
- **Comment editing/deleting is inline in the list**, not a separate page -
  a small edit-in-place form per comment row, following the same
  "view owns its loading and error state" rule `TaskBoardView.vue` already
  follows.
- **No frontend automated tests for the new view.** `vitest` is present in
  `package.json` but nothing in the repo uses it yet, and no existing view
  has a test to follow the pattern of. Verification is manual: run the dev
  server, exercise the four browser scenarios in the spec by hand.

## Risks / Trade-offs

- **[Risk]** A comment's identity in the URL (`/comments/{commentId}`) is
  global, not scoped to the task path segment, until the service checks
  `taskId` matches → **Mitigation**: the 404-on-mismatch decision above closes
  this in the service layer; a MockMvc test exercises it directly.
- **[Risk]** No confirmation before delete means a misclick loses a comment
  permanently (comments aren't soft-deleted) → **Mitigation**: accepted for
  this internal tool; noted explicitly in Non-Goals so it isn't revisited as
  an oversight later.
