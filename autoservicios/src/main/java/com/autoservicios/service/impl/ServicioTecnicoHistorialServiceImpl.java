package com.autoservicios.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoservicios.dto.HistorialTecnicoDTO;
import com.autoservicios.repository.ServicioTecnicoHistorialRepository;
import com.autoservicios.service.ServicioTecnicoHistorialService;

@Service
public class ServicioTecnicoHistorialServiceImpl
        implements ServicioTecnicoHistorialService {

    private final ServicioTecnicoHistorialRepository historialRepository;

    public ServicioTecnicoHistorialServiceImpl(
            ServicioTecnicoHistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialTecnicoDTO> obtenerServiciosPorTecnicoEnRango(
            Long tecnicoId,
            LocalDateTime inicio,
            LocalDateTime fin) {

        // Validación básica de fechas para la consulta
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser mayor que la fecha fin");
        }

        return historialRepository.findHistorialPorTecnico(
                tecnicoId,
                inicio,
                fin);
    }
}