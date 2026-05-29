package com.lachozag4.pisip.presentacion.excepciones;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.servicios.IDropboxService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("NotFoundException: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("BusinessException: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage(), request);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        var mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> {
                    log.warn("Validation error - Field: {}, Message: {}", error.getField(), error.getDefaultMessage());
                    return error.getField() + ": " + error.getDefaultMessage();
                })
                .orElse("Datos invalidos.");
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", mensaje, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("DataIntegrityViolationException: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY", mensajeIntegridad(ex), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(IDropboxService.DropboxException.class)
    public ResponseEntity<ApiError> handleDropbox(IDropboxService.DropboxException ex, HttpServletRequest request) {
        log.warn("DropboxException: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, "DROPBOX_UNAVAILABLE", ex.getMessage(), request);
    }

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex, HttpServletRequest request) {
        log.warn("AccessDenied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "No tiene permisos para acceder a este recurso.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ha ocurrido un error interno. Contacte al administrador.", request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String codigo, String mensaje,
            HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ApiError(codigo, mensaje, LocalDateTime.now(), request.getRequestURI()));
    }

    private String mensajeIntegridad(DataIntegrityViolationException ex) {
        String detalle = ((ex.getMessage() == null ? "" : ex.getMessage()) + " "
                + (ex.getMostSpecificCause() == null ? "" : ex.getMostSpecificCause().getMessage()))
                .toLowerCase();

        if (detalle.contains("uq_caja_turno_abierta")) {
            return "Ya existe una caja abierta. Cierre la caja actual antes de abrir otra.";
        }
        if (detalle.contains("uq_cuenta_abierta_mesa")) {
            return "La mesa ya tiene una cuenta abierta. Use la cuenta existente.";
        }

        return "No se puede realizar la operacion porque existen registros relacionados.";
    }

    public record ApiError(String codigo, String mensaje, LocalDateTime timestamp, String path) {}
}
