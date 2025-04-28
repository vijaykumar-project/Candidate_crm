package com.vijay.crm.candidate_crm.controller;

import com.vijay.crm.candidate_crm.candidate.Candidate;
import com.vijay.crm.candidate_crm.service.CandidateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin("*")
public class CandidateController {

    private final CandidateService service;

    public CandidateController(CandidateService service) {
        this.service = service;
    }

    // Add new candidate
    @PostMapping("/add")
    public ResponseEntity<List<Candidate>> addCandidate(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String jobRole,
            @RequestParam String status,
            @RequestParam String interviewDate,
            @RequestParam String notes,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestParam(value = "resume", required = false) MultipartFile resume
    ) throws IOException {

        // ❗ Check if email or phone already exists
        if (service.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }
        if (service.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already exists!");
        }

        // Create a new Candidate object
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPhone(phone);
        candidate.setJobRole(jobRole);
        candidate.setStatus(status);
        candidate.setInterviewDate(LocalDate.parse(interviewDate));
        candidate.setNotes(notes);

        // Handle profile picture upload (if provided)
        if (profilePic != null && !profilePic.isEmpty()) {
            System.out.println("Profile picture received: " + profilePic.getOriginalFilename());
            candidate.setProfilePicData(profilePic.getBytes());
        } else {
            System.out.println("No profile picture uploaded.");
        }

        // Handle resume upload (if provided)
        if (resume != null && !resume.isEmpty()) {
            candidate.setResume_link(resume.getBytes());
        }

        // Save the candidate to the database (including image and resume)
        service.saveCandidate(candidate);

        // Fetch the updated list of candidates and return it
        List<Candidate> updatedCandidates = service.getAllCandidates();
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCandidates);
    }


    // Get all candidates (with optional status filter)
    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates(@RequestParam(required = false) String status) {
        List<Candidate> candidates = (status != null) ? service.getCandidatesByStatus(status) : service.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }

    // Get candidate by ID
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        Candidate candidate = service.getCandidateById(id);
        if (candidate != null) {
            return ResponseEntity.ok(candidate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update candidate details
    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id, @RequestBody Candidate updated) {
        Candidate existing = service.getCandidateById(id);
        if (existing != null) {
            updated.setId(id);
            Candidate saved = service.saveCandidate(updated);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete candidate
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        Candidate existing = service.getCandidateById(id);
        if (existing != null) {
            service.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Download resume
    @GetMapping("/{id}/resume")
    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
        Candidate candidate = service.getCandidateById(id);
        if (candidate == null || candidate.getResume_link() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "resume.pdf");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(candidate.getResume_link(), headers, HttpStatus.OK);
    }

    // Download profile picture
    @GetMapping("/{id}/profilePic")
    public ResponseEntity<byte[]> downloadProfilePic(@PathVariable Long id) {
        Candidate candidate = service.getCandidateById(id);
        if (candidate == null || candidate.getProfilePicData() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or MediaType.IMAGE_PNG based on your images
        return new ResponseEntity<>(candidate.getProfilePicData(), headers, HttpStatus.OK);
    }

    // Update only candidate status
    @PutMapping("/{id}/status")
    public ResponseEntity<Candidate> updateCandidateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        Candidate updated = service.updateCandidateStatus(id, newStatus);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Simple test login (Optional: Can remove if not needed)
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, Object> response = new HashMap<>();
        boolean success = "hr".equals(username) && "password123".equals(password);
        response.put("success", success);

        return ResponseEntity.ok(response);
    }
}
