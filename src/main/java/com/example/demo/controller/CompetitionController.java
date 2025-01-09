package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CompetitionType;
import com.example.demo.service.CompetitionService;

@RestController
@RequestMapping("/api/competitions")
@CrossOrigin
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;

    @PostMapping("/create")
    public ResponseEntity<?> createCompetition(
            @RequestParam Long groupId,
            @RequestParam String title,
            @RequestParam CompetitionType type) {
        return competitionService.createCompetition(groupId, title, type);
    }

    @GetMapping("/{competitionId}/results")
    public ResponseEntity<?> getResults(@PathVariable Long competitionId) {
        return competitionService.getCompetitionResults(competitionId);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupCompetitions(@PathVariable Long groupId) {
        return competitionService.getGroupCompetitions(groupId);
    }

    @GetMapping("/group/{groupId}/active")
    public ResponseEntity<?> getActiveCompetitions(@PathVariable Long groupId) {
        return competitionService.getActiveCompetitions(groupId);
    }
} 