package com.gymapp.ms_miembros.repository;

import com.gymapp.ms_miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Miembro> findByEmailIgnoreCase(String email);

    List<Miembro> findByActivoTrue();

    Optional<Miembro> findByIdAndActivoTrue(Long id);

    long countByActivoTrue();

    List<Miembro> findByActivoFalse();

    List<Miembro> findByFechaRegistroAfter(LocalDate fecha);

    List<Miembro> findByFechaRegistro(LocalDate fecha);

    List<Miembro> findByEmailContainingIgnoreCase(String dominio);
}