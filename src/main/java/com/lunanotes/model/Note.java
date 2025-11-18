package com.lunanotes.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate(){
//        createdAt = updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate(){
//        updatedAt = LocalDateTime.now();
//    }
}
