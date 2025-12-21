package com.lachozag4.pisip.aplicacion.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Atrapa el error específico de stock
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Object> manejarStockInsuficiente(StockInsuficienteException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("mensaje", ex.getMessage());
        body.put("codigo", "STOCK_ERROR");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 2. Atrapa el error de mesa ocupada
    @ExceptionHandler(MesaOcupadaException.class)
    public ResponseEntity<Object> manejarMesaOcupada(MesaOcupadaException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("mensaje", "La mesa ya tiene un pedido activo.");
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 3. Atrapa cualquier otro error inesperado (Bases de datos, etc)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarErroresGenerales(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("mensaje", "Ocurrió un error interno en el sistema.");
        body.put("detalle", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}