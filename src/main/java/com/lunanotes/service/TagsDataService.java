package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.TagsJPARepository;
import com.lunanotes.security.CurrentUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsDataService {

    private final TagsJPARepository tagsJPARepository;

    private final CurrentUserService currentUserService;

    public Tag findById(String tagId){
        return this.tagsJPARepository.findById(tagId)
                .orElseThrow(()->new ObjectNotFoundException("tag", tagId));
    }

    public List<Tag> findAll(){
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.tagsJPARepository.findByOwnerId(currentUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Tag> findAllAdmin(){
        return this.tagsJPARepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public List<Tag> findByOwnerId(String userId){
        return this.tagsJPARepository.findByOwnerId(Long.parseLong(userId));
    }

    public Tag save(Tag newTag){
        if (newTag.getOwner() == null) {
            User currentUser = currentUserService.getCurrentUser();
            newTag.setOwner(currentUser);
        }

        return this.tagsJPARepository.save(newTag);
    }

    public Tag update(String tagId, Tag tag){
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.tagsJPARepository.findByIdAndOwnerId(Long.parseLong(tagId), currentUserId)
                .map(oldTag -> {
                    oldTag.setName(tag.getName());
                    oldTag.setHexColor(tag.getHexColor());

                    return this.tagsJPARepository.save(oldTag);
                })
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public Tag updateAdmin(String tagId, Tag tag){
        return this.tagsJPARepository.findById(tagId)
                .map(oldTag -> {
                    oldTag.setName(tag.getName());
                    oldTag.setHexColor(tag.getHexColor());

                    return this.tagsJPARepository.save(oldTag);
                })
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
    }

    public void delete(String tagId){
        Long currentUserId = currentUserService.getCurrentUserId();
        this.tagsJPARepository.findByIdAndOwnerId(Long.parseLong(tagId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
        this.tagsJPARepository.deleteById(tagId);
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public void deleteAdmin(String tagId){
        this.tagsJPARepository.findById(tagId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
        this.tagsJPARepository.deleteById(tagId);
    }
}
