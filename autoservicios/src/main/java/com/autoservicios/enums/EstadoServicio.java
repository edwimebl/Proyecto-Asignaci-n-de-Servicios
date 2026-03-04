package com.autoservicios.enums;

public enum EstadoServicio {

    PENDIENTE,
    ASIGNADO,
    EN_CAMINO,
    EN_SERVICIO,
    FINALIZADO,
    CANCELADO;

    public boolean puedeCambiarA(EstadoServicio nuevoEstado) {

        switch (this) {

            case PENDIENTE:
                return nuevoEstado == ASIGNADO
                        || nuevoEstado == CANCELADO;

            case ASIGNADO:
                return nuevoEstado == EN_CAMINO
                        || nuevoEstado == CANCELADO;

            case EN_CAMINO:
                return nuevoEstado == EN_SERVICIO
                        || nuevoEstado == CANCELADO;

            case EN_SERVICIO:
                return nuevoEstado == FINALIZADO;

            case FINALIZADO:
            case CANCELADO:
                return false;

            default:
                return false;
        }
    }
}
