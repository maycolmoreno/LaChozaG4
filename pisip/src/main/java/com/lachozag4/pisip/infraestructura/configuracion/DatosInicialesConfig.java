package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;

@Component
public class DatosInicialesConfig implements CommandLineRunner {

	private final IUsuarioUseCase usuarioUseCase;

	public DatosInicialesConfig(IUsuarioUseCase usuarioUseCase) {
		this.usuarioUseCase = usuarioUseCase;
	}

	@Override
	public void run(String... args) throws Exception {
		// Ya no se crea un administrador por defecto.
		// El primer usuario administrador se registra desde la interfaz
		// cuando la tabla de usuarios está vacía.
	}
}
