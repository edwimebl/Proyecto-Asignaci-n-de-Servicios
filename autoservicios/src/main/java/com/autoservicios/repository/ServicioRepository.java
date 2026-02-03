package com.autoservicios.repository;

import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.model.Servicio;
import com.autoservicios.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // 1️⃣ Traer todos los servicios por estado
    // Ejemplo: todos los PENDIENTES o FINALIZADOS
    List<Servicio> findByEstado(EstadoServicio estado);

    // 2️⃣ Traer todos los servicios asignados a un técnico
    List<Servicio> findByTecnico(Tecnico tecnico);

    // 3️⃣ Traer servicios de un técnico filtrados por estado
    List<Servicio> findByTecnicoAndEstado(Tecnico tecnico, EstadoServicio estado);
}
