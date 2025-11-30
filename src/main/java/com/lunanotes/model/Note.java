package com.lunanotes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="notes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private String content;

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name="owner_id")
    @Getter @Setter
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "note_tag",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Getter
    @Builder.Default
    private final Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate(){
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public void addTag(Tag tag){
        this.tags.add(tag);
        tag.getNotes().add(this);
    }

    public void removeTag(Tag tag){
        this.tags.remove(tag);
        tag.getNotes().remove(this);
    }

    public int getNumberOfTags() {
        return this.tags.size();
    }
}
