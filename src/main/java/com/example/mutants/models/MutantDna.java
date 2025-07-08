package com.example.mutants.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mutant_dna")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutantDna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String dnaSequence;

    private boolean isMutant;
}