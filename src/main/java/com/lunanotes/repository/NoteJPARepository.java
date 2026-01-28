package com.lunanotes.repository;

import com.lunanotes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteJPARepository extends JpaRepository<Note, String> {

    Optional<Note> findByIdAndOwnerId(Long noteId, Long ownerId);

    Optional<Note> findByTitleAndOwnerId(String title, Long ownerId);

    Optional<Note> findByIdAndIsPublic(Long noteId, boolean isPublic);

    List<Note> findByOwnerId(Long ownerId);

    List<Note> findByOwnerIdAndIsPublic(Long ownerId, boolean isPublic);

    List<Note> findByTagsId(Long tagId);


}
