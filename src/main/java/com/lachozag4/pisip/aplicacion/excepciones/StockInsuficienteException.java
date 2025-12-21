package com.lachozag4.pisip.aplicacion.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockInsuficienteException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StockInsuficienteException() {
        super("No hay suficiente stock para preparar este plato de ceviche.");
    }
}