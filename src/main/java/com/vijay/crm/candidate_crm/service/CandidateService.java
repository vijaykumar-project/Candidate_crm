package com.vijay.crm.candidate_crm.service;

import com.vijay.crm.candidate_crm.candidate.*;
import java.util.List;

public interface CandidateService {
	
    Candidate saveCandidate(Candidate candidate);
    List<Candidate> getAllCandidates();
    Candidate getCandidateById(Long id);
    void deleteCandidate(Long id);
    List<Candidate> getCandidatesByStatus(String status);
    Candidate updateCandidateStatus(Long id, String newStatus);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
}
