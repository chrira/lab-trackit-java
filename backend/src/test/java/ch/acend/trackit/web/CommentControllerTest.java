package ch.acend.trackit.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.acend.trackit.domain.Comment;
import ch.acend.trackit.dto.CreateCommentRequest;
import ch.acend.trackit.service.CommentService;
import ch.acend.trackit.service.TaskNotFoundException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The service checks that the task exists, so it is mocked here rather than imported.
 * A green run of this class says the web layer is correct, not that the cascade delete
 * in the migration works - that is a database-level guarantee no MockMvc test can reach.
 */
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private static final String ONE_COMMENT = """
            {"author":"jo","body":"Blocked on the design review."}
            """;

    private static final Comment STORED_COMMENT =
            new Comment(1L, 1L, "jo", "Blocked on the design review.", Instant.parse("2026-09-08T09:14:22.481Z"));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void postCommentReturnsCreated() throws Exception {
        when(commentService.create(eq(1L), any(CreateCommentRequest.class))).thenReturn(STORED_COMMENT);

        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ONE_COMMENT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.author").value("jo"))
                .andExpect(jsonPath("$.body").value("Blocked on the design review."))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void postCommentWithoutAuthorReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"\",\"body\":\"Blocked on the design review.\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCommentWithEmptyBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"jo\",\"body\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCommentLongerThanTheLimitReturnsBadRequest() throws Exception {
        String tooLong = "a".repeat(501);

        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"jo\",\"body\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postCommentOnUnknownTaskReturnsNotFound() throws Exception {
        when(commentService.create(eq(404L), any(CreateCommentRequest.class)))
                .thenThrow(new TaskNotFoundException(404L));

        mockMvc.perform(post("/api/v1/tasks/404/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ONE_COMMENT))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsReturnsThemNewestFirst() throws Exception {
        Comment older = new Comment(1L, 1L, "jo", "First.", Instant.parse("2026-09-08T09:00:00Z"));
        Comment newer = new Comment(2L, 1L, "sam", "Second.", Instant.parse("2026-09-08T09:10:00Z"));
        when(commentService.findByTask(1L)).thenReturn(List.of(newer, older));

        mockMvc.perform(get("/api/v1/tasks/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(1));
    }

    @Test
    void getCommentsForUnknownTaskReturnsNotFound() throws Exception {
        when(commentService.findByTask(404L)).thenThrow(new TaskNotFoundException(404L));

        mockMvc.perform(get("/api/v1/tasks/404/comments"))
                .andExpect(status().isNotFound());
    }
}
