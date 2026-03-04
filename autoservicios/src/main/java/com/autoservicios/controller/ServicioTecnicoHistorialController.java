package com.autoservicios.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.autoservicios.dto.HistorialTecnicoDTO;
import com.autoservicios.service.ServicioTecnicoHistorialService;

@RestController
@RequestMapping("/api/historial")
public class ServicioTecnicoHistorialController {

    private final ServicioTecnicoHistorialService historialService;

    public ServicioTecnicoHistorialController(
            ServicioTecnicoHistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<HistorialTecnicoDTO> obtenerHistorialPorTecnico(
            @PathVariable Long tecnicoId,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        return historialService.obtenerServiciosPorTecnicoEnRango(
                tecnicoId,
                inicio,
                fin);
    }
}