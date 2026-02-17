package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICuentaJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.ICuentaJpaRepositorio;

public class CuentaRepositorioImpl implements ICuentaRepositorio {

	private final ICuentaJpaRepositorio jpaRepository;
	private final ICuentaJpaMapper mapper;

	public CuentaRepositorioImpl(ICuentaJpaRepositorio jpaRepository, ICuentaJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Cuenta guardar(Cuenta cuenta) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(cuenta)));
	}

	@Override
	public Cuenta actualizar(Cuenta cuenta) {
		return guardar(cuenta);
	}

	@Override
	public Optional<Cuenta> buscarPorId(int idcuenta) {
		return jpaRepository.findById(idcuenta).map(mapper::toDomain);
	}

	@Override
	public List<Cuenta> listarTodas() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Cuenta> listarAbiertas() {
		return jpaRepository.findByEstado("ABIERTA").stream().map(mapper::toDomain).toList();
	}

	@Override
	public boolean existePorId(int idcuenta) {
		return jpaRepository.existsById(idcuenta);
	}

	@Override
	public void eliminar(int idcuenta) {
		if (existePorId(idcuenta)) {
			jpaRepository.deleteById(idcuenta);
		}
	}
}
