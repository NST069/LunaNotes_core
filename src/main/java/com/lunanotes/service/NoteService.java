package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.exception.UnauthorizedTagAccessException;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.model.User;
import com.lunanotes.repository.NoteJPARepository;
import com.lunanotes.repository.TagJPARepository;
import com.lunanotes.security.CurrentUserService;
import com.lunanotes.util.IdWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteJPARepository noteJPARepository;

    private final TagJPARepository tagJPARepository;

    private final CurrentUserService currentUserService;

    private final IdWorker idWorker;

    public Note findById(String noteId) {
        return this.noteJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public List<Note> findAll() {
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.noteJPARepository.findByOwnerId(currentUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Note> findAllAdmin() {
        return this.noteJPARepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public List<Note> findByOwnerId(String ownerId) {
        return this.noteJPARepository.findByOwnerId(Long.parseLong(ownerId));
    }

    public Note save(Note newNote) {
        //newNote.setId(idWorker.nextId());

        if (newNote.getOwner() == null) {
            User currentUser = currentUserService.getCurrentUser();
            newNote.setOwner(currentUser);
        }

        return this.noteJPARepository.save(newNote);
    }

    public Note update(String noteId, Note note) {
        Long currentUserId = currentUserService.getCurrentUserId();
        return this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .map(oldNote -> {
                    oldNote.setTitle(note.getTitle());
                    oldNote.setContent(note.getContent());

                    return this.noteJPARepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public Note updateAdmin(String noteId, Note note) {
        return this.noteJPARepository.findById(noteId)
                .map(oldNote -> {
                    oldNote.setTitle(note.getTitle());
                    oldNote.setContent(note.getContent());

                    return this.noteJPARepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public void delete(String noteId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        this.noteJPARepository.deleteById(noteId);
    }

    @PreAuthorize("hasRole('ADMIN', 'MODERATOR')")
    public void deleteAdmin(String noteId) {
        this.noteJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        this.noteJPARepository.deleteById(noteId);
    }

    public Set<Tag> getTags(String noteId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        return note.getTags();
    }

    public Note addTag(String noteId, String tagName) {
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        Tag tag = checkTagOrCreate(tagName);
        if (!note.getOwner().equals(tag.getOwner())) {
            throw new UnauthorizedTagAccessException(tag.getId().toString(), noteId);
        }

        note.addTag(tag);
        return noteJPARepository.save(note);
    }

    public Note removeTag(String noteId, String tagId) {
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        Tag tag = tagJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));

        note.removeTag(tag);
        return noteJPARepository.save(note);
    }

    public Note addMultipleTags(String noteId, List<String> tagNames) {
        Long currentUserId = currentUserService.getCurrentUserId();
        Note note = this.noteJPARepository.findByIdAndOwnerId(Long.parseLong(noteId), currentUserId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        List<Tag> tags = tagNames.stream().map(this::checkTagOrCreate).toList();

        if (tags.size() != tagNames.size()) {
            log.warn("One or more tags not found");
        }

        tags.forEach(tag -> {
            if (!note.getOwner().equals(tag.getOwner())) {
                throw new UnauthorizedTagAccessException(tag.getId().toString(), noteId);
            }
        });

        tags.forEach(note::addTag);
        return noteJPARepository.save(note);
    }


    private Tag checkTagOrCreate(String tagName) {
        User owner = currentUserService.getCurrentUser();
        Optional<Tag> existingTag = tagJPARepository.findByNameAndOwnerId(
                tagName, owner.getId()
        );

        Tag tag;
        if (existingTag.isPresent()) {
            tag = existingTag.get();
        } else {
            tag = Tag.builder()
                    .name(tagName)
                    .owner(owner)
                    .hexColor(getRandomColor())
                    .build();
            tag = tagJPARepository.save(tag);
        }
        return tag;
    }

    private @NonNull String getRandomColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", rand_num);
        return colorCode;
    }

}
