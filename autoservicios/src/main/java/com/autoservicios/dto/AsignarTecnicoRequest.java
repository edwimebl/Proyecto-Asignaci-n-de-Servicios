package com.autoservicios.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarTecnicoRequest {

    @NotNull(message = "El id del técnico es obligatorio")
    private Long tecnicoId;

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
}
