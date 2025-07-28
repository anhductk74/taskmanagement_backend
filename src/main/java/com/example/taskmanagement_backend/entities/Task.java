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
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String status;

    private String priority;

    private LocalDate deadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "assigned_to", foreignKey = @ForeignKey(name = "fk_task_assigned_to"))
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_task_group"))
    private Group group;

    @ManyToOne
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_task_project"))
    private Project project;

    @ManyToOne
    @JoinColumn(name = "creator_id", foreignKey = @ForeignKey(name = "fk_task_creator"))
    private User creator;
}
