package com.example.demo.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Group;
import com.example.demo.model.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.UserRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    public ResponseEntity<?> createGroup(String name, String description, Long creatorId) {
        try {
            User creator = userRepository.findById(creatorId).orElse(null);
            if (creator == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setCreator(creator);
            group.setMembers(new HashSet<>());
            group.getMembers().add(creator);

            groupRepository.save(group);
            return ResponseEntity.ok("Grup başarıyla oluşturuldu!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Grup oluşturulurken hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getUserGroups(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            List<Group> createdGroups = groupRepository.findByCreator(user);
            List<Group> memberGroups = groupRepository.findByMembersContaining(user);

            List<Map<String, Object>> groups = memberGroups.stream().map(g -> {
                Map<String, Object> groupData = new HashMap<>();
                groupData.put("id", g.getId());
                groupData.put("name", g.getName());
                groupData.put("description", g.getDescription());
                groupData.put("memberCount", g.getMembers().size());
                groupData.put("isCreator", g.getCreator().getId().equals(userId));
                return groupData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gruplar listelenirken hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> addMemberToGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            
            if (group == null || user == null) {
                return ResponseEntity.badRequest().body("Grup veya kullanıcı bulunamadı!");
            }
            
            if (group.getMembers().contains(user)) {
                return ResponseEntity.badRequest().body("Kullanıcı zaten grupta!");
            }
            
            group.getMembers().add(user);
            groupRepository.save(group);
            
            return ResponseEntity.ok("Kullanıcı gruba eklendi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> addAdminToGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            
            if (group == null || user == null) {
                return ResponseEntity.badRequest().body("Grup veya kullanıcı bulunamadı!");
            }
            
            if (!group.getMembers().contains(user)) {
                return ResponseEntity.badRequest().body("Kullanıcı önce gruba üye olmalı!");
            }
            
            if (group.getAdmins().contains(user)) {
                return ResponseEntity.badRequest().body("Kullanıcı zaten yönetici!");
            }
            
            group.getAdmins().add(user);
            groupRepository.save(group);
            
            return ResponseEntity.ok("Kullanıcı yönetici yapıldı!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getGroupMembers(Long groupId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            List<Map<String, Object>> members = group.getMembers().stream().map(m -> {
                Map<String, Object> memberData = new HashMap<>();
                memberData.put("id", m.getId());
                memberData.put("firstName", m.getFirstName());
                memberData.put("lastName", m.getLastName());
                memberData.put("profileImage", m.getProfileImage());
                memberData.put("isAdmin", group.getAdmins().contains(m));
                memberData.put("isCreator", group.getCreator().getId().equals(m.getId()));
                return memberData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> deleteGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            // Sadece grup kurucusu grubu silebilir
            if (!group.getCreator().getId().equals(userId)) {
                return ResponseEntity.badRequest().body("Bu işlem için yetkiniz yok!");
            }

            groupRepository.delete(group);
            return ResponseEntity.ok("Grup başarıyla silindi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> removeMemberFromGroup(Long groupId, Long userId, Long removedUserId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            // İşlemi yapan kişi admin veya kurucu olmalı
            if (!group.getCreator().getId().equals(userId) && !group.getAdmins().stream().anyMatch(a -> a.getId().equals(userId))) {
                return ResponseEntity.badRequest().body("Bu işlem için yetkiniz yok!");
            }

            // Kurucu üyelikten çıkarılamaz
            if (group.getCreator().getId().equals(removedUserId)) {
                return ResponseEntity.badRequest().body("Grup kurucusu üyelikten çıkarılamaz!");
            }

            User removedUser = userRepository.findById(removedUserId).orElse(null);
            if (removedUser == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            group.getMembers().remove(removedUser);
            group.getAdmins().remove(removedUser); // Eğer adminse admin rolünü de kaldır
            groupRepository.save(group);

            return ResponseEntity.ok("Üye gruptan çıkarıldı!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> removeAdminFromGroup(Long groupId, Long userId, Long removedAdminId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group == null) {
                return ResponseEntity.badRequest().body("Grup bulunamadı!");
            }

            // Sadece kurucu admin rolünü kaldırabilir
            if (!group.getCreator().getId().equals(userId)) {
                return ResponseEntity.badRequest().body("Bu işlem için yetkiniz yok!");
            }

            // Kurucu admin rolünden çıkarılamaz
            if (group.getCreator().getId().equals(removedAdminId)) {
                return ResponseEntity.badRequest().body("Grup kurucusunun yönetici rolü kaldırılamaz!");
            }

            User removedAdmin = userRepository.findById(removedAdminId).orElse(null);
            if (removedAdmin == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            group.getAdmins().remove(removedAdmin);
            groupRepository.save(group);

            return ResponseEntity.ok("Yönetici rolü kaldırıldı!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getFriendsNotInGroup(Long groupId, Long userId) {
        try {
            Group group = groupRepository.findById(groupId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);
            
            if (group == null || user == null) {
                return ResponseEntity.badRequest().body("Grup veya kullanıcı bulunamadı!");
            }

            // Kullanıcının arkadaşlarını al ve grupta olmayanları filtrele
            List<User> friends = friendshipRepository.findAllFriendships(user).stream()
                .map(f -> f.getSender().getId().equals(userId) ? f.getReceiver() : f.getSender())
                .filter(friend -> !group.getMembers().contains(friend))
                .collect(Collectors.toList());

            List<Map<String, Object>> result = friends.stream().map(f -> {
                Map<String, Object> friendData = new HashMap<>();
                friendData.put("id", f.getId());
                friendData.put("firstName", f.getFirstName());
                friendData.put("lastName", f.getLastName());
                friendData.put("profileImage", f.getProfileImage());
                return friendData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
} 