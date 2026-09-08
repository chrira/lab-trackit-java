package ch.acend.trackit.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.acend.trackit.domain.Task;
import ch.acend.trackit.domain.TaskStatus;
import ch.acend.trackit.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String ONE_TASK = """
            {"title":"Write the context file","project":"trackit"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void postTaskReturnsCreated() throws Exception {
        BDDMockito.given(taskService.create(BDDMockito.any()))
                .willReturn(new Task(1L, "Write the context file", "trackit", TaskStatus.OPEN));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ONE_TASK))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Write the context file"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void postTaskWithoutTitleReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"project\":\"trackit\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTasksReturnsThePostedTask() throws Exception {
        BDDMockito.given(taskService.findAll())
                .willReturn(List.of(new Task(1L, "Write the context file", "trackit", TaskStatus.OPEN)));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].project").value("trackit"));
    }
}
