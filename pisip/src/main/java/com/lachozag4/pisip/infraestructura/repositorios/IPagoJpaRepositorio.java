package com.lachozag4.pisip.infraestructura.repositorios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.PagoJpa;

public interface IPagoJpaRepositorio extends JpaRepository<PagoJpa, Integer> {

	List<PagoJpa> findByFkCuenta_Idcuenta(int idcuenta);

	List<PagoJpa> findByFkCajaTurno_Idcaja(int idcaja);

	List<PagoJpa> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

	@Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoJpa p WHERE p.fkCuenta.idcuenta = :idcuenta")
	Double totalPagadoCuenta(@Param("idcuenta") int idcuenta);

	@Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoJpa p WHERE p.fkCajaTurno.idcaja = :idcaja")
	Double totalPagadoCaja(@Param("idcaja") int idcaja);
}
