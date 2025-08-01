package com.example.taskmanagement_backend.services;

import com.example.taskmanagement_backend.entities.Role;
import com.example.taskmanagement_backend.repositories.RoleJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleJpaRepository roleJpaRepository;
    public RoleService(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }
    public List<Role> getAllRoles() {
        return roleJpaRepository.findAll();
    }

}
