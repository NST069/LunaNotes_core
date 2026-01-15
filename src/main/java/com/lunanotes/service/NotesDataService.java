package com.lunanotes.service;

import com.lunanotes.exception.ObjectNotFoundException;
import com.lunanotes.exception.UnauthorizedTagAccessException;
import com.lunanotes.mapper.CreateTagRequest;
import com.lunanotes.model.Note;
import com.lunanotes.model.Tag;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.repository.TagsJPARepository;
import com.lunanotes.util.IdWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final IdWorker idWorker;

    public Note findById(String noteId) {
        return this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public List<Note> findAll() {
        return this.notesJPARepository.findAll();
    }

    public List<Note> findByOwnerId(String ownerId) {
        return this.notesJPARepository.findByOwnerId(Long.parseLong(ownerId));
    }

    public Note save(Note newNote) {
        //newNote.setId(idWorker.nextId());
        return this.notesJPARepository.save(newNote);
    }

    public Note update(String noteId, Note note) {
        return this.notesJPARepository.findById(noteId)
                .map(oldNote -> {
                    oldNote.setTitle(note.getTitle());
                    oldNote.setContent(note.getContent());

                    return this.notesJPARepository.save(oldNote);
                })
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
    }

    public void delete(String noteId){
        this.notesJPARepository.findById(noteId)
            .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        this.notesJPARepository.deleteById(noteId);
    }

    public Set<Tag> getTags(String noteId){
        Note note = this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        return note.getTags();
    }

    public Note addTag(String noteId, String tagId){
        Note note = this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));
        Tag tag = this.tagsJPARepository.findById(tagId)
                .orElseThrow(()->new ObjectNotFoundException("tag", tagId));
        if (!note.getOwner().equals(tag.getOwner())) {
            throw new UnauthorizedTagAccessException(tagId, noteId);
        }

        note.addTag(tag);
        return notesJPARepository.save(note);
    }

    public Note removeTag(String noteId, String tagId) {
        Note note = notesJPARepository.findById(noteId)
                .orElseThrow(() -> new ObjectNotFoundException("note", noteId));

        Tag tag = tagsJPARepository.findById(tagId)
                .orElseThrow(() -> new ObjectNotFoundException("tag", tagId));

        note.removeTag(tag);
        return notesJPARepository.save(note);
    }

    public Note createAndAddTag(String noteId, CreateTagRequest request){
        Note note = notesJPARepository.findById(noteId)
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

    public Note addMultipleTags(String noteId, List<String> tagIds){
        Note note = notesJPARepository.findById(noteId)
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
