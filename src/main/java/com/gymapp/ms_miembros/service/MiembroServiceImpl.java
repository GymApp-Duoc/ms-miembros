package com.gymapp.ms_miembros.service;

import com.gymapp.ms_miembros.assembler.MiembroAssembler;
import com.gymapp.ms_miembros.client.GamificacionClient;
import com.gymapp.ms_miembros.client.NotificacionClient;
import com.gymapp.ms_miembros.dto.MiembroRequestDTO;
import com.gymapp.ms_miembros.dto.MiembroResponseDTO;
import com.gymapp.ms_miembros.exception.BusinessException;
import com.gymapp.ms_miembros.exception.RecursoNoEncontradoException;
import com.gymapp.ms_miembros.model.Miembro;
import com.gymapp.ms_miembros.repository.MiembroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MiembroServiceImpl implements MiembroService {

    private final MiembroRepository repository;
    private final GamificacionClient gamificacionClient;
    private final NotificacionClient notificacionClient;
    private final MiembroAssembler assembler; // Inyectamos el nuevo Assembler

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarTodos() {
        log.info("Consultando la lista de miembros ACTIVOS");
        return repository.findByActivoTrue().stream()
                .map(assembler::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MiembroResponseDTO obtenerPorId(Long id) {
        log.info("Buscando miembro activo por ID: {}", id);
        Miembro miembro = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o inactivo con ID: " + id));
        return assembler.toResponseDTO(miembro);
    }

    @Override
    @Transactional
    public MiembroResponseDTO crear(MiembroRequestDTO dto) {
        log.info("Iniciando registro de nuevo miembro: {}", dto.getEmail());

        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado en el sistema.");
        }

        Miembro miembro = assembler.toEntity(dto);
        Miembro guardado = repository.save(miembro);

        log.info("Miembro creado exitosamente con ID: {}", guardado.getId());
        enviarRecompensasYBienvenida(guardado);

        return assembler.toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public MiembroResponseDTO actualizar(Long id, MiembroRequestDTO dto) {
        log.info("Iniciando actualización para el miembro ID: {}", id);

        Miembro existente = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o inactivo."));

        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail()) && repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new BusinessException("El nuevo correo electrónico ya está en uso.");
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());

        return assembler.toResponseDTO(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Aplicando borrado lógico al miembro ID: {}", id);
        Miembro miembro = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o ya inactivo."));

        miembro.setActivo(false);
        repository.save(miembro);
    }



    @Override
    @Transactional(readOnly = true)
    public long contarMiembrosActivos() {
        log.info("Generando reporte: Conteo total de miembros activos");
        return repository.countByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembrosInactivos() {
        log.info("Generando reporte: Lista de miembros inactivos (Bajas)");
        return repository.findByActivoFalse().stream()
                .map(assembler::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembrosRecientes(int dias) {
        log.info("Generando reporte: Miembros registrados en los últimos {} días", dias);
        LocalDate fechaCorte = LocalDate.now().minusDays(dias);
        return repository.findByFechaRegistroAfter(fechaCorte).stream()
                .map(assembler::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembrosPorFecha(LocalDate fecha) {
        log.info("Generando reporte: Miembros registrados en la fecha {}", fecha);
        return repository.findByFechaRegistro(fecha).stream()
                .map(assembler::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarMiembrosPorDominioEmail(String dominio) {
        log.info("Generando reporte: Miembros con dominio de correo {}", dominio);
        return repository.findByEmailContainingIgnoreCase(dominio).stream()
                .map(assembler::toResponseDTO)
                .collect(Collectors.toList());
    }



    private void enviarRecompensasYBienvenida(Miembro miembro) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("miembroId", miembro.getId());
            evento.put("accion", "REGISTRO_NUEVO");
            evento.put("puntosBase", 50);
            gamificacionClient.enviarEvento(evento);
        } catch (Exception e) {
            log.warn("Fallo de comunicación con Gamificación: {}", e.getMessage());
        }

        try {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("miembroId", miembro.getId());
            notificacion.put("titulo", "¡Bienvenido a GymApp!");
            notificacion.put("mensaje", "Hola " + miembro.getNombre() + ", tu cuenta ha sido creada.");
            notificacionClient.enviarNotificacion(notificacion);
        } catch (Exception e) {
            log.warn("Fallo de comunicación con Notificaciones: {}", e.getMessage());
        }
    }
}