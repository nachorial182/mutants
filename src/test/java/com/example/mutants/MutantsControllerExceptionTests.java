package com.example.mutants;

import com.example.mutants.controllers.MutantsController;
import com.example.mutants.dtos.DnaRequest;
import com.example.mutants.exceptions.GlobalExceptionHandler;
import com.example.mutants.services.MutantsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = MutantsController.class)
@Import(GlobalExceptionHandler.class)
public class MutantsControllerExceptionTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MutantsService mutantsService;

    @Test
    void whenPayloadInvalid_thenBadRequestWithErrorResponse() {
        webClient.post().uri("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"dna\":[]}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").exists()
                .jsonPath("$.path").isEqualTo("/mutant");
    }

    @Test
    void whenServiceThrows_thenInternalServerErrorWithErrorResponse() {
        given(mutantsService.isMutant(any(List.class)))
                .willReturn(Mono.error(new IllegalStateException("failure")));

        webClient.post().uri("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DnaRequest(List.of("ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG")))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.error").isEqualTo("Internal Server Error")
                .jsonPath("$.message").isEqualTo("failure")
                .jsonPath("$.path").isEqualTo("/mutant");
    }
}
