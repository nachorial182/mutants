package com.example.mutants.repositories;

import com.example.mutants.models.MutantDna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MutantsRepository extends JpaRepository<MutantDna, Long> {

    Optional<MutantDna> findByDnaSequence(String dnaSequence);

    @Query("SELECT " +
            "COUNT(CASE WHEN m.isMutant = true THEN 1 END) AS mutantCount, " +
            "COUNT(CASE WHEN m.isMutant = false THEN 1 END) AS humanCount " +
            "FROM MutantDna m")
    DnaStatsProjection getDnaStats();

}
