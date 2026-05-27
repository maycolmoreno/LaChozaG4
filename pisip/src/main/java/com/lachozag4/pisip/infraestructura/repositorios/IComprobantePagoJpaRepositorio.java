package com.lachozag4.pisip.infraestructura.repositorios;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComprobantePagoJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IComprobantePagoJpaRepositorio extends JpaRepository<ComprobantePagoJpa, Integer> {

    Optional<ComprobantePagoJpa> findByFkPago_Idpago(int idpago);

    List<ComprobantePagoJpa> findAllByFkPago_Idpago(int idpago);
}
