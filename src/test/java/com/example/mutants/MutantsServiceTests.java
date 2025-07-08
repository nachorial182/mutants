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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MutantsServiceTests {

    @Mock
    private MutantsRepository mutantsRepository;

    @InjectMocks
    private MutantsService mutantsService;

    @Test
    void shouldDetectMutantDna() {
        List<String> dna = List.of("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG");

        Mono<Boolean> resultMono = mutantsService.isMutant(dna);
        Boolean isMutant = resultMono.block();

        assertNotNull(isMutant, "Mono completed without a value");
        assertTrue(isMutant, "Expected DNA to be detected as mutant");
    }

    @Test
    void shouldDetectHumanDna() {
        List<String> dna = List.of("ATGCGA","CAGTGC","TTATGT","AGACGG","GCGTCA","TCACTG");

        Mono<Boolean> resultMono = mutantsService.isMutant(dna);
        Boolean isMutant = resultMono.block();

        assertNotNull(isMutant, "Mono completed without emitting a value");
        assertFalse(isMutant, "Expected DNA to be detected as human");
    }

    @Test
    void shouldReturnCachedResultIfExists() {
        List<String> dna = List.of("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG");
        String seq = String.join(",", dna);

        MutantDna cached = new MutantDna(1L, seq, true);
        when(mutantsRepository.findByDnaSequence(seq))
                .thenReturn(Optional.of(cached));

        Mono<Boolean> resultMono = mutantsService.isMutant(dna);
        Boolean isMutant = resultMono.block();

        assertNotNull(isMutant, "Mono completed without emitting a value");
        assertTrue(isMutant, "Expected cached DNA result to be true");
    }

    @Test
    void shouldReturnStats() {
        DnaStatsProjection proj = mock(DnaStatsProjection.class);
        when(proj.getMutantCount()).thenReturn(40L);
        when(proj.getHumanCount()).thenReturn(100L);
        when(mutantsRepository.getDnaStats()).thenReturn(proj);

        Mono<StatsResponse> statsMono = mutantsService.getStats();
        StatsResponse stats = statsMono.block();

        assertNotNull(stats, "Mono<StatsResponse> completed without a value");
        assertEquals(40L, stats.getCountMutantDna());
        assertEquals(100L, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 1e-6);
    }
}
