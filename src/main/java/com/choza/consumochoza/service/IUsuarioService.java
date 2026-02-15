package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.UsuarioDTO;

public interface IUsuarioService {

    boolean existeAlgunUsuario();
    
    List<UsuarioDTO> listarTodos();
    
    UsuarioDTO obtenerPorId(int id);
    
    UsuarioDTO crear(UsuarioDTO usuario);
    
    UsuarioDTO actualizar(int id, UsuarioDTO usuario);
    
    void eliminar(int id);
	
    void cambiarPassword(String username, String passwordActual, String passwordNuevo);

    UsuarioDTO crearAdminInicial(UsuarioDTO usuario);
}
