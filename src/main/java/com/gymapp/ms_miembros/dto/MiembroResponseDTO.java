package com.gymapp.ms_miembros.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta con la información consolidada del miembro")
public class MiembroResponseDTO {

    @Schema(description = "Identificador único del miembro", example = "1")
    private Long id;

    @Schema(description = "Nombre del miembro", example = "Carlos")
    private String nombre;

    @Schema(description = "Apellido del miembro", example = "Ruiz")
    private String apellido;

    @Schema(description = "Correo electrónico", example = "carlos.ruiz@correo.cl")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @Schema(description = "Fecha en la que el miembro fue registrado en el sistema", example = "2026-06-21")
    private LocalDate fechaRegistro;

    @Schema(description = "Estado lógico del miembro en la base de datos (true = activo, false = inactivo)", example = "true")
    private boolean activo;
}