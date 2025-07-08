package com.example.mutants.services;

import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.models.MutantDna;
import com.example.mutants.repositories.DnaStatsProjection;
import com.example.mutants.repositories.MutantsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class MutantsService {

    private static final Logger logger = LoggerFactory.getLogger(MutantsService.class);
    private final MutantsRepository mutantsRepository;

    public MutantsService(MutantsRepository mutantsRepository) {
        this.mutantsRepository = mutantsRepository;
    }

    public Mono<Boolean> isMutant(List<String> dna) {
        return Mono.fromCallable(() -> {
                    boolean result = detectMutant(dna);
                    MutantDna record = new MutantDna();
                    record.setDnaSequence(String.join(",", dna));
                    record.setMutant(result);
                    mutantsRepository.save(record);
                    return result;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(ex -> logger.error("Failed to record DNA check", ex))
                .onErrorReturn(false);
    }

    private boolean detectMutant(List<String> dna) {
        char[][] matrix = toMatrix(dna);
        int sequenceCount = 0;
        int size = matrix.length;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char base = matrix[i][j];
                if (j + 3 < size && base == matrix[i][j+1] && base == matrix[i][j+2] && base == matrix[i][j+3]) sequenceCount++;
                if (i + 3 < size && base == matrix[i+1][j] && base == matrix[i+2][j] && base == matrix[i+3][j]) sequenceCount++;
                if (i + 3 < size && j + 3 < size && base == matrix[i+1][j+1] && base == matrix[i+2][j+2] && base == matrix[i+3][j+3]) sequenceCount++;
                if (i + 3 < size && j - 3 >= 0 && base == matrix[i+1][j-1] && base == matrix[i+2][j-2] && base == matrix[i+3][j-3]) sequenceCount++;
                if (sequenceCount > 1) return true;
            }
        }

        return false;
    }

    private char[][] toMatrix(List<String> dna) {
        int n = dna.size();
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna.get(i).toCharArray();
        }
        return matrix;
    }

    public Mono<StatsResponse> getStats() {
        return Mono.fromSupplier(this::getStatFromDb)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public StatsResponse getStatFromDb() {
        logger.info("Fetching DNA stats from repository");
        DnaStatsProjection stats = mutantsRepository.getDnaStats();

        long mutants = stats.getMutantCount() != null ? stats.getMutantCount() : 0;
        long humans = stats.getHumanCount() != null ? stats.getHumanCount() : 0;
        double ratio = humans == 0 ? 0 : (double) mutants / humans;

        logger.info("Stats calculated - Mutants: {}, Humans: {}, Ratio: {}", mutants, humans, ratio);
        return new StatsResponse(mutants, humans, ratio);
    }

}
