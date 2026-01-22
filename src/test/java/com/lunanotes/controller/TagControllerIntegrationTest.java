package com.lunanotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Tags API endpoints")
@Tag("integration")
@ActiveProfiles("test")
public class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(post(this.baseUrl+"/auth/login")
                        .with(httpBasic("User1", "password1")));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(content);
        this.token = "Bearer "+json.getJSONObject("data").getString("token");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findById_ExistingTag_ShouldReturnTag() throws Exception{
        this.mockMvc.perform(get(this.baseUrl + "/tags/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"));

        this.mockMvc.perform(get(this.baseUrl + "/tags/tag-1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void findById_NonExistingTag_ShouldThrowException() throws Exception{
        this.mockMvc.perform(get(baseUrl+"/tags/9999").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(baseUrl+"/tags/tag-9999").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findAllTags_ShouldReturnList() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/tags").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findAllTagsByUserId_ShouldReturnList() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/tags/user-1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All For User Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addTag_ShouldSave() throws Exception {
        com.lunanotes.model.Tag tag =  new com.lunanotes.model.Tag();
        tag.setName("test");
        tag.setHexColor("#FF0000");

        String json = this.objectMapper.writeValueAsString(tag);

        this.mockMvc.perform(post(this.baseUrl + "/tags").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.hexColor").value("#FF0000"));
        this.mockMvc.perform(get(this.baseUrl + "/tags").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateTag_ExistingTag_ShouldUpdate() throws Exception {
        com.lunanotes.model.Tag tag =  new com.lunanotes.model.Tag();
        tag.setName("test2");
        tag.setHexColor("#FF00CC");

        String json = this.objectMapper.writeValueAsString(tag);

        this.mockMvc.perform(put(baseUrl+"/tags/1").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("test2"))
                .andExpect(jsonPath("$.data.hexColor").value("#FF00CC"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateTag_NonExistingTag_ShouldThrowException() throws Exception {
        com.lunanotes.model.Tag tag =  new com.lunanotes.model.Tag();
        tag.setName("test2");
        tag.setHexColor("#FF00CC");

        String json = this.objectMapper.writeValueAsString(tag);

        this.mockMvc.perform(put(baseUrl+"/tags/9999").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteTag_ExistingTag_ShouldDelete() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/tags/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Success"));
        this.mockMvc.perform(get(this.baseUrl + "/tags/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteTag_NonExistingTag_ShouldThrowException() throws Exception {
        this.mockMvc.perform(delete(baseUrl+"/tags/9999").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Could not find tag with Id 9999"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
