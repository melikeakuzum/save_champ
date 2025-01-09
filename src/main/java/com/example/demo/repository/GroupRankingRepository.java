package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Group;
import com.example.demo.model.GroupRanking;
import com.example.demo.model.User;

public interface GroupRankingRepository extends JpaRepository<GroupRanking, Long> {
    
    @Query("SELECT gr FROM GroupRanking gr WHERE gr.group = :group ORDER BY gr.rank ASC")
    List<GroupRanking> findByGroupOrderByRankAsc(@Param("group") Group group);
    
    @Query("SELECT gr FROM GroupRanking gr WHERE gr.user = :user AND gr.rank <= 3 ORDER BY gr.rank ASC")
    List<GroupRanking> findTop3ByUser(@Param("user") User user);
    
    GroupRanking findByGroupAndUser(Group group, User user);
} 