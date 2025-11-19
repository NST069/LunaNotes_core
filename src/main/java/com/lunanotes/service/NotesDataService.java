package com.lunanotes.service;

import com.lunanotes.exception.NoteNotFoundException;
import com.lunanotes.model.Note;
import com.lunanotes.repository.NotesJPARepository;
import com.lunanotes.util.IdWorker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class NotesDataService {

    @Autowired
    private final NotesJPARepository notesJPARepository;

    private final IdWorker idWorker;

    public NotesDataService(NotesJPARepository notesJPARepository, IdWorker idWorker) {
        this.notesJPARepository = notesJPARepository;
        this.idWorker = idWorker;
    }

    public Note findById(String noteId) {
        return this.notesJPARepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    public List<Note> findAll() {
        return this.notesJPARepository.findAll();
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
                .orElseThrow(() -> new NoteNotFoundException(noteId));
    }

    public void delete(String noteId){
        this.notesJPARepository.findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException(noteId));
        this.notesJPARepository.deleteById(noteId);
    }

}
