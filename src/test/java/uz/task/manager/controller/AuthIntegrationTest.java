package uz.task.manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uz.task.manager.payload.SignInRequest;
import uz.task.manager.payload.SignUpRequest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldSignInSuccessfully() throws Exception {
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

        signInResultActions.andExpect(status().isOk());
    }

    @Test
    void itShouldNotMatchPassword() throws Exception {
        //signUp
        SignUpRequest signUpRequest = new SignUpRequest("username", "password", "FullName");
        mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signUpRequest))));

        //signIn
        SignInRequest signInRequest = new SignInRequest("username", "password2");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signInRequest))));

        signInResultActions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Password is incorrect"));
    }

    @Test
    void itShouldNotFindUser() throws Exception {
        SignInRequest signInRequest = new SignInRequest("username1", "password");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signInRequest))));

        signInResultActions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("User not found"));
    }

    @Test
    void itShouldSignInUnSuccessfully() throws Exception {
        //signIn
        SignInRequest signInRequest = new SignInRequest("user", "password");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signInRequest))));

        signInResultActions.andExpect(status().isBadRequest());
    }

    @Test
    void itShouldSignUpSuccessfully() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("newusername", "password", "FullName");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signUpRequest))));

        signInResultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void itShouldNotSignUpWithTwoSameUsername() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("newusername2", "password", "FullName");
        ResultActions signInResultActions = mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signUpRequest))));

        signInResultActions.andExpect(status().isOk());

        SignUpRequest signUpRequest2 = new SignUpRequest("newusername2", "password", "FullName");
        ResultActions signInResultActions2 = mockMvc.perform(post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(signUpRequest2))));

        signInResultActions2
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("User is already exist"));
    }


    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
