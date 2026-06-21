package com.gymapp.ms_miembros.controller;

import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.service.MiembroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
@Tag(name = "Miembros", description = "Operaciones relacionadas con la gestión de miembros del gimnasio")
public class MiembroController {

    private final MiembroService service;

    @Operation(summary = "Obtener todos los miembros activos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<MiembroResponseDTO>> obtenerTodos() {
        log.info("Petición REST: Obtener todos los miembros");
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar miembro por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro encontrado"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado o inactivo")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST: Obtener miembro ID {}", id);
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Validar si un miembro existe y está activo")
    @ApiResponse(responseCode = "200", description = "Retorna true si el miembro existe, false en caso contrario")
    @GetMapping("/validar/{id}")
    public ResponseEntity<Boolean> validarMiembro(@PathVariable Long id) {
        log.info("Petición REST: Validar existencia del miembro ID {}", id);
        try {
            MiembroResponseDTO miembro = service.obtenerPorId(id);
            return ResponseEntity.ok(miembro != null);
        } catch (Exception e) {
            log.warn("Validación fallida: El miembro ID {} no existe en la base de datos.", id);
            return ResponseEntity.ok(false);
        }
    }

    @Operation(summary = "Obtener el tipo de plan de un miembro")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna VIP o REGULAR según el ID"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @GetMapping("/{id}/plan")
    public ResponseEntity<String> obtenerPlan(@PathVariable Long id) {
        log.info("Petición REST: Obtener tipo de plan del miembro ID {}", id);
        try {
            MiembroResponseDTO miembro = service.obtenerPorId(id);
            if (miembro != null) {
                return ResponseEntity.ok(id == 1 ? "VIP" : "REGULAR");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.warn("No se pudo obtener el plan: El miembro ID {} no existe.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Crear un nuevo miembro")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Miembro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo duplicado")
    })
    @PostMapping
    public ResponseEntity<MiembroResponseDTO> crear(@Valid @RequestBody MiembroRequestDTO dto) {
        log.info("Petición REST: Crear nuevo miembro con email {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @Operation(summary = "Actualizar datos de un miembro")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Miembro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo duplicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MiembroRequestDTO dto) {
        log.info("Petición REST: Actualizar miembro ID {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Desactivar miembro (Borrado lógico)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Miembro desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Miembro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición REST: Desactivar miembro ID {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reporte 1: Total de miembros activos")
    @GetMapping("/reportes/activos/total")
    public ResponseEntity<Long> contarMiembrosActivos() {
        return ResponseEntity.ok(service.contarMiembrosActivos());
    }

    @Operation(summary = "Reporte 2: Listar todos los miembros inactivos (Bajas)")
    @GetMapping("/reportes/inactivos")
    public ResponseEntity<List<MiembroResponseDTO>> obtenerInactivos() {
        return ResponseEntity.ok(service.listarMiembrosInactivos());
    }

    @Operation(summary = "Reporte 3: Miembros registrados en los últimos X días")
    @GetMapping("/reportes/recientes")
    public ResponseEntity<List<MiembroResponseDTO>> obtenerRecientes(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.listarMiembrosRecientes(dias));
    }

    @Operation(summary = "Reporte 4: Miembros registrados en una fecha exacta")
    @GetMapping("/reportes/fecha/{fecha}")
    public ResponseEntity<List<MiembroResponseDTO>> obtenerPorFechaExacta(@PathVariable LocalDate fecha) {

        return ResponseEntity.ok(service.listarMiembrosPorFecha(fecha));
    }

    @Operation(summary = "Reporte 5: Filtrar miembros por dominio de correo corporativo")
    @GetMapping("/reportes/convenios/{dominio}")
    public ResponseEntity<List<MiembroResponseDTO>> obtenerPorDominio(@PathVariable String dominio) {

        return ResponseEntity.ok(service.listarMiembrosPorDominioEmail(dominio));
    }
}