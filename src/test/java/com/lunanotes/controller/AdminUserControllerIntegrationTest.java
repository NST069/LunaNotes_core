package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.model.User;
import com.lunanotes.util.UserRole;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Admin User API endpoints")
@Tag("integration")
@ActiveProfiles("test")
class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(post(this.baseUrl+"/auth/login")
                        .with(httpBasic("User1", "password1")));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(content);
        this.token = "Bearer "+json.getJSONObject("data").getString("token");
        System.out.println(this.token);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findUserById_ExistingUser_ShouldReturnUser() throws Exception{
        this.mockMvc.perform(get(this.baseUrl + "/admin/users/2").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    void findUserById_NonExistingUser_ShouldThrowException() throws Exception{
        this.mockMvc.perform(get(baseUrl+"/admin/users/9999").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findAll_ShouldReturnList() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/admin/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addUser_ShouldSave() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setRoles(UserRole.USER.name());

        String json = this.objectMapper.writeValueAsString(user);

        this.mockMvc.perform(post(this.baseUrl + "/admin/users").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("test"));
        this.mockMvc.perform(get(this.baseUrl + "/admin/users").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUser_ExistingUser_ShouldUpdate() throws Exception {
        User user = new User();
        user.setUsername("test2");
        user.setPassword("password");
        user.setRoles(UserRole.USER.name());

        String json = this.objectMapper.writeValueAsString(user);

        this.mockMvc.perform(put(baseUrl+"/admin/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUser_NonExistingUser_ShouldThrowException() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        user.setRoles(UserRole.USER.name());

        String json = this.objectMapper.writeValueAsString(user);

        this.mockMvc.perform(put(baseUrl+"/admin/users/9999").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteUser_ExistingUser_ShouldDelete() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"));
        this.mockMvc.perform(get(this.baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteUser_NonExistingUser_ShouldThrowException() throws Exception {
        this.mockMvc.perform(delete(baseUrl+"/admin/users/9999").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}