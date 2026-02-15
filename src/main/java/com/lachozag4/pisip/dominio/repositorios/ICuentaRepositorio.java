package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Cuenta;

public interface ICuentaRepositorio {

	Cuenta guardar(Cuenta cuenta);

	Cuenta actualizar(Cuenta cuenta);

	Optional<Cuenta> buscarPorId(int idcuenta);

	List<Cuenta> listarTodas();

	List<Cuenta> listarAbiertas();

	boolean existePorId(int idcuenta);

	void eliminar(int idcuenta);
}
