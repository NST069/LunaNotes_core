package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.exception.UnauthorizedTagAccessException;
import com.lunanotes.mapper.CreateTagRequest;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.repository.TagsJPARepository;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.util.IdWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotesDataService {

    private final NotesJPARepository notesJPARepository;

    private final TagsJPARepository tagsJPARepository;

    private final CurrentUserService currentUserService;

    private final IdWorker idWorker;

    public Note findById(String noteId) {
        return this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public List<Note> findAll() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.notesJPARepository.findByOwnerId(currentUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Note> findAllAdmin() {
        return this.notesJPARepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public List<Note> findByOwnerId(String ownerId) {
        return this.notesJPARepository.findByOwnerId(Long.parseLong(ownerId));
    }

    public Note save(Note newNote) {
        //newNote.setId(idWorker.nextId());

        if (newNote.getOwner() == null) {
            User currentUser = currentUserService.getCurrentUser();
            newNote.setOwner(currentUser);
        }

        return this.notesJPARepository.save(newNote);
    }

    public Note update(String noteId, Note note) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .map(oldNote -> {
                    oldNote.setTitle(note.getTitle());
                    oldNote.setContent(note.getContent());

                    return this.notesJPARepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public Note updateAdmin(String noteId, Note note) {
        return this.notesJPARepository.findById(noteId)
                .map(oldNote -> {
                    oldNote.setTitle(note.getTitle());
                    oldNote.setContent(note.getContent());

                    return this.notesJPARepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public void delete(String noteId){
        Long currentUserId = currentUserService.getCurrentUserId();
        this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        this.notesJPARepository.deleteById(noteId);
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public void deleteAdmin(String noteId){
        this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        this.notesJPARepository.deleteById(noteId);
    }

    public Set<Tag> getTags(String noteId){
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        return note.getTags();
    }

    public Note addTag(String noteId, String tagId){
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        Tag tag = this.tagsJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(()->new ObjectNotFoundException("tag", tagId));
        if (!note.getOwner().equals(tag.getOwner())) {
            throw new UnauthorizedTagAccessException(tagId, noteId);
        }

        note.addTag(tag);
        return notesJPARepository.save(note);
    }

    public Note removeTag(String noteId, String tagId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        Tag tag = tagsJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));

        note.removeTag(tag);
        return notesJPARepository.save(note);
    }

    public Note createAndAddTag(String noteId, CreateTagRequest request){
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        Optional<Tag> existingTag = tagsJPARepository.findByNameAndOwnerId(
                request.name(), note.getOwner().getId()
        );

        Tag tag;
        if(existingTag.isPresent()){
            tag = existingTag.get();
        }
        else{
            tag = Tag.builder()
                    .name(request.name())
                    .owner(note.getOwner())
                    .hexColor(request.hexColor())
                    .build();
            tag = tagsJPARepository.save(tag);
        }

        note.addTag(tag);
        return notesJPARepository.save(note);
    }

    @Deprecated
    public Note addMultipleTags(String noteId, List<String> tagIds){
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.notesJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        List<Tag> tags = tagsJPARepository.findAllById(tagIds);

        if (tags.size() != tagIds.size()) {
            log.warn("One or more tags not found");
        }

        tags.forEach(tag -> {
            if (!note.getOwner().equals(tag.getOwner())) {
                throw new UnauthorizedTagAccessException(tag.getId().toString(), noteId);
            }
        });

        tags.forEach(note::addTag);
        return notesJPARepository.save(note);
    }

}
