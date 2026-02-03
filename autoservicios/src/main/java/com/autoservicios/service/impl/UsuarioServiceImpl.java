package com.autoservicios.service.impl;

import org.springframework.stereotype.Service;
import com.autoservicios.enums.RolUsuario;
import com.autoservicios.model.Usuario;
import com.autoservicios.repository.UsuarioRepository;
import com.autoservicios.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario crearUsuario(String nombre, String email, String password, RolUsuario rol) {

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password); // 🔥 ESTA LÍNEA ES LA CLAVE
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }
}
