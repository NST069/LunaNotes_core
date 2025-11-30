package com.lunanotes.service;

import com.lunanotes.exception.TagNotFoundException;
import com.lunanotes.model.Tag;
import com.lunanotes.repository.TagsJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TagsDataService {

    private final TagsJPARepository tagsJPARepository;

    public TagsDataService(TagsJPARepository tagsJPARepository) {
        this.tagsJPARepository = tagsJPARepository;
    }

    public Tag findById(String tagId){
        return this.tagsJPARepository.findById(tagId)
                .orElseThrow(()->new TagNotFoundException(tagId));
    }

    public List<Tag> findAll(){
        return this.tagsJPARepository.findAll();
    }

    public List<Tag> findByOwnerId(String userId){
        return this.tagsJPARepository.findByOwnerId(Long.parseLong(userId));
    }

    public Tag save(Tag newTag){
        return this.tagsJPARepository.save(newTag);
    }

    public Tag update(String tagId, Tag tag){
        return this.tagsJPARepository.findById(tagId)
                .map(oldTag -> {
                    oldTag.setName(tag.getName());
                    oldTag.setHexColor(tag.getHexColor());

                    return this.tagsJPARepository.save(oldTag);
                })
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }

    public void delete(String tagId){
        this.tagsJPARepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        this.tagsJPARepository.deleteById(tagId);
    }
}
