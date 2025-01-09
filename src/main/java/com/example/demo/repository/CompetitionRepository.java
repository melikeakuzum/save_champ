package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Competition;
import com.example.demo.model.Group;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByGroup(Group group);
    List<Competition> findByGroupAndActiveTrue(Group group);
    
    @Query("SELECT c FROM Competition c WHERE c.group.id = :groupId")
    List<Competition> findByGroupId(@Param("groupId") Long groupId);
    
    @Query("SELECT c FROM Competition c WHERE c.group.id = :groupId AND c.active = true")
    List<Competition> findActiveByGroupId(@Param("groupId") Long groupId);
} 