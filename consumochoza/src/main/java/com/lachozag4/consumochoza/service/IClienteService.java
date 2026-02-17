package com.lachozag4.consumochoza.service;

import java.util.List;

import com.lachozag4.consumochoza.modelo.dto.ClienteDTO;

public interface IClienteService {
    
    List<ClienteDTO> listarTodos();
    
    ClienteDTO obtenerPorId(int id);
    
    ClienteDTO crear(ClienteDTO cliente);
    
    ClienteDTO actualizar(int id, ClienteDTO cliente);
    
    void eliminar(int id);
}

