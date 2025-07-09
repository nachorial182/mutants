package com.example.mutants.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@AllArgsConstructor
public class DnaRequest {

    @NotEmpty(message = "DNA sequence is required")
    @Size(min = 4, message = "DNA must contain at least 4 rows")
    private List<
            @Pattern(regexp = "^[ATCG]+$", message = "Each DNA string must contain only the characters A, T, C, or G")
                    String> dna;
}
