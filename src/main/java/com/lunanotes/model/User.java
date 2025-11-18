package com.lunanotes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String firstName; //?
    private String lastName; //?
    private String telegramId; //?

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // remake to spring conf
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
