package com.lachozag4.pisip.presentacion.mapeadores;

import org.springframework.stereotype.Component;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.presentacion.dto.request.CuentaRequestDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CuentaRequestMapper {

	private final IMesaUseCase mesaUseCase;
	private final IClienteUseCase clienteUseCase;

	public Cuenta toDomain(CuentaRequestDTO dto) {
		var mesa = mesaUseCase.obtenerPorId(dto.getIdMesa());
		var cliente = clienteUseCase.obtenerPorId(dto.getIdCliente());

		return new Cuenta(
			0,
			null,
			null,
			null,
			dto.getTotal(),
			mesa,
			cliente);
	}
}
