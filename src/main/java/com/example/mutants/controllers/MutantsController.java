package com.example.mutants.controllers;

import com.example.mutants.dtos.DnaRequest;
import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.services.MutantsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MutantsController {

    private final MutantsService mutantsService;

    public MutantsController(MutantsService mutantsService) {
        this.mutantsService = mutantsService;
    }

    @PostMapping("/mutant")
    public ResponseEntity<Void> isMutant(@RequestBody @Valid DnaRequest dnaRequest) {
        boolean result = mutantsService.isMutant(dnaRequest.getDna());
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(mutantsService.getStats());
    }

}

