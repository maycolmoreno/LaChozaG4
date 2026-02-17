package com.lachozag4.consumochoza.service;

import java.util.List;

import com.lachozag4.consumochoza.modelo.dto.CategoriaDTO;

public interface ICategoriaService {
    
    List<CategoriaDTO> listarTodas();
    
    List<CategoriaDTO> listarActivas();
    
    CategoriaDTO obtenerPorId(int id);
    
    CategoriaDTO crear(CategoriaDTO categoria);
    
    CategoriaDTO actualizar(int id, CategoriaDTO categoria);
    
    void eliminar(int id);
}

