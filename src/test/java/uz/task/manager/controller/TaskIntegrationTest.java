package uz.task.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uz.task.manager.entity.enums.TaskPriority;
import uz.task.manager.payload.ApiResponse;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;
import uz.task.manager.payload.TaskRequest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        //signUp
        SignUpRequest signUpRequest = new SignUpRequest("username", "password", "FullName");
        mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signUpRequest))));

        //signIn
        SignInRequest signInRequest = new SignInRequest("username", "password");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signInRequest))));
        String responseBody = signInResultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);

        //store access token
        accessToken = ((Map<String, Object>) apiResponse.getData()).get("access_token").toString();
    }

    @Test
    void itShouldCreateTask() throws Exception {
        //create task
        TaskRequest request = new TaskRequest("title", "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions createTaskResultAction = mockMvc.perform(post("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        createTaskResultAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task is created successfully"));

        //list of tasks
        ResultActions getResultActions = mockMvc.perform(get("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = getResultActions.andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse apiResponse = objectMapper.readValue(responseBody, ApiResponse.class);

        ArrayList tasks = (ArrayList) ((LinkedHashMap) apiResponse.getData()).get("content");
        LinkedHashMap task = (LinkedHashMap) tasks.get(tasks.size() - 1);

        //check fields
        assertEquals(task.get("title").toString(), "title");
        assertEquals(task.get("content").toString(), "content");
        assertEquals(task.get("priority").toString(), "HIGH");
        assertEquals(task.get("dueDate").toString(), "2024-04-20");
        assertEquals(task.get("category").toString(), "category");
        assertEquals(task.get("status").toString(), "OPEN");

    }

    @Test
    void itShouldReturnBadRequestStatusCode() throws Exception {
        TaskRequest request = new TaskRequest(null, "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        signInResultActions.andExpect(status().isBadRequest());
    }

    @Test
    void itShouldReturnForbiddenStatusCode() throws Exception {
        TaskRequest request = new TaskRequest("title", "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        signInResultActions.andExpect(status().isForbidden());
    }

    @Test
    void itShouldUpdateTask() throws Exception {
        //create task
        TaskRequest request = new TaskRequest("title", "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions createTaskResultActions = mockMvc.perform(post("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        createTaskResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task is created successfully"));

        //update task
        TaskRequest updateReq = new TaskRequest("title2", "content2", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions updateTaskResultActions = mockMvc.perform(put("/api/v1/task/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(updateReq))));

        updateTaskResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task is updated successfully"));

        //list of tasks
        ResultActions getResultActions = mockMvc.perform(get("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        getResultActions
                .andExpect(jsonPath("$.data.content[0].title").value("title2"))
                .andExpect(jsonPath("$.data.content[0].content").value("content2"))
                .andExpect(jsonPath("$.data.content[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.data.content[0].dueDate").value("2024-04-20"))
                .andExpect(jsonPath("$.data.content[0].category").value("category"))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"));

    }

    @Test
    void itShouldReturnForbiddenStatusCodeWhenUpdateTask() throws Exception {
        TaskRequest request = new TaskRequest("title", "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions resultActions = mockMvc.perform(put("/api/v1/task/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void itShouldReturnBadRequestStatusCodeWhenUpdateTask() throws Exception {
        TaskRequest request = new TaskRequest(null, "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions resultActions = mockMvc.perform(put("/api/v1/task/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void itShouldReturnBadRequestStatusCodeWhenDeleteTask() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/task/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Task is not found"));
    }

    @Test
    void itShouldDeleteTask() throws Exception {
        //create task
        TaskRequest request = new TaskRequest("title", "content", TaskPriority.HIGH, LocalDate.of(2024, Month.APRIL, 20), "category");
        ResultActions createTaskResultAction = mockMvc.perform(post("/api/v1/task")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(request))));

        //delete task
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/task/1")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task is deleted successfully"));
    }


    private String objectToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
