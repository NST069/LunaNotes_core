package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.TagJPARepository;
import com.lunanotes.security.CurrentUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagJPARepository tagJPARepository;

    private final CurrentUserService currentUserService;

    public Tag findById(String tagId){
        return this.tagJPARepository.findById(tagId)
                .orElseThrow(()->new ObjectNotFoundException("tag", tagId));
    }

    public List<Tag> findAll(){
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.tagJPARepository.findByOwnerId(currentUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Tag> findAllAdmin(){
        return this.tagJPARepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public List<Tag> findByOwnerId(String userId){
        return this.tagJPARepository.findByOwnerId(Long.parseLong(userId));
    }

    public Tag save(Tag newTag){
        if (newTag.getOwner() == null) {
            User currentUser = currentUserService.getCurrentUser();
            newTag.setOwner(currentUser);
        }

        return this.tagJPARepository.save(newTag);
    }

    public Tag update(String tagId, Tag tag){
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.tagJPARepository.findByIdAndOwnerId(Long.parseLong(tagId), currentUserId)
                .map(oldTag -> {
                    oldTag.setName(tag.getName());
                    oldTag.setHexColor(tag.getHexColor());

                    return this.tagJPARepository.save(oldTag);
                })
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public Tag updateAdmin(String tagId, Tag tag){
        return this.tagJPARepository.findById(tagId)
                .map(oldTag -> {
                    oldTag.setName(tag.getName());
                    oldTag.setHexColor(tag.getHexColor());

                    return this.tagJPARepository.save(oldTag);
                })
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
    }

    public void delete(String tagId){
        Long currentUserId = currentUserService.getCurrentUserId();
        this.tagJPARepository.findByIdAndOwnerId(Long.parseLong(tagId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
        this.tagJPARepository.deleteById(tagId);
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public void deleteAdmin(String tagId){
        this.tagJPARepository.findById(tagId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));
        this.tagJPARepository.deleteById(tagId);
    }
}
