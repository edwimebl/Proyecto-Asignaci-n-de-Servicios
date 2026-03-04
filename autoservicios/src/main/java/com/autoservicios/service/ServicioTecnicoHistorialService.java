package com.autoservicios.service;

import java.time.LocalDateTime;
import java.util.List;

import com.autoservicios.dto.HistorialTecnicoDTO;

public interface ServicioTecnicoHistorialService {

    List<HistorialTecnicoDTO> obtenerServiciosPorTecnicoEnRango(
            Long tecnicoId,
            LocalDateTime inicio,
            LocalDateTime fin);
}