package com.example.mutants.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@AllArgsConstructor
public class DnaRequest {

    @NotEmpty(message = "DNA sequence is required")
    @Size(min = 4, message = "DNA must contain at least 4 rows")
    private List<String> dna;
}

