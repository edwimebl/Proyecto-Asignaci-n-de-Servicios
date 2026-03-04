package com.autoservicios.service.impl;

import com.autoservicios.enums.*;
import com.autoservicios.exception.EstadoInvalidoException;
import com.autoservicios.exception.RecursoNoEncontradoException;
import com.autoservicios.model.*;
import com.autoservicios.repository.*;
import com.autoservicios.service.ServicioService;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ServicioTecnicoHistorialRepository historialRepository;

    public ServicioServiceImpl(
            ServicioRepository servicioRepository,
            TecnicoRepository tecnicoRepository,
            ServicioTecnicoHistorialRepository historialRepository) {

        this.servicioRepository = servicioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.historialRepository = historialRepository;
    }
    
    //Método para listar los servicios generados

    @Override
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }
    
    //Método para obteber un servicio por ID

    @Override
    public Servicio obtenerPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Servicio no encontrado con id: " + id));
    }

   
    //Método para eliminar un servicio

    @Override
    public void eliminarServicio(Long id) {
        servicioRepository.delete(obtenerPorId(id));
    }

   
    // Método para crear un servicio

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
        servicio.setEstado(EstadoServicio.PENDIENTE);

        servicio = servicioRepository.save(servicio);

        crearHistorial(servicio, null);

        asignacionAutomatica(servicio);

        return servicio;
    }

    
    //Método para la asignación de un servicio a un técnico
    

    @Override
    public Servicio asignarTecnico(Long servicioId, Long tecnicoId) {

        Servicio servicio = obtenerPorId(servicioId);
        validarAsignacion(servicio);

        Tecnico tecnico = obtenerTecnicoDisponible(tecnicoId, servicio.getCiudad());

        cerrarHistorialActivo(servicioId, MotivoFinAsignacion.ASIGNACION);

        asignar(servicio, tecnico);

        crearHistorial(servicio, tecnico);

        return servicio;
    }

    
    // Método para reasignar un técnico a un servicio
   
    @Override
    public void reasignarTecnico(Long servicioId, Long tecnicoNuevoId) {

        Servicio servicio = obtenerPorId(servicioId);

        if (servicio.getEstado() != EstadoServicio.ASIGNADO) {
            throw new EstadoInvalidoException(
                    "Solo se puede reasignar en estado ASIGNADO");
        }

        Tecnico tecnicoActual = servicio.getTecnico();
        Tecnico tecnicoNuevo = obtenerTecnicoDisponible(tecnicoNuevoId, servicio.getCiudad());

        cerrarHistorialActivo(servicioId, MotivoFinAsignacion.REASIGNACION);

        tecnicoActual.setEstado(EstadoTecnico.DISPONIBLE);
        tecnicoRepository.save(tecnicoActual);

        asignar(servicio, tecnicoNuevo);

        crearHistorial(servicio, tecnicoNuevo);
    }

    
    // Método para los cambios de estado

    @Override
    public Servicio cambiarEstado(Long servicioId, EstadoServicio nuevoEstado) {

        Servicio servicio = obtenerPorId(servicioId);

        EstadoServicio estadoActual = servicio.getEstado();

        //No permitir modificar servicios cerrados
        if (estadoActual == EstadoServicio.FINALIZADO ||
                estadoActual == EstadoServicio.CANCELADO) {

            throw new EstadoInvalidoException(
                    "El servicio ya está cerrado y no se puede cambiar de estado");
        }

        //Valida los cambios de estados permitidos segun el flujo establecido
        if (!estadoActual.puedeCambiarA(nuevoEstado)) {
            throw new EstadoInvalidoException(
                    "El servicio se encuentra " + estadoActual +
                            " y no se puede cambiar a " + nuevoEstado);
        }

        // 🔄 Si se va a cerrar el servicio
        if (nuevoEstado == EstadoServicio.FINALIZADO ||
                nuevoEstado == EstadoServicio.CANCELADO) {

            cerrarHistorialActivo(
                    servicioId,
                    nuevoEstado == EstadoServicio.FINALIZADO
                            ? MotivoFinAsignacion.FINALIZADO
                            : MotivoFinAsignacion.CANCELADO);

            liberarTecnico(servicio);
        }

        servicio.setEstado(nuevoEstado);

        return servicioRepository.save(servicio);
    }
    
    // Métodos privados

    private void asignacionAutomatica(Servicio servicio) {

        tecnicoRepository
                .findFirstByEstadoAndCiudadIgnoreCase(
                        EstadoTecnico.DISPONIBLE,
                        servicio.getCiudad())
                .ifPresent(tecnico -> {

                    cerrarHistorialActivo(
                            servicio.getId(),
                            MotivoFinAsignacion.ASIGNACION);

                    asignar(servicio, tecnico);

                    crearHistorial(servicio, tecnico);
                });
    }

    private void asignar(Servicio servicio, Tecnico tecnico) {

        try {

            servicio.setTecnico(tecnico);
            servicio.setEstado(EstadoServicio.ASIGNADO);

            tecnico.setEstado(EstadoTecnico.OCUPADO);
            tecnico.setUltimaAsignacion(LocalDateTime.now());

            tecnicoRepository.save(tecnico); // 🔥 aquí ocurre el locking
            servicioRepository.save(servicio);

        } catch (ObjectOptimisticLockingFailureException ex) {

            throw new EstadoInvalidoException(
                    "El técnico fue asignado por otro proceso. Intente nuevamente.");
        }
    }

    private void crearHistorial(Servicio servicio, Tecnico tecnico) {

        ServicioTecnicoHistorial historial = new ServicioTecnicoHistorial();

        historial.setServicio(servicio);
        historial.setTecnico(tecnico);
        historial.setFechaInicio(LocalDateTime.now());

        historialRepository.save(historial);
    }

    private void cerrarHistorialActivo(Long servicioId,
            MotivoFinAsignacion motivo) {

        historialRepository
                .findFirstByServicioIdAndFechaFinIsNull(servicioId)
                .ifPresent(historial -> {
                    historial.setFechaFin(LocalDateTime.now());
                    historial.setMotivoFin(motivo);
                    historialRepository.save(historial);
                });
    }

    private void liberarTecnico(Servicio servicio) {

        if (servicio.getTecnico() != null) {
            Tecnico tecnico = servicio.getTecnico();
            tecnico.setEstado(EstadoTecnico.DISPONIBLE);
            tecnicoRepository.save(tecnico);
        }
    }

    private void validarAsignacion(Servicio servicio) {
        if (servicio.getEstado() != EstadoServicio.PENDIENTE) {
            throw new EstadoInvalidoException(
                    "Solo se puede asignar en estado PENDIENTE");
        }
    }

    private Tecnico obtenerTecnicoDisponible(Long id, String ciudad) {

        Tecnico tecnico = tecnicoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Técnico no encontrado"));

        if (tecnico.getEstado() != EstadoTecnico.DISPONIBLE) {
            throw new EstadoInvalidoException(
                    "El técnico no está disponible");
        }

        if (!tecnico.getCiudad().equalsIgnoreCase(ciudad)) {
            throw new EstadoInvalidoException(
                    "El técnico debe pertenecer a la misma ciudad");
        }

        return tecnico;
    }
}