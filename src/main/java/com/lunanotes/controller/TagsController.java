package com.lunanotes.controller;

import com.lunanotes.mapper.TagDTO;
import com.lunanotes.mapper.TagDTOToTagConverter;
import com.lunanotes.mapper.TagToTagDTOConverter;
import com.lunanotes.model.Tag;
import com.lunanotes.service.TagsDataService;
import com.lunanotes.util.Result;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/tags")
public class TagsController {

    private final TagsDataService tagsDataService;

    private final TagToTagDTOConverter tagToTagDTOConverter;

    private final TagDTOToTagConverter tagDTOToTagConverter;

    public TagsController(TagsDataService tagsDataService, TagToTagDTOConverter tagToTagDTOConverter, TagDTOToTagConverter tagDTOToTagConverter) {
        this.tagsDataService = tagsDataService;
        this.tagToTagDTOConverter = tagToTagDTOConverter;
        this.tagDTOToTagConverter = tagDTOToTagConverter;
    }

    @GetMapping(value = {"/{tagId}", "/tag-{tagId}"})
    public Result findTagById(@PathVariable String tagId) {
        Tag foundTag = this.tagsDataService.findById(tagId);
        TagDTO tagDTO = this.tagToTagDTOConverter.convert(foundTag);
        return new Result(true, HttpStatus.OK.value(), "Find One Success", tagDTO);
    }

    @GetMapping
    public Result findAllTags() {
        List<Tag> foundTags = this.tagsDataService.findAll();
        List<TagDTO> tagDTOs = foundTags.stream().map(this.tagToTagDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All Success", tagDTOs);
    }

    @GetMapping(value = "/user-{userId}")
    public Result findAllTagsOfUser(@PathVariable String userId) {
        List<Tag> foundTags = this.tagsDataService.findByOwnerId(userId);
        List<TagDTO> tagDTOs = foundTags.stream().map(this.tagToTagDTOConverter::convert).collect(Collectors.toList());

        return new Result(true, HttpStatus.OK.value(), "Find All For User Success", tagDTOs);
    }

    @PostMapping
    public Result addTag(@Valid @RequestBody TagDTO tagDTO) {
        Tag newTag = this.tagDTOToTagConverter.convert(tagDTO);
        Tag savedTag = this.tagsDataService.save(newTag);
        TagDTO savedTagDTO = this.tagToTagDTOConverter.convert(savedTag);
        return new Result(true, HttpStatus.OK.value(), "Add Success", savedTagDTO);
    }

    @PutMapping(value = "/{tagId}")
    public Result updateTag(@PathVariable String tagId, @Valid @RequestBody TagDTO tagDTO) {
        Tag update = this.tagDTOToTagConverter.convert(tagDTO);
        Tag updatedTag = this.tagsDataService.update(tagId, update);
        TagDTO updatedTagDTO = this.tagToTagDTOConverter.convert(updatedTag);
        return new Result(true, HttpStatus.OK.value(), "Update Success", updatedTagDTO);
    }

    @DeleteMapping(value = "/{tagId}")
    public Result deleteTag(@PathVariable String tagId) {
        this.tagsDataService.delete(tagId);
        return new Result(true, HttpStatus.OK.value(), "Delete Success", null);
    }
}
