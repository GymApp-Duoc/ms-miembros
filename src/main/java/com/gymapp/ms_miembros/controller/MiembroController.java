package com.gymapp.ms_miembros.controller;

import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.service.MiembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
public class MiembroController {

    private final MiembroService service;

    @GetMapping
    public ResponseEntity<List<MiembroResponseDTO>> obtenerTodos() {
        log.info("Petición REST: Obtener todos los miembros");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST: Obtener miembro ID {}", id);
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

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

    @PostMapping
    public ResponseEntity<MiembroResponseDTO> crear(@Valid @RequestBody MiembroRequestDTO dto) {
        log.info("Petición REST: Crear nuevo miembro con email {}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MiembroResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MiembroRequestDTO dto) {
        log.info("Petición REST: Actualizar miembro ID {}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición REST: Desactivar miembro ID {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}