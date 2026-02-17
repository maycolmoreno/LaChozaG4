package com.lachozag4.consumochoza.service;

import java.util.List;

import com.lachozag4.consumochoza.modelo.dto.MesaDTO;

public interface IMesaService {
    
    List<MesaDTO> listarTodas();
    
    /**
     * Lista mesas disponibles (sin pedidos activos)
     */
    List<MesaDTO> listarDisponibles();
    
    /**
     * Lista mesas ocupadas (con pedidos activos)
     */
    List<MesaDTO> listarOcupadas();
    
    MesaDTO obtenerPorId(int id);
    
    MesaDTO crear(MesaDTO mesa);
    
    MesaDTO actualizar(int id, MesaDTO mesa);
    
    void eliminar(int id);
}

