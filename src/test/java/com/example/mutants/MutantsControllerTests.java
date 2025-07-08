package com.example.mutants;

import com.example.mutants.controllers.MutantsController;
import com.example.mutants.dtos.DnaRequest;
import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.services.MutantsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest(MutantsController.class)
@AutoConfigureWebTestClient
public class MutantsControllerTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MutantsService mutantService;

    @Test
    public void shouldReturn200ForMutant() {
        given(mutantService.isMutant(any(List.class)))
                .willReturn(Mono.just(true));

        webClient.post().uri("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DnaRequest(List.of("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG")))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void shouldReturn403ForHuman() {
        given(mutantService.isMutant(any(List.class)))
                .willReturn(Mono.just(false));

        webClient.post().uri("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DnaRequest(List.of("ATGCGA","CAGTGC","TTATGT","AGAAGT","CACCTA","TCACTG")))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void shouldReturnStats() {
        StatsResponse stats = new StatsResponse(40, 100, 0.4);
        given(mutantService.getStats())
                .willReturn(Mono.just(stats));

        webClient.get().uri("/stats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.count_mutant_dna").isEqualTo(40)
                .jsonPath("$.count_human_dna").isEqualTo(100)
                .jsonPath("$.ratio").isEqualTo(0.4);
    }
}