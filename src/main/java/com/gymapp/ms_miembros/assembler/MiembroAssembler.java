package com.gymapp.ms_miembros.assembler;

import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.model.Miembro;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MiembroAssembler {

    public MiembroResponseDTO toResponseDTO(Miembro miembro) {
        if (miembro == null) return null;

        return MiembroResponseDTO.builder()
                .id(miembro.getId())
                .nombre(miembro.getNombre())
                .apellido(miembro.getApellido())
                .email(miembro.getEmail())
                .telefono(miembro.getTelefono())
                .fechaRegistro(miembro.getFechaRegistro())
                .activo(miembro.isActivo())
                .build();
    }

    public Miembro toEntity(MiembroRequestDTO dto) {
        if (dto == null) return null;

        Miembro miembro = new Miembro();
        miembro.setNombre(dto.getNombre());
        miembro.setApellido(dto.getApellido());
        miembro.setEmail(dto.getEmail());
        miembro.setTelefono(dto.getTelefono());
        miembro.setFechaRegistro(LocalDate.now());
        miembro.setActivo(true);
        return miembro;
    }
}