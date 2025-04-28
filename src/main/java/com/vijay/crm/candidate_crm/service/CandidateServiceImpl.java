package com.vijay.crm.candidate_crm.service;

import com.vijay.crm.candidate_crm.candidate.Candidate;
import com.vijay.crm.candidate_crm.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateServiceImpl implements CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }

    @Override
    public List<Candidate> getCandidatesByStatus(String status) {
        return candidateRepository.findByStatus(status);
    }
    @Override
    public Candidate updateCandidateStatus(Long id, String newStatus) {
        return candidateRepository.findById(id).map(candidate -> {
            candidate.setStatus(newStatus);
            return candidateRepository.save(candidate);
        }).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return candidateRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return candidateRepository.existsByPhone(phone);
    }



   

}
