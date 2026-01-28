package com.lunanotes.controller.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.note.NoteDTO;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminNoteControllerTest {

    @Value("${api.endpoint.base-url}/admin/notes")
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
        notes.add(new Note(1L, "test1", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(2L, "test2", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, null, new HashSet<>()));
        notes.add(new Note(3L, "test3", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(4L, "test4", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(5L, "test5", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, null, new HashSet<>()));
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() throws Exception {
        given(this.noteService.findById("1")).willReturn(this.notes.get(0));

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
    void findById_NonExistingNote_ShouldThrowException() throws Exception {
        given(this.noteService.findById("1")).willThrow(new ObjectNotFoundException("note", "1"));

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
    void findAllNotes_ShouldReturnList() throws Exception {
        given(this.noteService.findAllAdmin()).willReturn(this.notes);

        this.mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.notes.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].title").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].title").value("test2"));
    }

    @Test
    void findAllNotesByUserId_ShouldReturnList() throws Exception {
        List<Note> filteredNotes = this.notes.stream()
                .filter(note -> ((note.getOwner() != null) ? note.getOwner().getId() : 0) == 1).toList();

        given(this.noteService.findByOwnerId("1")).willReturn(filteredNotes);

        this.mockMvc.perform(get(baseUrl + "/user-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All For User Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(filteredNotes.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].title").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("3"))
                .andExpect(jsonPath("$.data[1].title").value("test3"));
    }

    @Test
    void updateNote_ExistingNote_ShouldUpdate() throws Exception {
        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("test");
        updatedNote.setContent("lorem ipsum2");

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.updateAdmin(eq("1"), any(Note.class))).willReturn(updatedNote);

        this.mockMvc.perform(put(baseUrl + "/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value(updatedNote.getTitle()))
                .andExpect(jsonPath("$.data.content").value(updatedNote.getContent()));
    }

    @Test
    void updateNote_NonExistingNote_ShouldThrowException() throws Exception {
        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.updateAdmin(eq("1"), any(Note.class))).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(put(baseUrl + "/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete() throws Exception {
        doNothing().when(this.noteService).deleteAdmin("1");

        this.mockMvc.perform(delete(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException() throws Exception {
        doThrow(new ObjectNotFoundException("note", "1")).when(this.noteService).deleteAdmin("1");

        this.mockMvc.perform(delete(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void removeTag_ExistingTag_ShouldDeleteTag() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#FF0000");

        notes.get(0).getTags().add(tag);
        int tagsCount = notes.get(0).getTags().size();

        notes.get(0).removeTag(tag);

        given(noteService.removeTagAdmin("1", "1")).willReturn(notes.get(0));

        this.mockMvc.perform(delete(baseUrl + "/1/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Remove Tag Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount - 1));
    }

    @Test
    void removeTag_NonExistingTag_ShouldThrowException() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#FF0000");

        notes.get(0).removeTag(tag);

        given(noteService.removeTagAdmin("1", "1")).willThrow(new ObjectNotFoundException("tag", "1"));

        mockMvc.perform(delete(baseUrl + "/1/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
