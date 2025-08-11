package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamInvatationJpaRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findByTeamId(Long teamId);
}
