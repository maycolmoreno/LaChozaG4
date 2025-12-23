package com.lachozag4.pisip.aplicacion.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> manejarValidaciones(MethodArgumentNotValidException ex) {

		Map<String, String> errores = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
	}

	// ===============================
	// 2️⃣ STOCK INSUFICIENTE
	// ===============================
	@ExceptionHandler(StockInsuficienteException.class)
	public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(StockInsuficienteException ex) {

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("mensaje", ex.getMessage());
		body.put("codigo", "STOCK_ERROR");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	// ===============================
	// 3️⃣ MESA OCUPADA
	// ===============================
	@ExceptionHandler(MesaOcupadaException.class)
	public ResponseEntity<Map<String, Object>> manejarMesaOcupada(MesaOcupadaException ex) {

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("mensaje", "La mesa ya tiene un pedido activo.");
		body.put("codigo", "MESA_OCUPADA");

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	// ===============================
	// 4️⃣ ERROR GENERAL (fallback)
	// ===============================
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> manejarErroresGenerales(Exception ex) {

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("mensaje", "Ocurrió un error interno en el sistema.");
		body.put("detalle", ex.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}
