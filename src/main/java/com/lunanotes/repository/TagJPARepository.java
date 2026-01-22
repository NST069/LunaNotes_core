package com.lunanotes.repository;

import com.lunanotes.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagJPARepository extends JpaRepository<Tag, String> {

    Optional<Tag> findByIdAndOwnerId(Long noteId, Long ownerId);

    Optional<Tag> findByNameAndOwnerId(String name, Long ownerId);

    List<Tag> findByOwnerId(Long ownerId);

    List<Tag> findAllByOwnerId(Long ownerId);

    List<Tag> findByNotesId(Long noteId);
}
