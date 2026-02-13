package com.autoservicios.repository;

import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.model.Servicio;
import com.autoservicios.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Traer todos los servicios por estado
    // Ejemplo: todos los PENDIENTES o FINALIZADOS
    List<Servicio> findByEstado(EstadoServicio estado);

    // Traer todos los servicios asignados a un técnico
    List<Servicio> findByTecnico(Tecnico tecnico);

    // Traer servicios de un técnico filtrados por estado
    List<Servicio> findByTecnicoAndEstado(Tecnico tecnico, EstadoServicio estado);

    //Buscar servicios en estado pendiente para cuando se libere el técnico pueda ser asigando
    List<Servicio> findByCiudadAndEstadoOrderByFechaCreacionAsc(
            String ciudad,
            EstadoServicio estado);

}
