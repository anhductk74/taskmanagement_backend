package com.example.taskmanagement_backend.entities;

import com.example.taskmanagement_backend.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
