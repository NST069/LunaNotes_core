package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.AddMultipleTagsRequest;
import com.lunanotes.mapper.AddTagRequest;
import com.lunanotes.mapper.CreateTagRequest;
import com.lunanotes.mapper.NoteDTO;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class NoteControllerTest {

    @Value("${api.endpoint.base-url}")
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
        notes.add(new Note(2L, "test2", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false,  null, new HashSet<>()));
        notes.add(new Note(3L, "test3", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(4L, "test4", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, user, new HashSet<>()));
        notes.add(new Note(5L, "test5", "lorem ipsum", LocalDateTime.now(), LocalDateTime.now(), false, null, new HashSet<>()));
    }

    @Test
    void findById_ExistingNote_ShouldReturnNote() throws Exception{
        given(this.noteService.findById("1")).willReturn(this.notes.get(0));

        this.mockMvc.perform(get(baseUrl+"/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("test1"));

        this.mockMvc.perform(get(baseUrl+"/notes/note-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.title").value("test1"));
    }

    @Test
    void findById_NonExistingNote_ShouldThrowException() throws Exception{
        given(this.noteService.findById("1")).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(get(baseUrl+"/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(baseUrl+"/notes/note-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAllNotes_ShouldReturnList() throws Exception {
        given(this.noteService.findAll()).willReturn(this.notes);

        this.mockMvc.perform(get(baseUrl+"/notes").accept(MediaType.APPLICATION_JSON))
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

        this.mockMvc.perform(get(baseUrl+"/notes/user-1").accept(MediaType.APPLICATION_JSON))
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
    void addNote_ShouldSave() throws Exception {
        NoteDTO noteDTO = new NoteDTO(0, "test", "lorem ipsum", 0, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        Note savedNote = new Note();
        savedNote.setId(1L);
        savedNote.setTitle("test");
        savedNote.setContent("lorem ipsum");

        given(this.noteService.save(any(Note.class))).willReturn(savedNote);

        this.mockMvc.perform(post(baseUrl+"/notes").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
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

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.update(eq("1"), any(Note.class))).willReturn(updatedNote);

        this.mockMvc.perform(put(baseUrl+"/notes/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value(updatedNote.getTitle()))
                .andExpect(jsonPath("$.data.content").value(updatedNote.getContent()));
    }

    @Test
    void updateNote_NonExistingNote_ShouldThrowException() throws Exception {

        NoteDTO noteDTO = new NoteDTO(1, "test", "lorem ipsum2", 0, null);
        String json = this.objectMapper.writeValueAsString(noteDTO);

        given(this.noteService.update(eq("1"), any(Note.class))).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(put(baseUrl+"/notes/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_ExistingNote_ShouldDelete() throws Exception {
        doNothing().when(this.noteService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteNote_NonExistingNote_ShouldThrowException() throws Exception {
        doThrow(new ObjectNotFoundException("note", "1")).when(this.noteService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/notes/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getTags_ExistingNote_ShouldReturnTagsList() throws Exception {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("test1");
        tag1.setHexColor("#00FFFF");
        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("test2");
        tag2.setHexColor("#00FFFF");
        Set<Tag> tags = Set.of(tag1,tag2);
        notes.get(0).getTags().addAll(tags);

        given(this.noteService.getTags("1")).willReturn(this.notes.get(0).getTags());

        this.mockMvc.perform(get(baseUrl+"/notes/1/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Get Tags Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(tags.size())));

        this.mockMvc.perform(get(baseUrl+"/notes/note-1/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Get Tags Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(tags.size())));
    }

    @Test
    void getTags_NonExistingNote_ShouldThrowException() throws Exception {
        given(this.noteService.getTags("1")).willThrow(new ObjectNotFoundException("note", "1"));

        this.mockMvc.perform(get(baseUrl+"/notes/1/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(baseUrl+"/notes/note-1/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addTag_ExistingNote_ShouldAddTag() throws Exception{
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#00FFFF");

        AddTagRequest request = new AddTagRequest("1");
        String json = this.objectMapper.writeValueAsString(request);

        int tagsCount = notes.get(0).getTags().size();
        notes.get(0).getTags().add(tag);

        given(noteService.addTag("1", "1")).willReturn(this.notes.get(0));

        mockMvc.perform(post(baseUrl+"/notes/1/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Tag Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount+1));
    }

    @Test
    void addTag_NonExistingNote_ShouldThrowException() throws Exception{
        AddTagRequest request = new AddTagRequest("1");
        String json = this.objectMapper.writeValueAsString(request);

        given(noteService.addTag("1", "1")).willThrow(new ObjectNotFoundException("note", "1"));

        mockMvc.perform(post(baseUrl+"/notes/1/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find note with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void createAndAddTag_ExistingTag_ShouldAddTag() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#FF0000");
        CreateTagRequest request = new CreateTagRequest("test", "#FF0000");
        String json = this.objectMapper.writeValueAsString(request);

        int tagsCount = notes.get(0).getTags().size();
        notes.get(0).getTags().add(tag);

        given(noteService.createAndAddTag(eq("1"), any(CreateTagRequest.class))).willReturn(notes.get(0));

        mockMvc.perform(post(baseUrl+"/notes/1/tags/create").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Tag Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount+1));
    }

    @Test
    void addMultipleTags_ShouldAddExistingTags() throws Exception {
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

        given(noteService.addMultipleTags("1", request.tagIds())).willReturn(notes.get(0));

        mockMvc.perform(post(baseUrl+"/notes/1/tags/batch").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Multiple Tags Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount+tags.size()));
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

        given(noteService.removeTag("1", "1")).willReturn(notes.get(0));

        this.mockMvc.perform(delete(baseUrl+"/notes/1/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Remove Tag Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.numberOfTags").value(tagsCount-1));
    }

    @Test
    void removeTag_NonExistingTag_ShouldThrowException() throws Exception {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test");
        tag.setHexColor("#FF0000");

        notes.get(0).removeTag(tag);

        given(noteService.removeTag("1", "1")).willThrow(new ObjectNotFoundException("tag", "1"));

        mockMvc.perform(delete(baseUrl+"/notes/1/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}