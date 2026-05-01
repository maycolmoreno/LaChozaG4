package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.CategoriaDTO;

public interface ICategoriaService {
    
    List<CategoriaDTO> listarTodas();
    
    List<CategoriaDTO> listarActivas();
    
    CategoriaDTO obtenerPorId(int id);
    
    CategoriaDTO crear(CategoriaDTO categoria);
    
    CategoriaDTO actualizar(int id, CategoriaDTO categoria);
    
    void eliminar(int id);
}
