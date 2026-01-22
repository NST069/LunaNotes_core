package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.mapper.TagDTO;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.service.TagService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TagControllerTest {

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TagService tagService;

    @Autowired
    ObjectMapper objectMapper;

    List<Tag> tags;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .password("password")
                .roles(UserRole.USER.name())
                .build();
        this.tags = new ArrayList<>();
        tags.add(new Tag(1L, "test1","#000000", LocalDateTime.now(), LocalDateTime.now(), user));
        tags.add(new Tag(2L, "test2", "#000000", LocalDateTime.now(), LocalDateTime.now(), null));
        tags.add(new Tag(3L, "test3", "#000000", LocalDateTime.now(), LocalDateTime.now(), user));
        tags.add(new Tag(4L, "test4", "#000000", LocalDateTime.now(), LocalDateTime.now(), null));
        tags.add(new Tag(5L, "test5", "#000000", LocalDateTime.now(), LocalDateTime.now(), user));
    }

    @Test
    void findById_ExistingTag_ShouldReturnTag() throws Exception{
        given(this.tagService.findById("1")).willReturn(this.tags.get(0));

        this.mockMvc.perform(get(baseUrl+"/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("test1"));

        this.mockMvc.perform(get(baseUrl+"/tags/tag-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("test1"));
    }

    @Test
    void findById_NonExistingTag_ShouldThrowException() throws Exception{
        given(this.tagService.findById("1")).willThrow(new ObjectNotFoundException("tag", "1"));

        this.mockMvc.perform(get(baseUrl+"/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(baseUrl+"/tags/tag-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAllTags_ShouldReturnList() throws Exception {
        given(this.tagService.findAll()).willReturn(this.tags);

        this.mockMvc.perform(get(baseUrl+"/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.tags.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].name").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].name").value("test2"));
    }

    @Test
    void findAllTagsByUserId_ShouldReturnList() throws Exception {
        List<Tag> filteredTags = this.tags.stream()
                .filter(tag -> ((tag.getOwner() != null) ? tag.getOwner().getId() : 0) == 1).toList();

        given(this.tagService.findByOwnerId("1")).willReturn(filteredTags);

        this.mockMvc.perform(get(baseUrl+"/tags/user-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All For User Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(filteredTags.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].name").value("test1"))
                .andExpect(jsonPath("$.data[1].id").value("3"))
                .andExpect(jsonPath("$.data[1].name").value("test3"));
    }

    @Test
    void addTag_ShouldSave() throws Exception {
        TagDTO tagDTO = new TagDTO(0, "test", "#FFFFFF", null);
        String json = this.objectMapper.writeValueAsString(tagDTO);

        Tag savedTag = new Tag();
        savedTag.setId(1L);
        savedTag.setName("test");
        savedTag.setHexColor("#FF0000");

        given(this.tagService.save(Mockito.any(Tag.class))).willReturn(savedTag);

        this.mockMvc.perform(post(baseUrl+"/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.hexColor").value("#FF0000"));
    }

    @Test
    void updateTag_ExistingTag_ShouldUpdate() throws Exception {
        Tag updatedTag = new Tag();
        updatedTag.setId(1L);
        updatedTag.setName("test");
        updatedTag.setHexColor("#00FFFF");

        TagDTO tagDTO = new TagDTO(1, "test", "#00FFFF", null);
        String json = this.objectMapper.writeValueAsString(tagDTO);

        given(this.tagService.update(eq("1"), Mockito.any(Tag.class))).willReturn(updatedTag);

        this.mockMvc.perform(put(baseUrl+"/tags/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value(updatedTag.getName()))
                .andExpect(jsonPath("$.data.hexColor").value(updatedTag.getHexColor()));
    }

    @Test
    void updateTag_NonExistingTag_ShouldThrowException() throws Exception {

        TagDTO tagDTO = new TagDTO(1, "test", "#FFFF00", null);
        String json = this.objectMapper.writeValueAsString(tagDTO);

        given(this.tagService.update(eq("1"), Mockito.any(Tag.class))).willThrow(new ObjectNotFoundException("tag", "1"));

        this.mockMvc.perform(put(baseUrl+"/tags/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteTag_ExistingTag_ShouldDelete() throws Exception {
        doNothing().when(this.tagService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteTag_NonExistingTag_ShouldThrowException() throws Exception {
        doThrow(new ObjectNotFoundException("tag", "1")).when(this.tagService).delete("1");

        this.mockMvc.perform(delete(baseUrl+"/tags/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}