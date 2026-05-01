package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.ClienteDTO;

public interface IClienteService {
    
    List<ClienteDTO> listarTodos();
    
    ClienteDTO obtenerPorId(int id);
    
    ClienteDTO crear(ClienteDTO cliente);
    
    ClienteDTO actualizar(int id, ClienteDTO cliente);
    
    void eliminar(int id);
}
