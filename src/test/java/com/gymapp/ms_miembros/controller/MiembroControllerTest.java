package com.gymapp.ms_miembros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.service.MiembroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MiembroControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MiembroService miembroService;

    @InjectMocks
    private MiembroController miembroController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(miembroController).build();


        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void obtenerTodos_RetornaListaConEstatus200() throws Exception {

        MiembroResponseDTO miembro = MiembroResponseDTO.builder()
                .id(1L)
                .nombre("Ana")
                .apellido("Gomez")
                .email("ana@test.com")
                .activo(true)
                .build();

        when(miembroService.listarTodos()).thenReturn(List.of(miembro));


        mockMvc.perform(get("/api/miembros")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
    }

    @Test
    void obtenerPorId_MiembroExistente_Retorna200() throws Exception {
        // GIVEN
        MiembroResponseDTO miembro = MiembroResponseDTO.builder()
                .id(1L)
                .nombre("Ana")
                .build();

        when(miembroService.obtenerPorId(1L)).thenReturn(miembro);

        // WHEN & THEN
        mockMvc.perform(get("/api/miembros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void crear_MiembroValido_Retorna201YMiembro() throws Exception {
        // GIVEN
        MiembroRequestDTO request = new MiembroRequestDTO("Carlos", "Ruiz", "carlos@test.com", "987654321");
        MiembroResponseDTO response = MiembroResponseDTO.builder()
                .id(2L)
                .nombre("Carlos")
                .apellido("Ruiz")
                .email("carlos@test.com")
                .activo(true)
                .fechaRegistro(LocalDate.now())
                .build();

        when(miembroService.crear(any(MiembroRequestDTO.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/miembros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("carlos@test.com"));
    }

    @Test
    void crear_FaltaNombre_Retorna400BadRequest() throws Exception {

        MiembroRequestDTO request = new MiembroRequestDTO("", "Ruiz", "carlos@test.com", "987654321");


        mockMvc.perform(post("/api/miembros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_MiembroValido_Retorna200() throws Exception {

        MiembroRequestDTO request = new MiembroRequestDTO("Carlos Modificado", "Ruiz", "carlos@test.com", "987654321");

        MiembroResponseDTO response = MiembroResponseDTO.builder()
                .id(2L)
                .nombre("Carlos Modificado")
                .build();

        when(miembroService.actualizar(eq(2L), any(MiembroRequestDTO.class))).thenReturn(response);


        mockMvc.perform(put("/api/miembros/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Modificado"));
    }

    @Test
    void eliminar_IdExistente_Retorna204NoContent() throws Exception {

        doNothing().when(miembroService).eliminar(1L);


        mockMvc.perform(delete("/api/miembros/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void validarMiembro_Existente_RetornaTrue() throws Exception {

        MiembroResponseDTO miembro = MiembroResponseDTO.builder().id(1L).build();
        when(miembroService.obtenerPorId(1L)).thenReturn(miembro);


        mockMvc.perform(get("/api/miembros/validar/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void validarMiembro_NoExistente_RetornaFalse() throws Exception {

        when(miembroService.obtenerPorId(99L)).thenThrow(new RuntimeException("Not found"));


        mockMvc.perform(get("/api/miembros/validar/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void obtenerPlan_Id1_RetornaVIP() throws Exception {

        MiembroResponseDTO miembro = MiembroResponseDTO.builder().id(1L).build();
        when(miembroService.obtenerPorId(1L)).thenReturn(miembro);


        mockMvc.perform(get("/api/miembros/1/plan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("VIP"));
    }

    @Test
    void obtenerPlan_OtroId_RetornaREGULAR() throws Exception {

        MiembroResponseDTO miembro = MiembroResponseDTO.builder().id(2L).build();
        when(miembroService.obtenerPorId(2L)).thenReturn(miembro);


        mockMvc.perform(get("/api/miembros/2/plan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("REGULAR"));
    }
}