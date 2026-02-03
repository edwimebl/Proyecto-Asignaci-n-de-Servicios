package com.autoservicios.controller;

import com.autoservicios.dto.CrearTecnicoRequest;
import com.autoservicios.model.Tecnico;
import com.autoservicios.service.TecnicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<Tecnico> crearTecnico(
            @RequestBody CrearTecnicoRequest request) {
        Tecnico tecnico = tecnicoService.crearTecnico(
                request.getUsuarioId(),
                request.getCiudad());

        return new ResponseEntity<>(tecnico, HttpStatus.CREATED);
    }
}
