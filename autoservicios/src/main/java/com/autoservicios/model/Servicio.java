package com.autoservicios.model;


import com.autoservicios.enums.EstadoServicio;
import com.autoservicios.enums.TipoServicio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "servicios")
public class Servicio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de servicio (batería, llanta, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServicio tipoServicio;

    // Ciudad donde se presta el servicio
    @Column(nullable = false)
    private String ciudad;

    // Dirección exacta
    @Column(nullable = false)
    private String direccion;

    // Observaciones adicionales
    private String observaciones;

    // Datos del solicitante
    @Column(nullable = false)
    private String nombreSolicitante;

    @Column(nullable = false)
    private String telefonoSolicitante;

    // Estado del servicio
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoServicio estado;

    // Técnico asignado (puede ser null al inicio)
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tecnico tecnico;


    // =======================
    // GETTERS Y SETTERS
    // =======================

    public Long getId() {
        return id;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getTelefonoSolicitante() {
        return telefonoSolicitante;
    }

    public void setTelefonoSolicitante(String telefonoSolicitante) {
        this.telefonoSolicitante = telefonoSolicitante;
    }

    public EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(EstadoServicio estado) {
        this.estado = estado;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }   

}
