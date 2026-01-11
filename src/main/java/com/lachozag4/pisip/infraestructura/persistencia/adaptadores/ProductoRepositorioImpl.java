package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IProductoJpaRepositorio;

public class ProductoRepositorioImpl implements IProductoRepositorio {

	private final IProductoJpaRepositorio jparepository;
	private final IProductoJpaMapper entityMapper;

	public ProductoRepositorioImpl(IProductoJpaRepositorio jparepository, IProductoJpaMapper entityMapper) {
		this.jparepository = jparepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Producto guardar(Producto producto) {
		ProductoJpa entity = entityMapper.toEntity(producto);
		ProductoJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Producto> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Producto> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
