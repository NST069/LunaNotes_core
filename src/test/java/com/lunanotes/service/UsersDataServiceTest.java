package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UsersDataServiceTest {

    @Mock
    UsersJPARepository usersJPARepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UsersDataService usersDataService;

    List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();

        User user1 = User.builder()
                .id(1L)
                .userName("John Doe")
                .password("password")
                .roles("USER")
                .build();
        User user2 = User.builder()
                .id(2L)
                .userName("Marc Zucc")
                .password("password")
                .roles("USER")
                .build();
        User user3 = User.builder()
                .id(3L)
                .userName("Paul Fool")
                .password("password")
                .roles("USER")
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
                .password("password")
                .roles("USER")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.of(user));

        User result = usersDataService.findById("1");

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    void findById_NonExistingUser_ShouldThrowException() {
        given(usersJPARepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            usersDataService.findById("1");
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");

        verify(usersJPARepository, times(1)).findById("1");
    }

    @Test
    void findAll_ShouldReturnList(){
        given(usersJPARepository.findAll()).willReturn(users);

        List<User> result = usersDataService.findAll();

        assertThat(result.size()).isEqualTo(users.size());

        verify(usersJPARepository, times(1)).findAll();
    }

    @Test
    void saveUser_ShouldCreate() {
        User newUser = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .password("password")
                .roles("USER")
                .build();

        given(this.passwordEncoder.encode(newUser.getPassword())).willReturn("password");
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
                .password("password")
                .roles("USER")
                .build();

        User update = User.builder()
                .id(1L)
                .userName("Bill Straights")
                .password("password")
                .roles("USER")
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
                .password("password")
                .roles("USER")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            usersDataService.update("1", update);
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");

        verify(usersJPARepository, times(1)).findById("1");
    }

    @Test
    void deleteUser_ExistingUser_ShouldDeleteUser() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .password("password")
                .roles("USER")
                .build();

        given(usersJPARepository.findById("1")).willReturn(Optional.of(user));
        doNothing().when(usersJPARepository).deleteById("1");

        usersDataService.delete("1");

        verify(usersJPARepository, times(1)).deleteById("1");
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        given(usersJPARepository.findById("1")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            usersDataService.delete("1");
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");

        verify(usersJPARepository, times(1)).findById("1");
    }
}