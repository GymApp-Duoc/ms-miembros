package com.gymapp.ms_miembros.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferencia para registrar o actualizar un miembro del gimnasio")
public class MiembroRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del miembro", example = "Carlos")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(description = "Apellido del miembro", example = "Ruiz")
    private String apellido;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar un formato de email válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    @Schema(description = "Correo electrónico único de contacto", example = "carlos.ruiz@correo.cl")
    private String email;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Schema(description = "Número de teléfono de contacto", example = "+56912345678")
    private String telefono;
}
