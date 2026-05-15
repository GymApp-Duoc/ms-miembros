package com.gymapp.ms_miembros.service;

import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;

import java.util.List;

public interface MiembroService {

    List<MiembroResponseDTO> listarTodos();

    MiembroResponseDTO obtenerPorId(Long id);

    MiembroResponseDTO crear(MiembroRequestDTO dto);

    MiembroResponseDTO actualizar(Long id, MiembroRequestDTO dto);

    void eliminar(Long id);
}

