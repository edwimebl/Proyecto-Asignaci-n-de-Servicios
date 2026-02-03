package com.autoservicios.repository;

import com.autoservicios.enums.EstadoTecnico;
import com.autoservicios.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    List<Tecnico> findByCiudadAndEstado(String ciudad, EstadoTecnico estado);
}
