package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Friendship;
import com.example.demo.model.User;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;
    
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> sendFriendRequest(Long senderId, Long receiverId) {
        try {
            User sender = userRepository.findById(senderId).orElse(null);
            User receiver = userRepository.findById(receiverId).orElse(null);
            
            if (sender == null || receiver == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }
            
            Friendship existingFriendship = friendshipRepository.findFriendship(sender, receiver);
            if (existingFriendship != null) {
                return ResponseEntity.badRequest().body("Arkadaşlık isteği zaten mevcut!");
            }
            
            Friendship friendship = new Friendship();
            friendship.setSender(sender);
            friendship.setReceiver(receiver);
            friendship.setStatus(Friendship.FriendshipStatus.PENDING);
            
            friendshipRepository.save(friendship);
            return ResponseEntity.ok("Arkadaşlık isteği gönderildi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> respondToFriendRequest(Long friendshipId, boolean accept) {
        try {
            Friendship friendship = friendshipRepository.findById(friendshipId).orElse(null);
            if (friendship == null) {
                return ResponseEntity.badRequest().body("İstek bulunamadı!");
            }
            
            friendship.setStatus(accept ? 
                Friendship.FriendshipStatus.ACCEPTED : 
                Friendship.FriendshipStatus.REJECTED);
            
            friendshipRepository.save(friendship);
            return ResponseEntity.ok(accept ? "İstek kabul edildi!" : "İstek reddedildi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getFriends(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }
            
            List<Friendship> friendships = friendshipRepository.findAllFriendships(user);
            List<Map<String, Object>> friends = friendships.stream().map(f -> {
                User friend = f.getSender().getId().equals(userId) ? f.getReceiver() : f.getSender();
                Map<String, Object> friendData = new HashMap<>();
                friendData.put("id", friend.getId());
                friendData.put("firstName", friend.getFirstName());
                friendData.put("lastName", friend.getLastName());
                friendData.put("profileImage", friend.getProfileImage());
                return friendData;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getPendingRequests(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }
            
            List<Friendship> requests = friendshipRepository.findPendingRequests(user);
            List<Map<String, Object>> pendingRequests = requests.stream().map(r -> {
                User sender = r.getSender();
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("id", r.getId());
                requestData.put("senderId", sender.getId());
                requestData.put("senderName", sender.getFirstName() + " " + sender.getLastName());
                requestData.put("senderImage", sender.getProfileImage());
                return requestData;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }

    public ResponseEntity<?> searchUsers(String query, Long currentUserId) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            String searchQuery = query.trim();
            List<User> users = userRepository.findByFirstNameLikeOrLastNameLike(searchQuery, searchQuery);
            
            List<Map<String, Object>> results = users.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .map(u -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", u.getId());
                    userData.put("firstName", u.getFirstName());
                    userData.put("lastName", u.getLastName());
                    userData.put("profileImage", u.getProfileImage());
                    
                    Friendship friendship = friendshipRepository.findFriendship(u, userRepository.findById(currentUserId).get());
                    String status = friendship == null ? "NONE" : friendship.getStatus().toString();
                    userData.put("friendshipStatus", status);
                    
                    return userData;
                }).collect(Collectors.toList());
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
} 