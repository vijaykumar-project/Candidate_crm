package com.vijay.crm.candidate_crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vijay.crm.candidate_crm.candidate.Candidate;
import com.vijay.crm.candidate_crm.candidate.CandidateLog;
import com.vijay.crm.candidate_crm.repository.CandidateLogRepository;
import com.vijay.crm.candidate_crm.repository.CandidateRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidate-log")
@RequiredArgsConstructor
public class CandidateLogController {

    private final CandidateLogRepository logRepo;
    private final CandidateRepository candidateRepo;

    @PostMapping
    public ResponseEntity<?> addLog(@RequestBody CandidateLog log) {
        if (log.getCandidate() == null || log.getCandidate().getId() == null) {
            return ResponseEntity.badRequest().body("Candidate ID is missing");
        }

        // Fetch full candidate entity
        Candidate candidate = candidateRepo.findById(log.getCandidate().getId())
                              .orElseThrow(() -> new RuntimeException("Candidate not found"));

        log.setCandidate(candidate);
        CandidateLog savedLog = logRepo.save(log);
        return ResponseEntity.ok(savedLog);
    }

    @GetMapping("/{candidateId}")
    public List<CandidateLog> getLogs(@PathVariable Long candidateId) {
        return logRepo.findByCandidateIdOrderByTimestampDesc(candidateId);
    }
}

