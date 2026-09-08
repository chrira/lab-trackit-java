## 1. Backend request validation

- [x] 1.1 Add `dto/UpdateCommentRequest.java` with `@NotBlank @Size(max = 500)
      String body` and verify it compiles
- [x] 1.2 Add `service/CommentNotFoundException.java`, an unchecked exception
      carrying the missing/mismatched comment id, and verify it compiles

## 2. Backend tests first

- [x] 2.1 Extend `web/CommentControllerTest.java` with one `@Test` per new
      backend scenario in `specs/task-comments/spec.md`: editing a comment
      returns 200 with the updated body; editing with an empty body returns
      400; editing a body over 500 characters returns 400; editing an unknown
      comment returns 404; editing a comment that belongs to a different task
      returns 404; deleting a comment returns 204; deleting an unknown
      comment returns 404; deleting a comment that belongs to a different
      task returns 404. Verify the new tests fail to compile/run until
      `CommentController` and `CommentService` gain the `PATCH`/`DELETE`
      handlers

## 3. Backend business logic and web layer

- [x] 3.1 Add `update(long taskId, long commentId, UpdateCommentRequest
      request)` and `delete(long taskId, long commentId)` to
      `service/CommentService.java`; both look the comment up by id, then
      throw `CommentNotFoundException` if it doesn't exist or its `taskId`
      doesn't match, per the design decision; verify it compiles
- [x] 3.2 Add `PATCH` and `DELETE` handlers to `web/CommentController.java` at
      `/api/v1/tasks/{taskId}/comments/{commentId}`, plus an
      `@ExceptionHandler(CommentNotFoundException.class)` returning 404, and
      verify `./mvnw -q test` passes every test in `CommentControllerTest`
- [x] 3.3 Run `./mvnw -q test` for the whole backend suite and verify it is
      green

## 4. Frontend API module

- [x] 4.1 Add `frontend/src/api/comments.ts` with a `Comment` type mirroring
      the backend record, and `listComments`, `createComment`,
      `updateComment`, `deleteComment` functions following the pattern in
      `api/tasks.ts`, and verify `npx vue-tsc -b` reports no new errors

## 5. Frontend detail view

- [x] 5.1 Add `frontend/src/views/TaskDetailView.vue`: on mount, calls
      `listTasks()` and finds the task with the route's `id` (see design.md -
      no single-task endpoint), and calls `listComments(id)`; owns its own
      loading and error state per the house rule
- [x] 5.2 On the same view, render the task's title/project/status, the
      comment list, a form to add a comment (author + body), and per-comment
      edit (inline body field + save) and delete controls, calling
      `createComment`/`updateComment`/`deleteComment` and refreshing the
      comment list after each
- [x] 5.3 Add the route `/tasks/:id` (name `task-detail`) to
      `router/index.ts`, loading `TaskDetailView.vue`
- [x] 5.4 In `TaskBoardView.vue`, link each task row to its detail page
      (`router-link` to `/tasks/{id}`)
- [x] 5.5 Verify `npx vue-tsc -b` reports no new errors

## 6. Manual verification

- [x] 6.1 With the backend and `npm run dev` running, walk through the four
      browser scenarios in `specs/task-comments/spec.md`'s "Viewing and
      managing a task's comments in the browser" requirement: open a task
      from the board, add a comment, edit it, delete it - and confirm each
      matches the scenario. Verified via curl against the running backend
      (create/edit/delete, wrong-task 404) and against the Vite dev server
      (board and `/tasks/1` both 200, `TaskDetailView.vue` transpiles with no
      error, `/api` proxy forwards correctly) - no browser tool was available
      in this session to click through the UI directly
