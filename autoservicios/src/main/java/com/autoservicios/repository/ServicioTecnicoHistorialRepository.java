package com.autoservicios.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.autoservicios.dto.HistorialTecnicoDTO;
import com.autoservicios.model.ServicioTecnicoHistorial;

public interface ServicioTecnicoHistorialRepository
        extends JpaRepository<ServicioTecnicoHistorial, Long> {

    // Listar historial completo de un servicio
    List<ServicioTecnicoHistorial> findByServicioIdOrderByFechaInicioDesc(Long servicioId);

    // Obtener historial activo (el que no tiene fechaFin)
    Optional<ServicioTecnicoHistorial> findFirstByServicioIdAndFechaFinIsNull(Long servicioId);

    // Historial profesional por técnico en rango de fechas (DTO)
    @Query("""
                SELECT new com.autoservicios.dto.HistorialTecnicoDTO(
                    h.servicio.id,
                    h.servicio.tipoServicio,
                    h.servicio.ciudad,
                    h.fechaInicio,
                    h.fechaFin,
                    h.motivoFin
                )
                FROM ServicioTecnicoHistorial h
                WHERE h.tecnico.id = :tecnicoId
                AND h.fechaInicio BETWEEN :inicio AND :fin
            """)
    List<HistorialTecnicoDTO> findHistorialPorTecnico(
            @Param("tecnicoId") Long tecnicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}