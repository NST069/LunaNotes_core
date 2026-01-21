package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.UpdateUserDTO;
import com.lunanotes.mapper.UserDTO;
import com.lunanotes.model.User;
import com.lunanotes.service.UserService;
import com.lunanotes.util.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();

        User user1 = User.builder()
                .id(1L)
                .username("John Doe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("Marc Zucc")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        User user3 = User.builder()
                .id(3L)
                .username("Paul Fool")
                .password("password")
                .roles(UserRole.USER.name())
                .build();

        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() throws Exception {
        given(this.userService.findById("1")).willReturn(this.users.get(0));

        this.mockMvc.perform(get(baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.username").value("John Doe"));
    }

    @Test
    void findById_NonExistingUser_ShouldThrowException() throws Exception {
        given(this.userService.findById("1")).willThrow(new ObjectNotFoundException("user", "1"));

        this.mockMvc.perform(get(baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAll_ShouldReturnList() throws Exception {
        given(this.userService.findAll()).willReturn(this.users);

        this.mockMvc.perform(get(baseUrl + "/admin/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())));
    }

    @Test
    void addUser_ShouldSave() throws Exception {
        UserDTO userDTO = new UserDTO(0, "test", "test@mail.ru", "USER", true, "", "", LocalDateTime.now(), LocalDateTime.now());
        String json = this.objectMapper.writeValueAsString(userDTO);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("test");

        given(this.userService.save(Mockito.any(User.class))).willReturn(savedUser);

        this.mockMvc.perform(post(baseUrl + "/admin/users").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    void updateUser_ExistingUser_ShouldUpdate() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("test");

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(0, "test", "test@mail.ru", "USER", true, "", "");
        String json = this.objectMapper.writeValueAsString(updateUserDTO);

        given(this.userService.update(eq("1"), Mockito.any(User.class))).willReturn(updatedUser);

        this.mockMvc.perform(put(baseUrl + "/admin/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowException() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO(0, "test", "test@mail.ru", "USER", true, "", "");
        String json = this.objectMapper.writeValueAsString(updateUserDTO);

        given(this.userService.update(eq("1"), Mockito.any(User.class))).willThrow(new ObjectNotFoundException("user", "1"));

        this.mockMvc.perform(put(baseUrl + "/admin/users/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUser_ExistingUser_ShouldDelete() throws Exception {
        doNothing().when(this.userService).delete("1");

        this.mockMvc.perform(delete(baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() throws Exception {
        doThrow(new ObjectNotFoundException("user", "1")).when(this.userService).delete("1");

        this.mockMvc.perform(delete(baseUrl + "/admin/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}