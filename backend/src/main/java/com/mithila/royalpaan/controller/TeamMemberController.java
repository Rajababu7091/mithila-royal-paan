package com.mithila.royalpaan.controller;

import com.mithila.royalpaan.entity.TeamMember;
import com.mithila.royalpaan.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/team-members")
public class TeamMemberController {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @GetMapping
    public ResponseEntity<List<TeamMember>> getAllTeamMembers() {
        return ResponseEntity.ok(teamMemberRepository.findAllByOrderByDisplayOrderAsc());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<TeamMember> createTeamMember(@RequestBody TeamMember member) {
        return ResponseEntity.ok(teamMemberRepository.save(member));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<TeamMember> updateTeamMember(@PathVariable Integer id, @RequestBody TeamMember memberDetails) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member not found with id: " + id));
        member.setName(memberDetails.getName());
        member.setRole(memberDetails.getRole());
        member.setBio(memberDetails.getBio());
        member.setPhotoUrl(memberDetails.getPhotoUrl());
        member.setDisplayOrder(memberDetails.getDisplayOrder());
        return ResponseEntity.ok(teamMemberRepository.save(member));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STAFF')")
    public ResponseEntity<?> deleteTeamMember(@PathVariable Integer id) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member not found with id: " + id));
        teamMemberRepository.delete(member);
        return ResponseEntity.ok(Map.of("message", "Team member deleted successfully"));
    }
}
