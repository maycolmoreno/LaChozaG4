package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.ProductoDTO;

public interface IProductoService {
    
    List<ProductoDTO> listarTodos();
    
    List<ProductoDTO> listarActivos();
    
    List<ProductoDTO> listarPorCategoria(int idCategoria);
    
    ProductoDTO obtenerPorId(int id);
    
    ProductoDTO crear(ProductoDTO producto);
    
    ProductoDTO actualizar(int id, ProductoDTO producto);
    
    void eliminar(int id);
}
