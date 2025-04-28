package com.vijay.crm.candidate_crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vijay.crm.candidate_crm.candidate.CandidateLog;

public interface CandidateLogRepository extends JpaRepository<CandidateLog, Long> {
    List<CandidateLog> findByCandidateIdOrderByTimestampDesc(Long candidateId);
}
