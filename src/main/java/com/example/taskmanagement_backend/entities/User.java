package com.example.taskmanagement_backend.entities;

import lombok.Data;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String email;

    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    @ManyToOne
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_user_organization"))
    private com.example.demo.entities.Organization organization;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
