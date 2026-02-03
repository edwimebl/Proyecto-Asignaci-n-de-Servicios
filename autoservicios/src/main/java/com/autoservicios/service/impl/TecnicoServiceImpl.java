package com.autoservicios.service.impl;

import com.autoservicios.enums.EstadoTecnico;
import com.autoservicios.model.Tecnico;
import com.autoservicios.model.Usuario;
import com.autoservicios.repository.TecnicoRepository;
import com.autoservicios.repository.UsuarioRepository;
import com.autoservicios.service.TecnicoService;
import org.springframework.stereotype.Service;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final UsuarioRepository usuarioRepository;

    public TecnicoServiceImpl(TecnicoRepository tecnicoRepository,
            UsuarioRepository usuarioRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Tecnico crearTecnico(Long usuarioId, String ciudad) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Tecnico tecnico = new Tecnico();
        tecnico.setUsuario(usuario);
        tecnico.setCiudad(ciudad);
        tecnico.setEstado(EstadoTecnico.DISPONIBLE);

        return tecnicoRepository.save(tecnico);
    }
}
