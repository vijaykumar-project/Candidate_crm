package com.vijay.crm.candidate_crm.repository;

import com.vijay.crm.candidate_crm.candidate.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByStatus(String status);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

}
