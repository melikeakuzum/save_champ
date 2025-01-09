package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Competition;
import com.example.demo.model.CompetitionType;
import com.example.demo.model.Expense;
import com.example.demo.model.Group;
import com.example.demo.model.User;
import com.example.demo.repository.CompetitionRepository;
import com.example.demo.repository.ExpenseRepository;

@Service
public class CompetitionService {
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private GroupService groupService;

    public ResponseEntity<?> createCompetition(Long groupId, String title, CompetitionType type) {
        try {
            Competition competition = new Competition();
            Group group = groupService.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }
            
            competition.setGroup(group);
            competition.setTitle(title);
            competition.setType(type);
            competition.setActive(true);
            
            LocalDateTime now = LocalDateTime.now();
            competition.setStartDate(now);
            competition.setEndDate(type == CompetitionType.WEEKLY ? 
                now.plusWeeks(1) : now.plusMonths(1));
            
            competitionRepository.save(competition);
            return ResponseEntity.ok("Yarışma başarıyla oluşturuldu!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getCompetitionResults(Long competitionId) {
        try {
            Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Yarışma bulunamadı!"));

            List<Expense> expenses = expenseRepository.findByGroupAndCreatedAtBetween(
                competition.getGroup(),
                competition.getStartDate(),
                competition.getEndDate()
            );

            Map<User, Double> userExpenses = new HashMap<>();
            expenses.forEach(expense -> {
                userExpenses.merge(expense.getUser(), 
                    expense.getAmount().doubleValue(), Double::sum);
            });

            List<Map<String, Object>> results = userExpenses.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("username", entry.getKey().getUsername());
                    result.put("totalExpense", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());

            for (int i = 0; i < results.size(); i++) {
                results.get(i).put("rank", i + 1);
            }

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getGroupCompetitions(Long groupId) {
        try {
            Group group = groupService.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            List<Competition> competitions = competitionRepository.findByGroup(group);
            List<Map<String, Object>> result = competitions.stream()
                .map(competition -> {
                    Map<String, Object> competitionData = new HashMap<>();
                    competitionData.put("id", competition.getId());
                    competitionData.put("title", competition.getTitle());
                    competitionData.put("type", competition.getType());
                    competitionData.put("startDate", competition.getStartDate());
                    competitionData.put("endDate", competition.getEndDate());
                    competitionData.put("active", competition.isActive());
                    return competitionData;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getActiveCompetitions(Long groupId) {
        try {
            Group group = groupService.findGroupById(groupId);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            List<Competition> activeCompetitions = competitionRepository.findByGroupAndActiveTrue(group);
            List<Map<String, Object>> result = activeCompetitions.stream()
                .map(competition -> {
                    Map<String, Object> competitionData = new HashMap<>();
                    competitionData.put("id", competition.getId());
                    competitionData.put("title", competition.getTitle());
                    competitionData.put("type", competition.getType());
                    competitionData.put("startDate", competition.getStartDate());
                    competitionData.put("endDate", competition.getEndDate());
                    competitionData.put("active", competition.isActive());
                    return competitionData;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
} 