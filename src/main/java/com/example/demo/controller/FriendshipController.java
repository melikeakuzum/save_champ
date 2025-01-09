package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FriendshipService;

@RestController
@RequestMapping("/api/friendship")
@CrossOrigin
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/send-request")
    public ResponseEntity<?> sendRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        return friendshipService.sendFriendRequest(senderId, receiverId);
    }

    @PostMapping("/respond")
    public ResponseEntity<?> respondToRequest(
            @RequestParam Long friendshipId,
            @RequestParam boolean accept) {
        return friendshipService.respondToFriendRequest(friendshipId, accept);
    }

    @GetMapping("/friends/{userId}")
    public ResponseEntity<?> getFriends(@PathVariable Long userId) {
        return friendshipService.getFriends(userId);
    }

    @GetMapping("/requests/{userId}")
    public ResponseEntity<?> getPendingRequests(@PathVariable Long userId) {
        return friendshipService.getPendingRequests(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String query,
            @RequestParam Long currentUserId) {
        return friendshipService.searchUsers(query, currentUserId);
    }


    @DeleteMapping("/remove/{userId}/{friendId}")
    public ResponseEntity<String> removeFriendship(
            @PathVariable Long userId,
            @PathVariable Long friendId) {
        return friendshipService.removeFriendship(userId, friendId);
    }
} 