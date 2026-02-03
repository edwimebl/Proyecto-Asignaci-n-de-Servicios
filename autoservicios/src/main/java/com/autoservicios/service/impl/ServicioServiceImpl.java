package com.autoservicios.service.impl;

import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.enums.EstadoTecnico;
import com.autoservicios.enums.TipoServicio;
import com.autoservicios.exception.EstadoInvalidoException;
import com.autoservicios.exception.RecursoNoEncontradoException;
import com.autoservicios.model.Servicio;
import com.autoservicios.model.Tecnico;
import com.autoservicios.repository.ServicioRepository;
import com.autoservicios.repository.TecnicoRepository;
import com.autoservicios.service.ServicioService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final TecnicoRepository tecnicoRepository;

    public ServicioServiceImpl(
            ServicioRepository servicioRepository,
            TecnicoRepository tecnicoRepository) {
        this.servicioRepository = servicioRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    // ============================
    // VALIDACIÓN DE TRANSICIONES
    // ============================
    private boolean esTransicionValida(
            EstadoServicio actual,
            EstadoServicio nuevo) {

        switch (actual) {
            case PENDIENTE:
                return nuevo == EstadoServicio.ASIGNADO
                    || nuevo == EstadoServicio.CANCELADO;

            case ASIGNADO:
                return nuevo == EstadoServicio.EN_CAMINO
                        || nuevo == EstadoServicio.CANCELADO;

            case EN_CAMINO:
                return nuevo == EstadoServicio.EN_SERVICIO
                        || nuevo == EstadoServicio.CANCELADO;

            case EN_SERVICIO:
                return nuevo == EstadoServicio.FINALIZADO
                        || nuevo == EstadoServicio.CANCELADO;

            default:
                return false;
        }
    }

    // ============================
    // CREAR SERVICIO (SIN CAMBIOS)
    // ============================
    @Override
    public Servicio crearServicio(
            TipoServicio tipoServicio,
            String ciudad,
            String direccion,
            String observaciones,
            String nombreSolicitante,
            String telefonoSolicitante) {

        Servicio servicio = new Servicio();
        servicio.setTipoServicio(tipoServicio);
        servicio.setCiudad(ciudad);
        servicio.setDireccion(direccion);
        servicio.setObservaciones(observaciones);
        servicio.setNombreSolicitante(nombreSolicitante);
        servicio.setTelefonoSolicitante(telefonoSolicitante);

        List<Tecnico> tecnicosDisponibles = tecnicoRepository.findByCiudadAndEstado(ciudad, EstadoTecnico.DISPONIBLE);

        if (!tecnicosDisponibles.isEmpty()) {
            Tecnico tecnico = tecnicosDisponibles.get(0);

            servicio.setTecnico(tecnico);
            servicio.setEstado(EstadoServicio.ASIGNADO);

            tecnico.setEstado(EstadoTecnico.OCUPADO);
            tecnicoRepository.save(tecnico);
        } else {
            servicio.setEstado(EstadoServicio.PENDIENTE);
        }

        return servicioRepository.save(servicio);
    }

    
    // Cambiar estado
    
    @Override
    public Servicio cambiarEstado(Long servicioId, EstadoServicio nuevoEstado) {

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Servicio no encontrado con id: " + servicioId));

        EstadoServicio estadoActual = servicio.getEstado();

        if (!esTransicionValida(estadoActual, nuevoEstado)) {
            throw new EstadoInvalidoException(
                    "No se puede cambiar de " + estadoActual + " a " + nuevoEstado);
        }

        servicio.setEstado(nuevoEstado);

        // Liberar técnico si finaliza o cancela

        if (nuevoEstado == EstadoServicio.FINALIZADO
                || nuevoEstado == EstadoServicio.CANCELADO) {

            Tecnico tecnico = servicio.getTecnico();

            if (tecnico != null) {
                tecnico.setEstado(EstadoTecnico.DISPONIBLE);
                tecnicoRepository.save(tecnico);
                //servicio.setTecnico(null);  Hace que cuando técnico vuelva a disponible se quita del historial de servicio 
            }
        }

        return servicioRepository.save(servicio);
    }

    // Obtener por ID
    
    @Override
    public Servicio obtenerPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Servicio no encontrado con id: " + id));
    }

    @Override
    public Servicio asignarTecnico(Long servicioId, Long tecnicoId) {

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Servicio no encontrado con id: " + servicioId));

        if (servicio.getEstado() != EstadoServicio.PENDIENTE) {
            throw new EstadoInvalidoException(
                    "Solo se pueden asignar técnicos a servicios en estado PENDIENTE");
        }

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Técnico no encontrado con id: " + tecnicoId));

        if (tecnico.getEstado() != EstadoTecnico.DISPONIBLE) {
            throw new EstadoInvalidoException(
                    "El técnico no está disponible");
        }

        // Validando que sea de la misma ciudad 
        if (!tecnico.getCiudad().equalsIgnoreCase(servicio.getCiudad())) {
            throw new EstadoInvalidoException(
                    "El técnico no pertenece a la misma ciudad del servicio");
        }

        servicio.setTecnico(tecnico);
        servicio.setEstado(EstadoServicio.ASIGNADO);

        tecnico.setEstado(EstadoTecnico.OCUPADO);

        tecnicoRepository.save(tecnico);
        return servicioRepository.save(servicio);
    }

}
