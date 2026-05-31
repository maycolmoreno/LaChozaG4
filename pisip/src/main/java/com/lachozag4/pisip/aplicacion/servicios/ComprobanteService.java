package com.lachozag4.pisip.aplicacion.servicios;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lachozag4.pisip.dominio.entidades.ComprobantePago;
import com.lachozag4.pisip.dominio.servicios.IDropboxService;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComprobantePagoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PagoJpa;
import com.lachozag4.pisip.infraestructura.repositorios.IComprobantePagoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPagoJpaRepositorio;

@Service
public class ComprobanteService {

    private static final long TAMANO_MAX_BYTES = 5L * 1024 * 1024;

    private final IComprobantePagoJpaRepositorio comprobanteRepositorio;
    private final IPagoJpaRepositorio pagoRepositorio;
    private final IDropboxService dropboxService;

    public ComprobanteService(IComprobantePagoJpaRepositorio comprobanteRepositorio,
            IPagoJpaRepositorio pagoRepositorio,
            IDropboxService dropboxService) {
        this.comprobanteRepositorio = comprobanteRepositorio;
        this.pagoRepositorio = pagoRepositorio;
        this.dropboxService = dropboxService;
    }

    public ComprobantePago subirComprobante(int idpago, MultipartFile archivo, String usuario) {
        validarArchivo(archivo);

        var nombreOriginal = archivo.getOriginalFilename();
        dropboxService.validarExtension(nombreOriginal);

        PagoJpa pago = pagoRepositorio.findById(idpago)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado: " + idpago));

        comprobanteRepositorio.findByFkPago_Idpago(idpago).ifPresent(existente -> {
            throw new IllegalStateException("El pago ya tiene un comprobante registrado.");
        });

        var nombreDropbox = construirNombreDropbox(idpago, nombreOriginal);
        String rutaDropbox = null;

        try {
            rutaDropbox = dropboxService.subirArchivo(nombreDropbox, archivo.getInputStream(), archivo.getSize());
            var urlDropbox = dropboxService.obtenerUrlPublica(rutaDropbox);

            var entity = new ComprobantePagoJpa();
            entity.setFkPago(pago);
            entity.setNombreArchivo(nombreOriginal);
            entity.setRutaRelativa(rutaDropbox);
            entity.setRutaDropbox(rutaDropbox);
            entity.setUrlDropbox(urlDropbox);
            entity.setContentType(archivo.getContentType());
            entity.setTamano(archivo.getSize());
            entity.setUsuarioRegistro(usuario);
            entity.setFechaSubida(LocalDateTime.now());

            return toDomain(comprobanteRepositorio.save(entity));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer el comprobante.", ex);
        } catch (RuntimeException ex) {
            eliminarArchivoSubidoSiAplica(rutaDropbox);
            throw ex;
        }
    }

    public ComprobantePago buscarPorPago(int idpago) {
        return comprobanteRepositorio.findByFkPago_Idpago(idpago)
                .map(this::toDomain)
                .orElse(null);
    }

    public ComprobantePago obtenerPorPago(int idpago) {
        return comprobanteRepositorio.findByFkPago_Idpago(idpago)
                .map(this::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("No existe comprobante para el pago: " + idpago));
    }

    public void eliminarComprobante(int idpago) {
        var comprobante = comprobanteRepositorio.findByFkPago_Idpago(idpago)
                .orElseThrow(() -> new IllegalArgumentException("No existe comprobante para el pago: " + idpago));

        try {
            if (comprobante.getRutaDropbox() != null && !comprobante.getRutaDropbox().isBlank()) {
                dropboxService.eliminarArchivo(comprobante.getRutaDropbox());
            }
        } catch (IDropboxService.DropboxException ex) {
            // El pago no debe quedar bloqueado por una falla temporal al borrar en Dropbox.
        }

        comprobanteRepositorio.delete(comprobante);
    }

    public void validarConexionDropbox() {
        dropboxService.validarConexion();
    }

    private static void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El comprobante es obligatorio.");
        }
        if (archivo.getSize() > TAMANO_MAX_BYTES) {
            throw new IllegalArgumentException("El comprobante no puede superar 5 MB.");
        }

        var contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Use una imagen JPG o PNG.");
        }
    }

    private static String construirNombreDropbox(int idpago, String nombreOriginal) {
        var extension = "";
        var punto = nombreOriginal == null ? -1 : nombreOriginal.lastIndexOf('.');
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto).toLowerCase();
        }
        return "pago_" + idpago + "_" + UUID.randomUUID() + extension;
    }

    private void eliminarArchivoSubidoSiAplica(String rutaDropbox) {
        if (rutaDropbox == null || rutaDropbox.isBlank()) {
            return;
        }

        try {
            dropboxService.eliminarArchivo(rutaDropbox);
        } catch (IDropboxService.DropboxException ignored) {
            // La operacion principal debe conservar el error original.
        }
    }

    private ComprobantePago toDomain(ComprobantePagoJpa entity) {
        return new ComprobantePago(
                entity.getIdcomprobante(),
                entity.getFkPago() != null ? entity.getFkPago().getIdpago() : 0,
                entity.getNombreArchivo(),
                entity.getRutaRelativa(),
                entity.getRutaDropbox(),
                entity.getUrlDropbox(),
                entity.getContentType(),
                entity.getTamano(),
                entity.getUsuarioRegistro(),
                entity.getFechaSubida());
    }
}
