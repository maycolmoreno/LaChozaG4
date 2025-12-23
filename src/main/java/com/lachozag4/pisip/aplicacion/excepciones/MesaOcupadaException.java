package com.lachozag4.pisip.aplicacion.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Devuelve un error 409 (Conflicto)
public class MesaOcupadaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MesaOcupadaException(Integer numeroMesa) {
		super("La mesa número " + numeroMesa + " ya se encuentra ocupada.");
	}
}