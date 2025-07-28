package com.example.taskmanagement_backend.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_project_owner"))
    private User owner;

    @ManyToOne
    @JoinColumn(name = "pm_id", foreignKey = @ForeignKey(name = "fk_project_pm"))
    private User projectManager;

    @ManyToOne
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_project_organization"))
    private com.example.taskmanagement_backend.entities.Organization organization;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
