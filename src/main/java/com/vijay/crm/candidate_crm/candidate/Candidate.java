package com.vijay.crm.candidate_crm.candidate;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String jobRole;

    @Lob  
    @Basic(fetch = FetchType.LAZY)
    private byte[] resume_link;  // Resume as a binary blob

    @Lob  
    @Basic(fetch = FetchType.LAZY)
    private byte[] profilePicData; // Profile Picture as a binary blob

    private String status;
    private LocalDate interviewDate;
    private String notes;

    // ✅ Add this
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CandidateLog> candidateLogs;

}
