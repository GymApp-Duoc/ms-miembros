package com.gymapp.ms_miembros.repository;

import com.gymapp.ms_miembros.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MiembroRepository extends JpaRepository<Miembro, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Miembro> findByEmailIgnoreCase(String email);

    List<Miembro> findByActivoTrue();

    Optional<Miembro> findByIdAndActivoTrue(Long id);
}
