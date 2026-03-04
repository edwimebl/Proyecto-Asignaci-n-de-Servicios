package com.autoservicios.repository;

import com.autoservicios.enums.EstadoTecnico;
import com.autoservicios.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    
    // Listados normales
  
    List<Tecnico> findByCiudadAndEstado(String ciudad, EstadoTecnico estado);

    Optional<Tecnico> findFirstByEstadoAndCiudadIgnoreCase(
            EstadoTecnico estado,
            String ciudad);

    
    // Bloqueo pesimista
    

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tecnico t WHERE t.id = :id")
    Optional<Tecnico> findByIdForUpdate(Long id);
}