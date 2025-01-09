package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ExpenseCreateDTO;
import com.example.demo.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    public ResponseEntity<String> createExpense(@RequestBody ExpenseCreateDTO dto) {
        return expenseService.createExpense(dto);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupExpenses(@PathVariable Long groupId) {
        return expenseService.getGroupExpenses(groupId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserExpenses(@PathVariable Long userId) {
        return expenseService.getUserExpenses(userId);
    }

    @GetMapping("/user/{userId}/groups")
    public ResponseEntity<?> getUserGroupExpenses(@PathVariable Long userId) {
        return expenseService.getUserGroupExpenses(userId);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(
            @PathVariable Long expenseId,
            @RequestParam Long userId) {
        return expenseService.deleteExpense(expenseId, userId);
    }

    @GetMapping("/group-ranking")
    public ResponseEntity<?> getGroupExpenseRanking(
            @RequestParam Long groupId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String period) {
        return expenseService.getGroupExpenseRanking(groupId, startDate, endDate, period);
    }
}