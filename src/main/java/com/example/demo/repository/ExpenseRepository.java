package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Expense;
import com.example.demo.model.Group;
import com.example.demo.model.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserAndVisibleInListTrue(User user);
    List<Expense> findByGroupAndVisibleInListTrue(Group group);
    
    @Query("SELECT e FROM Expense e WHERE e.group = :group AND e.user = :user")
    List<Expense> findByGroupAndUser(@Param("group") Group group, @Param("user") User user);
    
    @Query("SELECT e FROM Expense e WHERE e.group = :group")
    List<Expense> findByGroup(@Param("group") Group group);
    
    @Query("SELECT e FROM Expense e WHERE e.group = :group AND e.createdAt BETWEEN :startDate AND :endDate")
    List<Expense> findByGroupAndCreatedAtBetween(
        @Param("group") Group group, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
} 