package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamJpaRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM Team t JOIN t.projectTeams pt WHERE pt.project.id = :projectId")
    List<Team> findTeamsByProjectId(@Param("projectId") Long projectId);
    List<Team> findByProjectTeams_Project_Id(Long projectId);
}
