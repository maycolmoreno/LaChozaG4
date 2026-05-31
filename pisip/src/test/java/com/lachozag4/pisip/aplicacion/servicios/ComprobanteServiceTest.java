package com.lachozag4.pisip.aplicacion.servicios;

import com.lachozag4.pisip.dominio.entidades.ComprobantePago;
import com.lachozag4.pisip.dominio.servicios.IDropboxService;
import com.lachozag4.pisip.dominio.servicios.IDropboxService.DropboxException;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComprobantePagoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PagoJpa;
import com.lachozag4.pisip.infraestructura.repositorios.IComprobantePagoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPagoJpaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComprobanteService — tests unitarios")
class ComprobanteServiceTest {

    @Mock IComprobantePagoJpaRepositorio comprobanteRepo;
    @Mock IPagoJpaRepositorio            pagoRepo;
    @Mock IDropboxService                dropboxService;
    @Mock MultipartFile                  archivo;

    ComprobanteService service;

    @BeforeEach
    void setUp() {
        service = new ComprobanteService(comprobanteRepo, pagoRepo, dropboxService);
    }

    // ─── subirComprobante ────────────────────────────────────────────────────────

    @Test
    @DisplayName("subirComprobante — éxito: persiste y devuelve ComprobantePago con URL Dropbox")
    void subirComprobante_exito() throws Exception {
        PagoJpa pago = new PagoJpa();
        // Simulamos que el pago existe
        when(pagoRepo.findById(1)).thenReturn(Optional.of(pago));
        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.empty());

        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getSize()).thenReturn(1024L);
        when(archivo.getOriginalFilename()).thenReturn("comp.jpg");
        when(archivo.getInputStream()).thenReturn(new ByteArrayInputStream("datos".getBytes()));

        doNothing().when(dropboxService).validarExtension(anyString());
        when(dropboxService.subirArchivo(anyString(), any(), anyLong()))
                .thenReturn("/LaChoza/Comprobantes/comp_uuid.jpg");
        when(dropboxService.obtenerUrlPublica(anyString()))
                .thenReturn("https://dl.dropboxusercontent.com/s/abc/comp.jpg");

        ComprobantePagoJpa savedJpa = new ComprobantePagoJpa();
        savedJpa.setIdcomprobante(99);
        savedJpa.setFkPago(pago);
        savedJpa.setNombreArchivo("comp.jpg");
        savedJpa.setRutaDropbox("/LaChoza/Comprobantes/comp_uuid.jpg");
        savedJpa.setUrlDropbox("https://dl.dropboxusercontent.com/s/abc/comp.jpg");
        savedJpa.setContentType("image/jpeg");
        savedJpa.setTamano(1024L);
        savedJpa.setUsuarioRegistro("cajero1");
        when(comprobanteRepo.save(any(ComprobantePagoJpa.class))).thenReturn(savedJpa);

        ComprobantePago result = service.subirComprobante(1, archivo, "cajero1");

        assertThat(result).isNotNull();
        assertThat(result.getUrlDropbox()).isEqualTo("https://dl.dropboxusercontent.com/s/abc/comp.jpg");
        assertThat(result.getRutaDropbox()).isEqualTo("/LaChoza/Comprobantes/comp_uuid.jpg");
        verify(comprobanteRepo).save(any(ComprobantePagoJpa.class));
    }

    @Test
    @DisplayName("subirComprobante — lanza excepción si ya existe un comprobante para el pago")
    void subirComprobante_duplicado() {
        PagoJpa pago = new PagoJpa();
        when(pagoRepo.findById(1)).thenReturn(Optional.of(pago));

        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getSize()).thenReturn(100L);
        when(archivo.getOriginalFilename()).thenReturn("dup.jpg");
        doNothing().when(dropboxService).validarExtension(anyString());

        // Ya existe un comprobante previo
        when(comprobanteRepo.findByFkPago_Idpago(1))
                .thenReturn(Optional.of(new ComprobantePagoJpa()));

        assertThatThrownBy(() -> service.subirComprobante(1, archivo, "cajero1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene un comprobante");

        verify(dropboxService, never()).subirArchivo(anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("subirComprobante — lanza excepción si el archivo supera 5 MB")
    void subirComprobante_archivoDemasiadoGrande() {
        when(archivo.getSize()).thenReturn(6L * 1024 * 1024);

        assertThatThrownBy(() -> service.subirComprobante(1, archivo, "cajero1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MB");

        verify(pagoRepo, never()).findById(anyInt());
    }

    @Test
    @DisplayName("subirComprobante — lanza excepción si el Content-Type no es imagen permitida")
    void subirComprobante_tipoInvalido() {
        when(archivo.getContentType()).thenReturn("application/pdf");
        when(archivo.getSize()).thenReturn(100L);

        assertThatThrownBy(() -> service.subirComprobante(1, archivo, "cajero1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo");

        verify(pagoRepo, never()).findById(anyInt());
    }

    @Test
    @DisplayName("subirComprobante — lanza excepción si el pago no existe")
    void subirComprobante_pagoNoExiste() {
        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getSize()).thenReturn(100L);
        when(archivo.getOriginalFilename()).thenReturn("foto.jpg");
        doNothing().when(dropboxService).validarExtension(anyString());
        when(pagoRepo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.subirComprobante(999, archivo, "cajero1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pago no encontrado");
    }

    @Test
    @DisplayName("subirComprobante — elimina archivo subido si falla al obtener URL publica")
    void subirComprobante_fallaUrlPublica_eliminaArchivoSubido() throws Exception {
        PagoJpa pago = new PagoJpa();
        when(pagoRepo.findById(1)).thenReturn(Optional.of(pago));
        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.empty());

        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getSize()).thenReturn(1024L);
        when(archivo.getOriginalFilename()).thenReturn("comp.jpg");
        when(archivo.getInputStream()).thenReturn(new ByteArrayInputStream("datos".getBytes()));

        doNothing().when(dropboxService).validarExtension(anyString());
        when(dropboxService.subirArchivo(anyString(), any(), anyLong()))
                .thenReturn("/LaChoza/Comprobantes/comp_uuid.jpg");
        when(dropboxService.obtenerUrlPublica("/LaChoza/Comprobantes/comp_uuid.jpg"))
                .thenThrow(new DropboxException("url error"));

        assertThatThrownBy(() -> service.subirComprobante(1, archivo, "cajero1"))
                .isInstanceOf(DropboxException.class)
                .hasMessageContaining("url error");

        verify(dropboxService).eliminarArchivo("/LaChoza/Comprobantes/comp_uuid.jpg");
        verify(comprobanteRepo, never()).save(any());
    }

    @Test
    @DisplayName("subirComprobante — elimina archivo subido si falla al guardar metadata")
    void subirComprobante_fallaGuardarMetadata_eliminaArchivoSubido() throws Exception {
        PagoJpa pago = new PagoJpa();
        when(pagoRepo.findById(1)).thenReturn(Optional.of(pago));
        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.empty());

        when(archivo.getContentType()).thenReturn("image/jpeg");
        when(archivo.getSize()).thenReturn(1024L);
        when(archivo.getOriginalFilename()).thenReturn("comp.jpg");
        when(archivo.getInputStream()).thenReturn(new ByteArrayInputStream("datos".getBytes()));

        doNothing().when(dropboxService).validarExtension(anyString());
        when(dropboxService.subirArchivo(anyString(), any(), anyLong()))
                .thenReturn("/LaChoza/Comprobantes/comp_uuid.jpg");
        when(dropboxService.obtenerUrlPublica("/LaChoza/Comprobantes/comp_uuid.jpg"))
                .thenReturn("https://dl.dropboxusercontent.com/s/abc/comp.jpg");
        when(comprobanteRepo.save(any(ComprobantePagoJpa.class)))
                .thenThrow(new IllegalStateException("db error"));

        assertThatThrownBy(() -> service.subirComprobante(1, archivo, "cajero1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("db error");

        verify(dropboxService).eliminarArchivo("/LaChoza/Comprobantes/comp_uuid.jpg");
    }

    // ─── eliminarComprobante ─────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarComprobante — elimina de Dropbox y de la BD")
    void eliminarComprobante_exito() {
        ComprobantePagoJpa jpa = new ComprobantePagoJpa();
        jpa.setIdcomprobante(5);
        jpa.setRutaDropbox("/LaChoza/Comprobantes/foto.jpg");

        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.of(jpa));
        doNothing().when(dropboxService).eliminarArchivo(anyString());

        assertThatNoException().isThrownBy(() -> service.eliminarComprobante(1));

        verify(dropboxService).eliminarArchivo("/LaChoza/Comprobantes/foto.jpg");
        verify(comprobanteRepo).delete(jpa);
    }

    @Test
    @DisplayName("eliminarComprobante — si no existe comprobante lanza IllegalArgumentException")
    void eliminarComprobante_noExiste() {
        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarComprobante(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe comprobante");

        verify(dropboxService, never()).eliminarArchivo(anyString());
    }

    @Test
    @DisplayName("eliminarComprobante — continúa y elimina BD aunque falle Dropbox")
    void eliminarComprobante_fallaDropboxPeroEliminaBD() {
        ComprobantePagoJpa jpa = new ComprobantePagoJpa();
        jpa.setRutaDropbox("/LaChoza/Comprobantes/foto.jpg");

        when(comprobanteRepo.findByFkPago_Idpago(1)).thenReturn(Optional.of(jpa));
        doThrow(new DropboxException("network error")).when(dropboxService).eliminarArchivo(anyString());

        // No debe lanzar excepción al caller — Dropbox falla silenciosamente
        assertThatNoException().isThrownBy(() -> service.eliminarComprobante(1));

        verify(comprobanteRepo).delete(jpa);
    }
}
