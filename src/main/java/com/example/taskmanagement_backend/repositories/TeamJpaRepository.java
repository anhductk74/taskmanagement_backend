package com.example.taskmanagement_backend.repositories;

import com.example.taskmanagement_backend.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamJpaRepository extends JpaRepository<Team, Long> {

}
