package com.example.mutants;

import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.models.MutantDna;
import com.example.mutants.repositories.DnaStatsProjection;
import com.example.mutants.repositories.MutantsRepository;
import com.example.mutants.services.MutantsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MutantsServiceTests {

    @Mock
    private MutantsRepository mutantsRepository;

    @InjectMocks
    private MutantsService mutantsService;

    @Test
    void shouldDetectMutantDna() {
        List<String> dna = List.of("ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG");
        String dnaString = String.join(",", dna);
        when(mutantsRepository.findByDnaSequence(dnaString))
                .thenReturn(Optional.empty());

        boolean result = mutantsService.isMutant(dna);

        assertTrue(result);
        verify(mutantsRepository, times(1)).save(any());
    }


    @Test
    void shouldDetectHumanDna() {
        List<String> dna = List.of("ATGCGA", "CAGTGC", "TTATGT", "AGACGG", "GCGTCA", "TCACTG");
        String dnaString = String.join(",", dna);

        when(mutantsRepository.findByDnaSequence(dnaString))
                .thenReturn(Optional.empty());

        boolean result = mutantsService.isMutant(dna);

        assertFalse(result);
        verify(mutantsRepository, times(1)).save(any());
    }

    @Test
    public void shouldReturnCachedResultIfExists() {
        List<String> dna = List.of("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG");
        String dnaString = String.join(",", dna);

        MutantDna saved = new MutantDna(1L, dnaString, true);
        when(mutantsRepository.findByDnaSequence(dnaString)).thenReturn(Optional.of(saved));

        boolean result = mutantsService.isMutant(dna);

        assertTrue(result);
        verify(mutantsRepository, never()).save(any());
    }

    @Test
    public void shouldReturnStats() {
        DnaStatsProjection stats = mock(DnaStatsProjection.class);
        when(stats.getMutantCount()).thenReturn(40L);
        when(stats.getHumanCount()).thenReturn(100L);
        when(mutantsRepository.getDnaStats()).thenReturn(stats);

        StatsResponse response = mutantsService.getStats();

        assertEquals(40L, response.getCountMutantDna());
        assertEquals(100L, response.getCountHumanDna());
        assertEquals(0.4, response.getRatio(), 0.01);
    }
}
