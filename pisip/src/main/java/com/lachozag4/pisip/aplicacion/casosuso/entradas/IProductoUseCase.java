package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Producto;

public interface IProductoUseCase {
	
    Producto crear(Producto producto, int categoriaId);
    
    Producto buscarPorId(int id);
    
    List<Producto> listarTodos();
    
    List<Producto> listarPorCategoria(int idCategoria);
    
    List<Producto> listarActivos();
    
    Producto actualizar(int id, Producto producto, int categoriaId);
    
//    Producto actualizarStock(int id, int cantidad);
    
    void eliminar(int id);
}