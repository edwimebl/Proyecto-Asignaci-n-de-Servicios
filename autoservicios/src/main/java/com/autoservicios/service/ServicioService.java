package com.autoservicios.service;

import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.enums.TipoServicio;
import com.autoservicios.model.Servicio;

public interface ServicioService {

    Servicio crearServicio(
            TipoServicio tipoServicio,
            String ciudad,
            String direccion,
            String observaciones,
            String nombreSolicitante,
            String telefonoSolicitante);

    Servicio cambiarEstado(Long servicioId, EstadoServicio nuevoEstado);

    Servicio obtenerPorId(Long id);

    Servicio asignarTecnico(Long servicioId, Long tecnicoId);

}
