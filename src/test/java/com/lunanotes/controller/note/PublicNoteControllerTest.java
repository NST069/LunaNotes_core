package com.lunanotes.controller.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.model.User;
import com.lunanotes.service.NoteService;
import com.lunanotes.util.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PublicNoteControllerTest {

    @Value("${api.endpoint.base-url}/notes")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NoteService noteService;

    @Autowired
    ObjectMapper objectMapper;

    List<Note> notes;

    List<User> users;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        this.users = new ArrayList<>();
        this.users.add(user);
        this.notes = new ArrayList<>();
        notes.add(new Note(1L, "test1", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), true, user, new HashSet<>()));
        notes.add(new Note(2L, "test2", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(3L, "test3", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), true, user, new HashSet<>()));
        notes.add(new Note(4L, "test4", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), true, user, new HashSet<>()));
        notes.add(new Note(5L, "test5", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
    }

    @Test
    void findPublicNoteById_ExistingNote() throws Exception {
        given(this.noteService.findPublicById("1")).willReturn(this.notes.get(0));

        this.mockMvc.perform(get(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("test1"));

        this.mockMvc.perform(get(baseUrl + "/note-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("test1"));
    }

    @Test
    void findPublicNoteById_NotExistingNote() throws Exception {
        given(this.noteService.findPublicById("1")).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(get(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(baseUrl + "/note-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAllPublicNotesOfUser() throws Exception {
        List<Note> publicNotes = this.notes.stream().filter(note-> note.isPublic() && note.getOwner().getId() == 1).toList();
        given(this.noteService.findAllPublic("1")).willReturn(publicNotes);

        this.mockMvc.perform(get(baseUrl + "/user-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All For User Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(publicNotes.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].title").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("3"))
                .andExpect(jsonPath("$.data[1].title").value("test3"));
    }
}
