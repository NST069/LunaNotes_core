package com.lunanotes.repository;

import com.lunanotes.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesCrudRepository extends CrudRepository<Note, Long> {

    Optional<Note> findByTitleAndAuthorId(String title, Long authorId);
}
