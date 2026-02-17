package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.CajaTurno;

public interface ICajaTurnoRepositorio {

	CajaTurno guardar(CajaTurno cajaTurno);

	Optional<CajaTurno> buscarCajaAbierta();

	Optional<CajaTurno> buscarPorId(int idcaja);

	List<CajaTurno> listarTodos();
}
