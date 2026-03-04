package com.autoservicios.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import com.autoservicios.enums.TipoServicio;
import com.autoservicios.enums.MotivoFinAsignacion;
import com.fasterxml.jackson.annotation.JsonFormat;

public class HistorialTecnicoDTO {

    private Long servicioId;
    private TipoServicio tipoServicio;
    private String ciudad;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaInicio;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fechaFin;

    private MotivoFinAsignacion motivoFin;

    // Campo calculado (NO viene de la query)
    private Long minutosDuracion;

    public HistorialTecnicoDTO(
            Long servicioId,
            TipoServicio tipoServicio,
            String ciudad,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            MotivoFinAsignacion motivoFin) {

        this.servicioId = servicioId;
        this.tipoServicio = tipoServicio;
        this.ciudad = ciudad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivoFin = motivoFin;

        // 👇 cálculo automático de duración
        if (fechaInicio != null && fechaFin != null) {
            this.minutosDuracion =
                    Duration.between(fechaInicio, fechaFin).toMinutes();
        }
    }
    
    public Long getServicioId() {
        return servicioId;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public MotivoFinAsignacion getMotivoFin() {
        return motivoFin;
    }

    public Long getMinutosDuracion() {
        return minutosDuracion;
    }
}