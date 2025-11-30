package com.lunanotes.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tags")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    private String name;

    @ManyToOne
    @JoinColumn(name="owner_id")
    @Getter @Setter
    private User owner;

    @ManyToMany(mappedBy = "tags")
    private List<Note> notes = new ArrayList<>();

    @Getter @Setter
    private String hexColor;

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private LocalDateTime updatedAt;
}
