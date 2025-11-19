package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.NoteNotFoundException;
import com.lunanotes.mapper.NoteDTO;
import com.lunanotes.model.Note;
import com.lunanotes.model.User;
import com.lunanotes.service.NotesDataService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@AutoConfigureMockMvc
class NotesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NotesDataService notesDataService;

    @Autowired
    ObjectMapper objectMapper;

    List<Note> notes;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .userName("JohnDoe")
                .build();
        this.notes = new ArrayList<>();
        notes.add(new Note(1L, "test1", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), user));
        notes.add(new Note(2L, "test2", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), user));
        notes.add(new Note(3L, "test3", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), user));
        notes.add(new Note(4L, "test4", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), user));
        notes.add(new Note(5L, "test5", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), user));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() throws Exception{
        given(this.notesDataService.findById("1")).willReturn(this.notes.get(0));

        this.mockMvc.perform(get("/api/v1/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("test1"));
    }

    @Test
    void findById_NonExistingNote_ShouldReturnNull() throws Exception{
        given(this.notesDataService.findById("1")).willThrow(new NoteNotFoundException("1"));

        this.mockMvc.perform(get("/api/v1/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAllNotes_ShouldReturnList() throws Exception {
        given(this.notesDataService.findAll()).willReturn(this.notes);

        this.mockMvc.perform(get("/api/v1/notes").accept(MediaType.APPLICATION_JSON))
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
    void addNote_ShouldSave() throws Exception {
        NoteDTO noteDTO = new NoteDTO(0, "test", "lorem ipsum", null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        Note savedNote = new Note();
        savedNote.setId(1L);
        savedNote.setTitle("test");
        savedNote.setContent("lorem ipsum");

        given(this.notesDataService.save(Mockito.any(Note.class))).willReturn(savedNote);

        this.mockMvc.perform(post("/api/v1/notes").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value("test"))
                .andExpect(jsonPath("$.data.content").value("lorem ipsum"));
    }

    @Test
    void updateNote_ExistingNote_ShouldUpdate() throws Exception {
        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("test");
        updatedNote.setContent("lorem ipsum2");

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.notesDataService.update(eq("1"), Mockito.any(Note.class))).willReturn(updatedNote);

        this.mockMvc.perform(put("/api/v1/notes/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value(updatedNote.getTitle()))
                .andExpect(jsonPath("$.data.content").value(updatedNote.getContent()));
    }

    @Test
    void updateNote_NonExistingNote_ShouldThrowException() throws Exception {

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.notesDataService.update(eq("1"), Mockito.any(Note.class))).willThrow(new NoteNotFoundException("1"));

        this.mockMvc.perform(put("/api/v1/notes/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete() throws Exception {
        doNothing().when(this.notesDataService).delete("1");

        this.mockMvc.perform(delete("/api/v1/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException() throws Exception {
        doThrow(new NoteNotFoundException("1")).when(this.notesDataService).delete("1");

        this.mockMvc.perform(delete("/api/v1/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}