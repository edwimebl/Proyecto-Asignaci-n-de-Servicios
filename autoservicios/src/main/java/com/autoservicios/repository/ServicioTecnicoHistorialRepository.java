package com.autoservicios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoservicios.model.ServicioTecnicoHistorial;

public interface ServicioTecnicoHistorialRepository
        extends JpaRepository<ServicioTecnicoHistorial, Long> {

    List<ServicioTecnicoHistorial> findByServicioId(Long servicioId);
    
}
