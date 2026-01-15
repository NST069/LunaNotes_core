package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Tag;
import com.lunanotes.repository.TagsJPARepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsDataService {

    private final TagsJPARepository tagsJPARepository;

    public Tag findById(String tagId){
        return this.tagsJPARepository.findById(tagId)
                .orElseThrow(()->new ObjectNotFoundException("tag", tagId));
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
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
    }

    public void delete(String tagId){
        this.tagsJPARepository.findById(tagId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
        this.tagsJPARepository.deleteById(tagId);
    }
}
