package com.lunanotes.controller.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.note.AddMultipleTagsRequest;
import com.lunanotes.mapper.note.AddTagRequest;
import com.lunanotes.mapper.note.NoteDTO;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.security.CurrentUserService;
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
import java.util.Objects;

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
class CurrentNoteControllerTest {

    @Value("${api.endpoint.base-url}/notes/me")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NoteService noteService;

    @MockitoBean
    CurrentUserService currentUserService;

    @Autowired
    ObjectMapper objectMapper;

    List<Note> notes;

    List<User> users;

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        User user2 = User.builder()
                .id(2L)
                .username("JohnDoe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        this.users = new ArrayList<>();
        this.users.add(user1);
        this.users.add(user2);
        this.notes = new ArrayList<>();
        notes.add(new Note(1L, "test1", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user1, new HashSet<>()));
        notes.add(new Note(2L, "test2", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user2, new HashSet<>()));
        notes.add(new Note(3L, "test3", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user1, new HashSet<>()));
        notes.add(new Note(4L, "test4", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user1, new HashSet<>()));
        notes.add(new Note(5L, "test5", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user2, new HashSet<>()));
    }

    @Test
    void findNoteById_ExistingNote_ShouldReturnNote() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));
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
    void findNoteById_NonExistingNote_ShouldThrowException() throws Exception {
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
        given(this.currentUserService.getCurrentUserId()).willReturn(this.users.get(0).getId());
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        List<Note> myNotes = this.notes.stream().filter(note -> Objects.equals(note.getOwner().getId(), this.users.get(0).getId())).toList();

        given(this.noteService.findAll()).willReturn(myNotes);

        this.mockMvc.perform(get(baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(myNotes.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].title").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("3"))
                .andExpect(jsonPath("$.data[1].title").value("test3"));
    }

    @Test
    void addNote_ShouldSave() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        NoteDTO noteDTO = new NoteDTO(0, "test", "lorem ipsum", 0, null, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        Note savedNote = new Note();
        savedNote.setId(1L);
        savedNote.setTitle("test");
        savedNote.setContent("lorem ipsum");

        given(this.noteService.save(any(Note.class))).willReturn(savedNote);

        this.mockMvc.perform(post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value("test"))
                .andExpect(jsonPath("$.data.content").value("lorem ipsum"));
    }

    @Test
    void updateNote_ExistingNote_ShouldUpdate() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("test");
        updatedNote.setContent("lorem ipsum2");

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.update(eq("1"), any(Note.class))).willReturn(updatedNote);

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
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.update(eq("1"), any(Note.class))).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(put(baseUrl + "/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        doNothing().when(this.noteService).delete("1");

        this.mockMvc.perform(delete(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException() throws Exception {
        doThrow(new ObjectNotFoundException("note", "1")).when(this.noteService).delete("1");

        this.mockMvc.perform(delete(baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addTag_ExistingNote_ShouldAddTag() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#00FFFF");

        AddTagRequest request = new AddTagRequest("1");
        String json = this.objectMapper.writeValueAsString(request);

        int tagsCount = notes.get(0).getTags().size();
        notes.get(0).getTags().add(tag);

        given(noteService.addTag("1", "1")).willReturn(this.notes.get(0));

        mockMvc.perform(post(baseUrl + "/1/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Tag Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount + 1));
    }

    @Test
    void addTag_NonExistingNote_ShouldThrowException() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        AddTagRequest request = new AddTagRequest("1");
        String json = this.objectMapper.writeValueAsString(request);

        given(noteService.addTag("1", "1")).willThrow(new ObjectNotFoundException("note", "1"));

        mockMvc.perform(post(baseUrl + "/1/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addMultipleTags_ShouldAddTags() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("test");
        tag1.setHexColor("#FF0000");
        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("test");
        tag2.setHexColor("#FF0000");
        List<Tag> tags = List.of(tag1, tag2);

        AddMultipleTagsRequest request = new AddMultipleTagsRequest(List.of("1", "2"));
        String json = this.objectMapper.writeValueAsString(request);

        int tagsCount = notes.get(0).getTags().size();
        notes.get(0).getTags().addAll(tags);

        given(noteService.addMultipleTags("1", request.tagNames())).willReturn(notes.get(0));

        mockMvc.perform(post(baseUrl + "/1/tags/batch").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Multiple Tags Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount + tags.size()));
    }

    @Test
    void removeTag_ExistingTag_ShouldDeleteTag() throws Exception {
        given(this.currentUserService.getCurrentUser()).willReturn(this.users.get(0));

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#FF0000");

        notes.get(0).getTags().add(tag);
        int tagsCount = notes.get(0).getTags().size();

        notes.get(0).removeTag(tag);

        given(noteService.removeTag("1", "1")).willReturn(notes.get(0));

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

        given(noteService.removeTag("1", "1")).willThrow(new ObjectNotFoundException("tag", "1"));

        mockMvc.perform(delete(baseUrl + "/1/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
