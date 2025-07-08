package com.example.mutants;

import com.example.mutants.controllers.MutantsController;
import com.example.mutants.dtos.StatsResponse;
import com.example.mutants.services.MutantsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantsController.class)
public class MutantsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantsService mutantService;

    @Test
    public void shouldReturn200ForMutant() throws Exception {
        when(mutantService.isMutant(any())).thenReturn(true);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dna\": [\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn403ForHuman() throws Exception {
        when(mutantService.isMutant(any())).thenReturn(false);
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dna\": [\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGT\",\"CACCTA\",\"TCACTG\"]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnStats() throws Exception {
        StatsResponse stats = new StatsResponse(40, 100, 0.4);
        when(mutantService.getStats()).thenReturn(stats);
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }
}