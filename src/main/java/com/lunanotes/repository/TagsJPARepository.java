package com.lunanotes.repository;

import com.lunanotes.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagsJPARepository extends JpaRepository<Tag, String> {

    Optional<Tag> findByNameAndOwnerId(String name, Long ownerId);

    List<Tag> findByOwnerId(Long ownerId);
}
