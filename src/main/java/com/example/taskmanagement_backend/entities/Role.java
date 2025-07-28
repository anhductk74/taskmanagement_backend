package com.example.taskmanagement_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organizations")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
}
