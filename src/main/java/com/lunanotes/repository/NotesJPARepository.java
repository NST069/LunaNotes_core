package com.lunanotes.repository;

import com.lunanotes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesJPARepository extends JpaRepository<Note, String> {

    Optional<Note> findByTitleAndOwnerId(String title, Long ownerId);

    List<Note> findByOwnerId(Long authorId);

}
