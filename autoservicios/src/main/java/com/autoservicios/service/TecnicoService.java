package com.autoservicios.service;

import com.autoservicios.model.Tecnico;

public interface TecnicoService {

    Tecnico crearTecnico(Long usuarioId, String ciudad);
}
