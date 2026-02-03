package com.autoservicios.service;

import com.autoservicios.model.Usuario;
import com.autoservicios.enums.RolUsuario;

public interface UsuarioService {
    Usuario crearUsuario(String nombre, String email, String password, RolUsuario rol);
}
