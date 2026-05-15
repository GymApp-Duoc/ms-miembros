package com.gymapp.ms_miembros.service;

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

    @Override
    @Transactional(readOnly = true)
    public List<MiembroResponseDTO> listarTodos() {
        log.info("Consultando la lista de miembros ACTIVOS en el sistema");
        return repository.findByActivoTrue().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MiembroResponseDTO obtenerPorId(Long id) {
        log.info("Buscando miembro activo por ID: {}", id);
        Miembro miembro = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o inactivo con ID: " + id));
        return mapearADto(miembro);
    }

    @Override
    @Transactional
    public MiembroResponseDTO crear(MiembroRequestDTO dto) {
        log.info("Iniciando registro de nuevo miembro: {}", dto.getEmail());

        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado en el sistema.");
        }

        Miembro miembro = new Miembro(
                null,
                dto.getNombre(),
                dto.getApellido(),
                dto.getEmail(),
                dto.getTelefono(),
                LocalDate.now(),
                true
        );

        Miembro guardado = repository.save(miembro);
        log.info("Miembro creado exitosamente con ID: {}", guardado.getId());

        enviarRecompensasYBienvenida(guardado);

        return mapearADto(guardado);
    }

    @Override
    @Transactional
    public MiembroResponseDTO actualizar(Long id, MiembroRequestDTO dto) {
        log.info("Iniciando actualización para el miembro ID: {}", id);

        Miembro existente = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o inactivo para actualizar."));

        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail()) && repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new BusinessException("El nuevo correo electrónico ya está en uso por otro miembro.");
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());

        log.info("Miembro ID {} actualizado correctamente en la base de datos.", id);
        return mapearADto(repository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Aplicando borrado lógico al miembro ID: {}", id);

        Miembro miembro = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Miembro no encontrado o ya se encuentra inactivo."));

        miembro.setActivo(false);
        repository.save(miembro);
        log.info("Miembro ID {} desactivado correctamente (activo = false).", id);
    }

    private void enviarRecompensasYBienvenida(Miembro miembro) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("miembroId", miembro.getId());
            evento.put("accion", "REGISTRO_NUEVO");
            evento.put("puntosBase", 50);
            gamificacionClient.enviarEvento(evento);
            log.info("Puntos de bienvenida enviados al microservicio de Gamificación para el miembro {}", miembro.getId());
        } catch (Exception e) {
            log.warn("No se pudo establecer comunicación con Gamificación: {}", e.getMessage());
        }

        try {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("miembroId", miembro.getId());
            notificacion.put("titulo", "¡Bienvenido a GymApp!");
            notificacion.put("mensaje", "Hola " + miembro.getNombre() + ", tu cuenta ha sido creada con éxito.");
            notificacionClient.enviarNotificacion(notificacion);
            log.info("Notificación de bienvenida enviada al microservicio de Notificaciones.");
        } catch (Exception e) {
            log.warn("No se pudo establecer comunicación con Notificaciones: {}", e.getMessage());
        }
    }

    private MiembroResponseDTO mapearADto(Miembro m) {
        return MiembroResponseDTO.builder()
                .id(m.getId())
                .nombre(m.getNombre())
                .apellido(m.getApellido())
                .email(m.getEmail())
                .telefono(m.getTelefono())
                .fechaRegistro(m.getFechaRegistro())
                .activo(m.isActivo())
                .build();
    }
}

