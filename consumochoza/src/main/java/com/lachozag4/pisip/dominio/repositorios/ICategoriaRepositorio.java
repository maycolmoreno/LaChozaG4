package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Categoria;

/**
 * Interfaz repositorio para la entidad Categoria.
 * Define las operaciones de persistencia para Categoria en la capa de dominio.
 */
public interface ICategoriaRepositorio {
	
	
	Categoria guardar(Categoria categoria);
	
	Optional<Categoria> buscarPorId(int idcategoria);
	
	Optional<Categoria> buscarPorNombre(String nombre);
	
	List<Categoria> listarActivas();
	
	List<Categoria> listarTodas();
	
	
	Categoria actualizar(Categoria categoria);
	
	
	void eliminar(int idcategoria);

}
