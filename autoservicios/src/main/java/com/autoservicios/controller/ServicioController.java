package com.autoservicios.controller;

import jakarta.validation.Valid;

import com.autoservicios.dto.AsignarTecnicoRequest;
import com.autoservicios.dto.CrearServicioRequest;
import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.model.Servicio;
import com.autoservicios.service.ServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @PostMapping
    public ResponseEntity<Servicio> crearServicio(
            
            @Valid @RequestBody CrearServicioRequest request) {
        Servicio servicio = servicioService.crearServicio(
                request.getTipoServicio(),
                request.getCiudad(),
                request.getDireccion(),
                request.getObservaciones(),
                request.getNombreSolicitante(),
                request.getTelefonoSolicitante());

        return new ResponseEntity<>(servicio, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/estado")
    public Servicio cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoServicio estado) {

        return servicioService.cambiarEstado(id, estado);
    }
    
    @GetMapping("/{id}")
    public Servicio obtenerServicio(@PathVariable Long id) {
        return servicioService.obtenerPorId(id);
    }
    //Asignar técnico manual cuando está en estado "PENDIENTE"
    
    @PatchMapping("/{id}/asignar-tecnico")
    public ResponseEntity<Servicio> asignarTecnico(
            @PathVariable Long id,
            @RequestBody AsignarTecnicoRequest request) {

        Servicio servicio = servicioService.asignarTecnico(
                id,
                request.getTecnicoId());

        return ResponseEntity.ok(servicio);
    }
}
