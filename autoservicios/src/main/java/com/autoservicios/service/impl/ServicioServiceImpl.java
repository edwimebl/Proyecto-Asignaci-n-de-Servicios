package com.autoservicios.service.impl;

import com.autoservicios.enums.*;
import com.autoservicios.exception.EstadoInvalidoException;
import com.autoservicios.exception.RecursoNoEncontradoException;
import com.autoservicios.model.*;
import com.autoservicios.repository.*;
import com.autoservicios.service.ServicioService;

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

    // ======================================================
    // LISTAR SERVICIOS
    // ======================================================

    @Override
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    // ======================================================
    // ELIMINAR SERVICIO
    // ======================================================

    @Override
    public void eliminarServicio(Long id) {
        Servicio servicio = obtenerPorId(id);
        servicioRepository.delete(servicio);
    }

    // ======================================================
    // CREAR SERVICIO
    // ======================================================

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

        Servicio servicioGuardado = servicioRepository.save(servicio);

        // Crear historial inicial
        ServicioTecnicoHistorial historial = new ServicioTecnicoHistorial();
        historial.setServicio(servicioGuardado);
        historial.setFechaInicio(LocalDateTime.now());
        historialRepository.save(historial);

        // 🔥 ASIGNACIÓN AUTOMÁTICA
        tecnicoRepository.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTecnico.DISPONIBLE)
                .filter(t -> t.getCiudad().equalsIgnoreCase(ciudad))
                .findFirst()
                .ifPresent(tecnicoDisponible -> {

                    // Cerrar historial PENDIENTE
                    cerrarHistorialActivo(servicioGuardado.getId(), MotivoFinAsignacion.ASIGNACION);

                    servicioGuardado.setTecnico(tecnicoDisponible);
                    servicioGuardado.setEstado(EstadoServicio.ASIGNADO);

                    tecnicoDisponible.setEstado(EstadoTecnico.OCUPADO);
                    tecnicoDisponible.setUltimaAsignacion(LocalDateTime.now());

                    tecnicoRepository.save(tecnicoDisponible);
                    servicioRepository.save(servicioGuardado);

                    // Crear nuevo historial con técnico
                    ServicioTecnicoHistorial nuevoHistorial = new ServicioTecnicoHistorial();
                    nuevoHistorial.setServicio(servicioGuardado);
                    nuevoHistorial.setTecnico(tecnicoDisponible);
                    nuevoHistorial.setFechaInicio(LocalDateTime.now());

                    historialRepository.save(nuevoHistorial);
                });

        return servicioGuardado;
    }

    // ======================================================
    // OBTENER POR ID
    // ======================================================

    @Override
    public Servicio obtenerPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Servicio no encontrado con id: " + id));
    }

    // ======================================================
    // ASIGNAR TÉCNICO
    // ======================================================

    @Override
    public Servicio asignarTecnico(Long servicioId, Long tecnicoId) {

        Servicio servicio = obtenerPorId(servicioId);

        if (servicio.getEstado() != EstadoServicio.PENDIENTE) {
            throw new EstadoInvalidoException(
                    "Solo se pueden asignar técnicos a servicios en estado PENDIENTE");
        }

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Técnico no encontrado con id: " + tecnicoId));

        if (tecnico.getEstado() != EstadoTecnico.DISPONIBLE) {
            throw new EstadoInvalidoException("El técnico no está disponible");
        }

        if (!tecnico.getCiudad().equalsIgnoreCase(servicio.getCiudad())) {
            throw new EstadoInvalidoException(
                    "El técnico no pertenece a la misma ciudad del servicio");
        }

        // 1️⃣ Cerrar historial activo (PENDIENTE)
        cerrarHistorialActivo(servicioId, MotivoFinAsignacion.ASIGNACION);

        // 2️⃣ Asignar técnico
        servicio.setTecnico(tecnico);
        servicio.setEstado(EstadoServicio.ASIGNADO);

        tecnico.setEstado(EstadoTecnico.OCUPADO);
        tecnico.setUltimaAsignacion(LocalDateTime.now());

        tecnicoRepository.save(tecnico);
        servicioRepository.save(servicio);

        // 3️⃣ Crear nuevo historial
        ServicioTecnicoHistorial nuevoHistorial = new ServicioTecnicoHistorial();
        nuevoHistorial.setServicio(servicio);
        nuevoHistorial.setTecnico(tecnico);
        nuevoHistorial.setFechaInicio(LocalDateTime.now());

        historialRepository.save(nuevoHistorial);

        return servicio;
    }

    // ======================================================
    // REASIGNAR TÉCNICO
    // ======================================================

    @Override
    public void reasignarTecnico(Long idServicio, Long idTecnicoNuevo) {

        Servicio servicio = obtenerPorId(idServicio);

        if (servicio.getEstado() != EstadoServicio.ASIGNADO) {
            throw new EstadoInvalidoException(
                    "Solo se pueden reasignar técnicos en servicios ASIGNADOS");
        }

        Tecnico tecnicoActual = servicio.getTecnico();

        Tecnico tecnicoNuevo = tecnicoRepository.findById(idTecnicoNuevo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Técnico no encontrado con id: " + idTecnicoNuevo));

        if (tecnicoNuevo.getEstado() != EstadoTecnico.DISPONIBLE) {
            throw new EstadoInvalidoException("El técnico nuevo no está disponible");
        }

        if (!tecnicoNuevo.getCiudad().equalsIgnoreCase(servicio.getCiudad())) {
            throw new EstadoInvalidoException(
                    "El técnico debe pertenecer a la misma ciudad");
        }

        // Cerrar historial actual
        cerrarHistorialActivo(idServicio, MotivoFinAsignacion.REASIGNACION);

        // Liberar técnico anterior
        tecnicoActual.setEstado(EstadoTecnico.DISPONIBLE);
        tecnicoRepository.save(tecnicoActual);

        // Asignar nuevo técnico
        servicio.setTecnico(tecnicoNuevo);
        tecnicoNuevo.setEstado(EstadoTecnico.OCUPADO);

        tecnicoRepository.save(tecnicoNuevo);
        servicioRepository.save(servicio);

        // Crear nuevo historial
        ServicioTecnicoHistorial nuevoHistorial = new ServicioTecnicoHistorial();
        nuevoHistorial.setServicio(servicio);
        nuevoHistorial.setTecnico(tecnicoNuevo);
        nuevoHistorial.setFechaInicio(LocalDateTime.now());

        historialRepository.save(nuevoHistorial);
    }

    // ======================================================
    // CAMBIAR ESTADO
    // ======================================================

    @Override
    public Servicio cambiarEstado(Long servicioId, EstadoServicio nuevoEstado) {

        Servicio servicio = obtenerPorId(servicioId);

        if (servicio.getEstado() == EstadoServicio.FINALIZADO ||
                servicio.getEstado() == EstadoServicio.CANCELADO) {

            throw new EstadoInvalidoException(
                    "No se puede cambiar el estado de un servicio finalizado o cancelado");
        }

        if (nuevoEstado == EstadoServicio.FINALIZADO ||
                nuevoEstado == EstadoServicio.CANCELADO) {

            cerrarHistorialActivo(
                    servicioId,
                    nuevoEstado == EstadoServicio.FINALIZADO
                            ? MotivoFinAsignacion.FINALIZADO
                            : MotivoFinAsignacion.CANCELADO);

            if (servicio.getTecnico() != null) {
                Tecnico tecnico = servicio.getTecnico();
                tecnico.setEstado(EstadoTecnico.DISPONIBLE);
                tecnicoRepository.save(tecnico);
            }
        }

        servicio.setEstado(nuevoEstado);

        return servicioRepository.save(servicio);
    }

    // ======================================================
    // CERRAR HISTORIAL ACTIVO
    // ======================================================

    private void cerrarHistorialActivo(Long servicioId, MotivoFinAsignacion motivo) {

        ServicioTecnicoHistorial historialActivo = historialRepository
                .findByServicioId(servicioId)
                .stream()
                .filter(h -> h.getFechaFin() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay historial activo"));

        historialActivo.setFechaFin(LocalDateTime.now());
        historialActivo.setMotivoFin(motivo);

        historialRepository.save(historialActivo);
    }
}
