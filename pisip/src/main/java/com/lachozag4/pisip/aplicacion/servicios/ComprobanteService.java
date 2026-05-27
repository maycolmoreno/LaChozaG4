package com.lachozag4.pisip.aplicacion.servicios;

import com.lachozag4.pisip.dominio.entidades.ComprobantePago;
import com.lachozag4.pisip.dominio.servicios.IDropboxService;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComprobantePagoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PagoJpa;
import com.lachozag4.pisip.infraestructura.repositorios.IComprobantePagoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPagoJpaRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: subir, consultar y eliminar comprobantes de pago.
 * <p>
 * Los archivos se almacenan en <strong>Dropbox</strong> bajo la ruta configurada
 * en {@code dropbox.folder-root} (por defecto {@code /LaChoza/Comprobantes}).
 * En la base de datos solo se persiste la URL pública y la ruta en Dropbox;
 * nunca se guarda el binario en la BD.
 * <p>
 * <strong>Regla de negocio:</strong> un pago puede tener a lo sumo un comprobante.
 * Si se intenta subir un segundo comprobante al mismo pago se lanza excepción.
 */
@Service
public class ComprobanteService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteService.class);

    private static final List<String> TIPOS_PERMITIDOS =
            List.of("image/jpeg", "image/png", "image/webp");
    private static final long TAMANO_MAX = 5L * 1024 * 1024; // 5 MB

    private final IComprobantePagoJpaRepositorio comprobanteRepo;
    private final IPagoJpaRepositorio            pagoRepo;
    private final IDropboxService                dropboxService;

    public ComprobanteService(IComprobantePagoJpaRepositorio comprobanteRepo,
                              IPagoJpaRepositorio pagoRepo,
                              IDropboxService dropboxService) {
        this.comprobanteRepo = comprobanteRepo;
        this.pagoRepo        = pagoRepo;
        this.dropboxService  = dropboxService;
    }

    // ─── Subida ──────────────────────────────────────────────────────────────────

    /**
     * Sube el comprobante a Dropbox y persiste la URL en la base de datos.
     *
     * @param idpago   ID del pago al que se adjunta
     * @param archivo  Multipart con la imagen (jpg/jpeg/png, máx 5 MB)
     * @param usuario  Nombre de usuario que registra el comprobante
     * @return Entidad de dominio con la URL pública de Dropbox
     */
    public ComprobantePago subirComprobante(int idpago, MultipartFile archivo, String usuario) {
        validarArchivo(archivo);
        dropboxService.validarExtension(archivo.getOriginalFilename());

        PagoJpa pago = pagoRepo.findById(idpago)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado: " + idpago));

        // Regla: un pago solo puede tener un comprobante
        comprobanteRepo.findByFkPago_Idpago(idpago).ifPresent(c -> {
            throw new IllegalStateException(
                    "El pago #" + idpago + " ya tiene un comprobante registrado. " +
                    "Elimine el anterior antes de subir uno nuevo.");
        });

        String nombreUnico = generarNombreUnico(idpago, archivo.getOriginalFilename());

        // ── 1. Subir a Dropbox ───────────────────────────────────────────────
        String rutaDropbox;
        try {
            rutaDropbox = dropboxService.subirArchivo(nombreUnico, archivo.getInputStream(), archivo.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo del comprobante", e);
        }

        // ── 2. Obtener URL pública ────────────────────────────────────────────
        String urlDropbox = dropboxService.obtenerUrlPublica(rutaDropbox);
        log.info("[ComprobanteService] Comprobante subido a Dropbox. Pago={} URL={}", idpago, urlDropbox);

        // ── 3. Persistir en BD ───────────────────────────────────────────────
        ComprobantePagoJpa entidad = new ComprobantePagoJpa();
        entidad.setFkPago(pago);
        entidad.setNombreArchivo(archivo.getOriginalFilename() != null
                ? archivo.getOriginalFilename() : nombreUnico);
        entidad.setRutaRelativa(rutaDropbox);   // reutilizamos columna existente para path Dropbox
        entidad.setRutaDropbox(rutaDropbox);
        entidad.setUrlDropbox(urlDropbox);
        entidad.setContentType(archivo.getContentType());
        entidad.setTamano(archivo.getSize());
        entidad.setUsuarioRegistro(usuario);
        entidad.setFechaSubida(LocalDateTime.now());

        return toDominio(comprobanteRepo.save(entidad));
    }

    // ─── Consulta ────────────────────────────────────────────────────────────────

    /**
     * Obtiene el comprobante asociado a un pago.
     *
     * @param idpago ID del pago
     * @return Entidad de dominio con URL pública de Dropbox
     */
    public ComprobantePago obtenerPorPago(int idpago) {
        return comprobanteRepo.findByFkPago_Idpago(idpago)
                .map(this::toDominio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe comprobante para el pago #" + idpago));
    }

    // ─── Eliminación ─────────────────────────────────────────────────────────────

    /**
     * Elimina el comprobante de Dropbox y de la base de datos.
     *
     * @param idpago ID del pago cuyo comprobante se elimina
     */
    public void eliminarComprobante(int idpago) {
        ComprobantePagoJpa entidad = comprobanteRepo.findByFkPago_Idpago(idpago)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe comprobante para el pago #" + idpago));

        String rutaDropbox = entidad.getRutaDropbox() != null
                ? entidad.getRutaDropbox() : entidad.getRutaRelativa();

        // Eliminar de Dropbox (registramos error pero no bloqueamos la eliminación de BD)
        if (rutaDropbox != null && !rutaDropbox.isBlank()) {
            try {
                dropboxService.eliminarArchivo(rutaDropbox);
            } catch (Exception e) {
                log.warn("[ComprobanteService] Error al eliminar de Dropbox (ruta={}): {}. " +
                         "Se elimina el registro de BD de todas formas.", rutaDropbox, e.getMessage());
            }
        }

        comprobanteRepo.delete(entidad);
        log.info("[ComprobanteService] Comprobante eliminado. Pago={}", idpago);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo es obligatorio");
        }
        if (archivo.getSize() > TAMANO_MAX) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 5 MB");
        }
        String tipo = archivo.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Tipo de archivo no permitido. Use: JPEG, PNG o WebP");
        }
    }

    /**
     * Genera un nombre único con formato: {@code yyyyMMdd_pago_{id}_{uuid8}.ext}
     * para evitar colisiones en Dropbox.
     */
    private String generarNombreUnico(int idpago, String nombreOriginal) {
        String ext = "jpg";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            ext = nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1).toLowerCase();
        }
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String guid8 = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return fecha + "_pago_" + idpago + "_" + guid8 + "." + ext;
    }

    private ComprobantePago toDominio(ComprobantePagoJpa e) {
        return new ComprobantePago(
                e.getIdcomprobante(),
                e.getFkPago().getIdpago(),
                e.getNombreArchivo(),
                e.getRutaRelativa(),
                e.getRutaDropbox(),
                e.getUrlDropbox(),
                e.getContentType(),
                e.getTamano(),
                e.getUsuarioRegistro(),
                e.getFechaSubida()
        );
    }
}
