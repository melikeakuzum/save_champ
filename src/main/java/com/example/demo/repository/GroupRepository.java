package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Group;
import com.example.demo.model.User;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreator(User creator);
    List<Group> findByMembersContaining(User member);
} 