package com.autoservicios.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.autoservicios.enums.EstadoTecnico;
import jakarta.persistence.*;

@Entity
@Table(name = "tecnicos")
public class Tecnico extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @JsonIgnore
    private Usuario usuario;


    @Column(nullable = false)
    private String ciudad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTecnico estado;

    @Column(name = "ultima_asignacion")
    private LocalDateTime ultimaAsignacion;

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public EstadoTecnico getEstado() {
        return estado;
    }

    public void setEstado(EstadoTecnico estado) {
        this.estado = estado;
    }

    public LocalDateTime getUltimaAsignacion() {
        return ultimaAsignacion;
    }

    public void setUltimaAsignacion(LocalDateTime ultimaAsignacion) {
        this.ultimaAsignacion = ultimaAsignacion;
    }

}
