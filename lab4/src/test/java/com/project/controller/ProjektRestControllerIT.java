package com.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.model.Projekt;
import com.project.service.ProjektService;

@WebMvcTest(ProjektRestController.class)
@WithMockUser(username = "admin", password = "admin")
public class ProjektRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjektService projektService;

    @Test
    void getProjekt_whenValidId_returnsProjekt() throws Exception {
        Projekt projekt = Projekt.builder()
                .projektId(1)
                .nazwa("Aplikacja REST")
                .opis("Projekt testowy")
                .dataOddania(LocalDate.of(2026, 6, 15))
                .build();

        given(projektService.getProjekt(1)).willReturn(Optional.of(projekt));

        mockMvc.perform(get("/api/projekty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projektId").value(1))
                .andExpect(jsonPath("$.nazwa").value("Aplikacja REST"));
    }

    @Test
    void getProjekt_whenInvalidId_returnsNotFound() throws Exception {
        given(projektService.getProjekt(99)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/projekty/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProjekt_whenValidData_returnsCreated() throws Exception {
        Projekt projekt = Projekt.builder()
                .projektId(1)
                .nazwa("Aplikacja REST")
                .opis("Projekt testowy")
                .dataOddania(LocalDate.of(2026, 6, 15))
                .build();

        given(projektService.setProjekt(any(Projekt.class))).willReturn(projekt);

        String json = """
                {
                  "nazwa": "Aplikacja REST",
                  "opis": "Projekt testowy",
                  "dataOddania": "2026-06-15"
                }
                """;

        mockMvc.perform(post("/api/projekty")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/projekty/1"));
    }
}