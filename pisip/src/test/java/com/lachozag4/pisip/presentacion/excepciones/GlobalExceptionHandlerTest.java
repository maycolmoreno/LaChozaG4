package com.lachozag4.pisip.presentacion.excepciones;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void dataIntegrityDevuelveMensajeClaroParaCajaAbiertaDuplicada() {
        var ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"uq_caja_turno_abierta\"");

        var response = handler.handleDataIntegrity(ex, request("/api/caja/apertura"));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("DATA_INTEGRITY");
        assertThat(response.getBody().mensaje()).contains("Ya existe una caja abierta");
        assertThat(response.getBody().path()).isEqualTo("/api/caja/apertura");
    }

    @Test
    void dataIntegrityDevuelveMensajeClaroParaCuentaAbiertaDuplicadaPorMesa() {
        var ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"uq_cuenta_abierta_mesa\"");

        var response = handler.handleDataIntegrity(ex, request("/api/cuentas"));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("DATA_INTEGRITY");
        assertThat(response.getBody().mensaje()).contains("mesa ya tiene una cuenta abierta");
        assertThat(response.getBody().path()).isEqualTo("/api/cuentas");
    }

    private MockHttpServletRequest request(String uri) {
        return new MockHttpServletRequest("POST", uri);
    }
}
