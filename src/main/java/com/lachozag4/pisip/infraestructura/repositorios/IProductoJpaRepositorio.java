package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;

public interface IProductoJpaRepositorio extends JpaRepository<ProductoJpa, Integer> {

	List<ProductoJpa> findByEstadoTrue();
	
	List<ProductoJpa> findByEstadoAndStockActualGreaterThan(boolean estado, int stockMinimo);
	
	List<ProductoJpa> findByFkCategoriaId_Idcategoria(int idCategoria);
	
	Optional<ProductoJpa> findByNombreAndFkCategoriaId_Idcategoria(String nombre, int idCategoria);
	
	List<ProductoJpa> findByNombreContaining(String nombre);
}