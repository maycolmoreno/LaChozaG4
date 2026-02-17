package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pago;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CajaTurnoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PagoJpa;
import com.lachozag4.pisip.infraestructura.repositorios.IPagoJpaRepositorio;

public class PagoRepositorioImpl implements IPagoRepositorio {

	private final IPagoJpaRepositorio jpaRepositorio;

	public PagoRepositorioImpl(IPagoJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public Pago guardar(Pago pago) {
		PagoJpa saved = jpaRepositorio.save(toEntity(pago));
		return toDomain(saved);
	}

	@Override
	public List<Pago> listarPorCuenta(int idcuenta) {
		return jpaRepositorio.findByFkCuenta_Idcuenta(idcuenta).stream().map(this::toDomain).toList();
	}

	@Override
	public double totalPagadoCuenta(int idcuenta) {
		Double total = jpaRepositorio.totalPagadoCuenta(idcuenta);
		return total != null ? total : 0.0;
	}

	@Override
	public double totalPagadoCaja(int idcaja) {
		Double total = jpaRepositorio.totalPagadoCaja(idcaja);
		return total != null ? total : 0.0;
	}

	private PagoJpa toEntity(Pago domain) {
		PagoJpa entity = new PagoJpa();
		entity.setIdpago(domain.getIdpago());
		entity.setFecha(domain.getFecha());
		entity.setMonto(domain.getMonto());
		entity.setMetodo(domain.getMetodo());
		entity.setReferencia(domain.getReferencia());
		entity.setUsuario(domain.getUsuario());

		CuentaJpa cuenta = new CuentaJpa();
		cuenta.setIdcuenta(domain.getIdcuenta());
		entity.setFkCuenta(cuenta);

		CajaTurnoJpa caja = new CajaTurnoJpa();
		caja.setIdcaja(domain.getIdcaja());
		entity.setFkCajaTurno(caja);
		return entity;
	}

	private Pago toDomain(PagoJpa entity) {
		return new Pago(entity.getIdpago(), entity.getFecha(), entity.getMonto(), entity.getMetodo(),
				entity.getReferencia(), entity.getUsuario(), entity.getFkCuenta().getIdcuenta(),
				entity.getFkCajaTurno().getIdcaja());
	}
}
