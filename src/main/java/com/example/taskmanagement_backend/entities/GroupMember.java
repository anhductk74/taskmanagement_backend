package com.example.taskmanagement_backend.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_groupmember_group"))
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_groupmember_user"))
    private User user;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}