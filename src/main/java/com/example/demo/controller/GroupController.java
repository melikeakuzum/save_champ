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

import com.example.demo.model.Group;
import com.example.demo.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long creatorId) {
        return groupService.createGroup(name, description, creatorId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable Long userId) {
        return groupService.getUserGroups(userId);
    }

    @PostMapping("/{groupId}/members/add")
    public ResponseEntity<?> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return groupService.addMemberToGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/admins/add")
    public ResponseEntity<?> addAdmin(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return groupService.addAdminToGroup(groupId, userId);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getMembers(@PathVariable Long groupId) {
        return groupService.getGroupMembers(groupId);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return groupService.deleteGroup(groupId, userId);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam Long userId) {
        return groupService.removeMemberFromGroup(groupId, userId, memberId);
    }

    @DeleteMapping("/{groupId}/admins/{adminId}")
    public ResponseEntity<?> removeAdmin(
            @PathVariable Long groupId,
            @PathVariable Long adminId,
            @RequestParam Long userId) {
        return groupService.removeAdminFromGroup(groupId, userId, adminId);
    }

    @GetMapping("/{groupId}/available-friends")
    public ResponseEntity<?> getAvailableFriends(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return groupService.getFriendsNotInGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {
        return groupService.leaveGroup(groupId, userId);
    }

    @GetMapping("/{groupId}/details")
    public ResponseEntity<Group> getGroupDetails(@PathVariable Long groupId) {
        try {
            Group group = groupService.findGroupById(groupId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable Long groupId) {
        try {
            Group group = groupService.findGroupById(groupId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
} 