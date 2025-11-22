package com.lunanotes.service;

import com.lunanotes.exception.UserNotFoundException;
import com.lunanotes.model.User;
import com.lunanotes.repository.UsersJPARepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersDataServiceTest {

    @Mock
    UsersJPARepository usersJPARepository;

    @InjectMocks
    UsersDataService usersDataService;

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
    void findById_ExistingUser_ShouldReturnUser() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.of(user));

        User result = usersDataService.findById("1");

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    void findById_NonExistingUser_ShouldThrowException() {
        given(usersJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            usersDataService.findById("1");
        });

        verify(usersJPARepository, times(1)).findById("1");
    }

    @Test
    void saveUser_ShouldCreate() {
        User newUser = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .build();

        given(usersJPARepository.save(newUser)).willReturn(newUser);

        User result = usersDataService.save(newUser);

        assertThat(result.getId()).isEqualTo(newUser.getId());
        assertThat(result.getUserName()).isEqualTo(newUser.getUserName());

        verify(usersJPARepository, times(1)).save(newUser);
    }

    @Test
    void updateUser_ExistingUser_ShouldUpdateUser() {
        User oldUser = User.builder()
                .id(1L)
                .userName("John Doe")
                .build();

        User update = User.builder()
                .id(1L)
                .userName("Bill Straights")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.of(oldUser));
        given(usersJPARepository.save(oldUser)).willReturn(oldUser);

        User result = usersDataService.update("1", update);

        assertThat(result.getId()).isEqualTo(update.getId());
        assertThat(result.getUserName()).isEqualTo(update.getUserName());

        verify(usersJPARepository, times(1)).findById("1");
        verify(usersJPARepository, times(1)).save(oldUser);
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowException() {
        User update = User.builder()
                .id(1L)
                .userName("Nylon Tusk")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            usersDataService.update("1", update);
        });

        verify(usersJPARepository, times(1)).findById("1");
    }

    @Test
    void deleteUser_ExistingUser_ShouldDeleteUser() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.of(user));
        doNothing().when(usersJPARepository).deleteById("1");

        usersDataService.delete("1");

        verify(usersJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        given(usersJPARepository.findById("1")).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            usersDataService.delete("1");
        });

        verify(usersJPARepository, times(1)).findById("1");
    }
}