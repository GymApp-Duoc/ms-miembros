package com.gymapp.ms_miembros.service;

import com.gymapp.ms_miembros.assembler.MiembroAssembler;
import com.gymapp.ms_miembros.client.GamificacionClient;
import com.gymapp.ms_miembros.client.NotificacionClient;
import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.exception.BusinessException;
import com.gymapp.ms_miembros.model.Miembro;
import com.gymapp.ms_miembros.repository.MiembroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiembroServiceImplTest {

    @Mock
    private MiembroRepository repository;
    @Mock
    private GamificacionClient gamificacionClient;
    @Mock
    private NotificacionClient notificacionClient;
    @Mock
    private MiembroAssembler assembler;

    @InjectMocks
    private MiembroServiceImpl miembroService;

    @Test
    void crear_NuevoMiembro_RetornaResponseDTO() {
        // GIVEN
        MiembroRequestDTO request = new MiembroRequestDTO("Juan", "Perez", "juan@test.com", "123456");
        Miembro entity = new Miembro(null, "Juan", "Perez", "juan@test.com", "123456", LocalDate.now(), true);
        Miembro guardado = new Miembro(1L, "Juan", "Perez", "juan@test.com", "123456", LocalDate.now(), true);
        MiembroResponseDTO response = MiembroResponseDTO.builder().id(1L).email("juan@test.com").build();

        when(repository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(false);
        when(assembler.toEntity(request)).thenReturn(entity);
        when(repository.save(any(Miembro.class))).thenReturn(guardado);
        when(assembler.toResponseDTO(guardado)).thenReturn(response);


        MiembroResponseDTO resultado = miembroService.crear(request);


        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@test.com", resultado.getEmail());

        verify(gamificacionClient, times(1)).enviarEvento(anyMap());
        verify(notificacionClient, times(1)).enviarNotificacion(anyMap());
    }

    @Test
    void crear_EmailExistente_LanzaBusinessException() {

        MiembroRequestDTO request = new MiembroRequestDTO("Juan", "Perez", "duplicado@test.com", "123");
        when(repository.existsByEmailIgnoreCase(request.getEmail())).thenReturn(true);


        BusinessException exception = assertThrows(BusinessException.class, () -> miembroService.crear(request));
        assertEquals("El correo electrónico ya está registrado en el sistema.", exception.getMessage());
        verify(repository, never()).save(any());
    }
}