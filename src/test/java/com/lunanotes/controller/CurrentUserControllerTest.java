package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.mapper.UpdateUserDTO;
import com.lunanotes.model.User;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.service.UserService;
import com.lunanotes.util.UserRole;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CurrentUserControllerTest {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CurrentUserService currentUserService;

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
    void getCurrentUser_ShouldReturnUser() throws Exception{
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        this.mockMvc.perform(get(baseUrl+"/users/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Current user"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.username").value("John Doe"));
    }

    @Test
    void updateCurrentUser_ShouldUpdate() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("test");

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(1, "test", "test@mail.ru", "USER", true, "", "");
        String json = this.objectMapper.writeValueAsString(updateUserDTO);

        given(this.userService.update(eq("1"), Mockito.any(User.class))).willReturn(updatedUser);

        this.mockMvc.perform(put(baseUrl+"/users/me").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("test"));
    }

    @Test
    void deleteCurrentUser_ShouldInactivate() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));
        doNothing().when(this.userService).inactivate("1");

        this.mockMvc.perform(delete(baseUrl+"/users/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Account inactivated"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}