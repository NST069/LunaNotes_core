package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.UserNotFoundException;
import com.lunanotes.mapper.UserDTO;
import com.lunanotes.model.User;
import com.lunanotes.service.UsersDataService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UsersDataService usersDataService;

    @Autowired
    ObjectMapper objectMapper;

    List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();

        User user1 = User.builder()
                .id(1L)
                .userName("John Doe")
                .build();
        User user2 = User.builder()
                .id(2L)
                .userName("Marc Zucc")
                .build();
        User user3 = User.builder()
                .id(3L)
                .userName("Paul Fool")
                .build();

        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ExistingUser_ShouldReturnNote() throws Exception{
        given(this.usersDataService.findById("1")).willReturn(this.users.get(0));

        this.mockMvc.perform(get(baseUrl+"/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.userName").value("John Doe"));
    }

    @Test
    void findById_NonExistingUser_ShouldThrowException() throws Exception{
        given(this.usersDataService.findById("1")).willThrow(new UserNotFoundException("1"));

        this.mockMvc.perform(get(baseUrl+"/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        given(this.usersDataService.findAll()).willReturn(this.users);

        this.mockMvc.perform(get(baseUrl+"/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())));
    }

    @Test
    void addUser_ShouldSave() throws Exception {
        UserDTO userDTO = new UserDTO(0, "test", 0, 0);
        String json = this.objectMapper.writeValueAsString(userDTO);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("test");

        given(this.usersDataService.save(Mockito.any(User.class))).willReturn(savedUser);

        this.mockMvc.perform(post(baseUrl+"/users").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.userName").value("test"));
    }

    @Test
    void updateUser_ExistingUser_ShouldUpdate() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUserName("test");

        UserDTO userDTO = new UserDTO(1, "test", 0, 0);
        String json = this.objectMapper.writeValueAsString(userDTO);

        given(this.usersDataService.update(eq("1"), Mockito.any(User.class))).willReturn(updatedUser);

        this.mockMvc.perform(put(baseUrl+"/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.userName").value("test"));
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowException() throws Exception {

        UserDTO userDTO = new UserDTO(1, "test", 0, 0);
        String json = this.objectMapper.writeValueAsString(userDTO);

        given(this.usersDataService.update(eq("1"), Mockito.any(User.class))).willThrow(new UserNotFoundException("1"));

        this.mockMvc.perform(put(baseUrl+"/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUser_ExistingUser_ShouldDelete() throws Exception {
        doNothing().when(this.usersDataService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() throws Exception {
        doThrow(new UserNotFoundException("1")).when(this.usersDataService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}