package com.example.mutants.controllers;

import com.example.mutants.dtos.DnaRequest;
import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.services.MutantsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
public class MutantsController {

    private final MutantsService service;

    public MutantsController(MutantsService service) {
        this.service = service;
    }

    @PostMapping("/mutant")
    public Mono<ResponseEntity<Void>> isMutant(@RequestBody @Valid Mono<DnaRequest> dnaRequest) {
        return dnaRequest
                .flatMap(dto -> service.isMutant(dto.getDna()))
                .map(isMutant -> {
                    if (isMutant) {
                        return ResponseEntity.ok().build();
                    } else {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                });
    }

    @GetMapping("/stats")
    public Mono<StatsResponse> stats() {
        return service.getStats();
    }
}

