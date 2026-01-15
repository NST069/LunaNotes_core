package com.lunanotes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String userName;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private String roles;

    private String firstName; //?

    @Getter @Setter
    private String lastName; //?

    @Getter @Setter
    private String telegramId; //?

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Tag> tags = new ArrayList<>();

    public void addNote(Note note){
        note.setOwner(this);
        this.notes.add(note);
    }

    @PrePersist
    protected void onCreate(){
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

}
