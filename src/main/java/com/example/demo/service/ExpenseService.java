package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ExpenseCreateDTO;
import com.example.demo.model.Expense;
import com.example.demo.model.Group;
import com.example.demo.model.GroupRanking;
import com.example.demo.model.User;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.GroupRankingRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupRankingRepository groupRankingRepository;

    public ResponseEntity<String> createExpense(ExpenseCreateDTO dto) {
        try {
            User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

            if (dto.getGroupIds() == null || dto.getGroupIds().isEmpty()) {
                return ResponseEntity.badRequest().body("En az bir grup seçilmelidir!");
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(dto.getAmount().toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Geçersiz harcama tutarı!");
            }

            List<Group> selectedGroups = new ArrayList<>();
            
            // Seçilen tüm grupları al
            for (Long groupId : dto.getGroupIds()) {
                Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Grup bulunamadı: " + groupId));
                selectedGroups.add(group);
            }

            // Ana harcama kaydını oluştur
            Expense mainExpense = new Expense();
            mainExpense.setGroup(selectedGroups.get(0));
            mainExpense.setUser(user);
            mainExpense.setDescription(dto.getDescription());
            mainExpense.setAmount(amount);
            mainExpense.setCategory(dto.getCategory());
            mainExpense.setCreatedAt(LocalDateTime.now());
            mainExpense.setVisibleInList(true);
            mainExpense = expenseRepository.save(mainExpense);

            // Eğer harcama bölüştürülecekse
            if (dto.isSplit()) {
                // İlk gruptaki üye sayısına göre tutarı böl
                BigDecimal splitAmount = amount.divide(BigDecimal.valueOf(selectedGroups.get(0).getMembers().size()), 2, RoundingMode.HALF_UP);

                // Ana harcama kaydını güncelle
                mainExpense.setAmount(splitAmount);
                expenseRepository.save(mainExpense);

                // İlk gruptaki diğer üyeler için harcama kayıtları oluştur
                for (User member : selectedGroups.get(0).getMembers()) {
                    if (!member.getId().equals(user.getId())) {
                        Expense splitExpense = new Expense();
                        splitExpense.setGroup(selectedGroups.get(0));
                        splitExpense.setUser(member);
                        splitExpense.setDescription(dto.getDescription() + " (Bölüştürülmüş)");
                        splitExpense.setAmount(splitAmount);
                        splitExpense.setCategory(dto.getCategory());
                        splitExpense.setCreatedAt(LocalDateTime.now());
                        splitExpense.setVisibleInList(true);
                        expenseRepository.save(splitExpense);
                    }
                }
            }

            // Diğer gruplar için kayıtlar oluştur
            for (int i = 1; i < selectedGroups.size(); i++) {
                Group group = selectedGroups.get(i);
                
                Expense groupExpense = new Expense();
                groupExpense.setGroup(group);
                groupExpense.setUser(user);
                groupExpense.setDescription(dto.getDescription());
                groupExpense.setAmount(amount);
                groupExpense.setCategory(dto.getCategory());
                groupExpense.setCreatedAt(LocalDateTime.now());
                groupExpense.setVisibleInList(false); // Diğer gruplar için görünmez yap
                expenseRepository.save(groupExpense);

                if (dto.isSplit()) {
                    BigDecimal splitAmount = amount.divide(BigDecimal.valueOf(group.getMembers().size()), 2, RoundingMode.HALF_UP);
                    
                    groupExpense.setAmount(splitAmount);
                    expenseRepository.save(groupExpense);

                    for (User member : group.getMembers()) {
                        if (!member.getId().equals(user.getId())) {
                            Expense splitExpense = new Expense();
                            splitExpense.setGroup(group);
                            splitExpense.setUser(member);
                            splitExpense.setDescription(dto.getDescription() + " (Bölüştürülmüş)");
                            splitExpense.setAmount(splitAmount);
                            splitExpense.setCategory(dto.getCategory());
                            splitExpense.setCreatedAt(LocalDateTime.now());
                            splitExpense.setVisibleInList(false); // Diğer gruplar için görünmez yap
                            expenseRepository.save(splitExpense);
                        }
                    }
                }
            }
            
            return ResponseEntity.ok("Harcama başarıyla kaydedildi!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getGroupExpenses(Long groupId) {
        try {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı!"));
            
            List<Expense> expenses = expenseRepository.findByGroupAndVisibleInListTrue(group);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getUserExpenses(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
            
            List<Expense> expenses = expenseRepository.findByUserAndVisibleInListTrue(user);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getUserGroupExpenses(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
            
            List<Group> userGroups = groupRepository.findByMembersContaining(user);
            List<Map<String, Object>> groupExpenses = new ArrayList<>();
            
            for (Group group : userGroups) {
                List<Expense> expenses = expenseRepository.findByGroup(group);
                if (!expenses.isEmpty()) {
                    Map<String, Object> groupData = new HashMap<>();
                    groupData.put("groupId", group.getId());
                    groupData.put("groupName", group.getName());
                    groupData.put("totalAmount", expenses.stream()
                        .map(Expense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                    groupData.put("lastExpense", expenses.stream()
                        .max(Comparator.comparing(Expense::getCreatedAt))
                        .orElse(null));
                    
                    groupExpenses.add(groupData);
                }
            }
            
            return ResponseEntity.ok(groupExpenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<String> deleteExpense(Long expenseId, Long userId) {
        try {
            Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Harcama bulunamadı!"));
            
            if (!expense.getUser().getId().equals(userId)) {
                return ResponseEntity.badRequest().body("Bu harcamayı silme yetkiniz yok!");
            }
            
            // Harcamayı sil
            expenseRepository.delete(expense);
            
            return ResponseEntity.ok("Harcama başarıyla silindi!");
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getGroupExpenseRanking(Long groupId, String startDateStr, String endDateStr, String period) {
        try {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grup bulunamadı!"));

            // Şu anki tarihi al
            LocalDateTime now = LocalDateTime.now();
            
            // Tarihleri parse et ve kontrol et
            LocalDateTime startDate;
            LocalDateTime endDate;
            try {
                startDate = LocalDateTime.parse(startDateStr);
                endDate = LocalDateTime.parse(endDateStr);
                
                // Gelecek tarihli istekleri engelle
                if (endDate.isAfter(now)) {
                    endDate = now;
                }
                if (startDate.isAfter(now)) {
                    startDate = now.minusMonths(1); // Varsayılan olarak son 1 ay
                }
                
                // Başlangıç tarihi bitiş tarihinden sonra ise düzelt
                if (startDate.isAfter(endDate)) {
                    startDate = endDate.minusMonths(1);
                }
            } catch (Exception e) {
                // Tarih parse edilemezse varsayılan tarih aralığını kullan
                endDate = now;
                startDate = period.equals("WEEKLY") ? now.minusWeeks(1) : now.minusMonths(1);
            }

            // Gruptaki tüm harcamaları al (visibleInList durumuna bakmaksızın)
            List<Expense> expenses = expenseRepository.findByGroupAndCreatedAtBetween(group, startDate, endDate);

            // Kullanıcı bazında harcamaları topla
            Map<User, BigDecimal> userExpenses = new HashMap<>();
            for (Expense expense : expenses) {
                userExpenses.merge(expense.getUser(), expense.getAmount(), BigDecimal::add);
            }

            // Sonuçları liste haline getir ve sırala (az harcayandan çok harcayana)
            List<Map<String, Object>> results = new ArrayList<>();
            userExpenses.entrySet().stream()
                .sorted(Map.Entry.<User, BigDecimal>comparingByValue())
                .forEach(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    User user = entry.getKey();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("firstName", user.getFirstName());
                    userMap.put("lastName", user.getLastName());
                    userMap.put("profileImage", user.getProfileImage());
                    
                    result.put("user", userMap);
                    result.put("totalExpense", entry.getValue());
                    result.put("rank", results.size() + 1);
                    results.add(result);
                });

            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace(); // Hata loglaması için
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    private void updateGroupRankings(Group group) {
        Set<User> members = group.getMembers();
        Map<User, Double> totalExpenses = new HashMap<>();
        
        // Her üye için toplam harcamayı hesapla
        for (User member : members) {
            List<Expense> expenses = expenseRepository.findByGroupAndUser(group, member);
            double total = expenses.stream()
                .map(expense -> expense.getAmount().doubleValue())
                .reduce(0.0, Double::sum);
            totalExpenses.put(member, total);
        }
        
        // Harcamalara göre sırala
        List<Map.Entry<User, Double>> sortedExpenses = new ArrayList<>(totalExpenses.entrySet());
        sortedExpenses.sort(Map.Entry.<User, Double>comparingByValue());
        
        // Sıralamaları güncelle veya oluştur
        for (int i = 0; i < sortedExpenses.size(); i++) {
            Map.Entry<User, Double> entry = sortedExpenses.get(i);
            User user = entry.getKey();
            Double totalExpense = entry.getValue();
            
            GroupRanking ranking = groupRankingRepository.findByGroupAndUser(group, user);
            if (ranking == null) {
                ranking = new GroupRanking();
                ranking.setGroup(group);
                ranking.setUser(user);
            }
            
            ranking.setRank(i + 1);
            ranking.setTotalExpense(totalExpense);
            ranking.setLastUpdated(LocalDateTime.now());
            
            groupRankingRepository.save(ranking);
        }
    }
} 