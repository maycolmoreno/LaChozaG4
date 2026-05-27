package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Comprobante de pago adjunto (imagen de transferencia, etc.).
 * Un pago puede tener cero o un comprobante.
 */
public class ComprobantePago implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int           idcomprobante;
    private final int           idpago;
    private final String        nombreArchivo;   // nombre original del archivo
    private final String        rutaRelativa;    // ruta local o path en Dropbox
    private final String        rutaDropbox;     // path completo en Dropbox
    private final String        urlDropbox;      // URL pública directa para MAUI
    private final String        contentType;     // MIME: image/jpeg, image/png, etc.
    private final long          tamano;          // bytes
    private final String        usuarioRegistro; // quien subió el comprobante
    private final LocalDateTime fechaSubida;

    public ComprobantePago(int idcomprobante, int idpago, String nombreArchivo,
                           String rutaRelativa, String rutaDropbox, String urlDropbox,
                           String contentType, long tamano,
                           String usuarioRegistro, LocalDateTime fechaSubida) {
        this.idcomprobante   = idcomprobante;
        this.idpago          = idpago;
        this.nombreArchivo   = nombreArchivo;
        this.rutaRelativa    = rutaRelativa;
        this.rutaDropbox     = rutaDropbox;
        this.urlDropbox      = urlDropbox;
        this.contentType     = contentType;
        this.tamano          = tamano;
        this.usuarioRegistro = usuarioRegistro;
        this.fechaSubida     = fechaSubida;
    }

    public int           getIdcomprobante()   { return idcomprobante; }
    public int           getIdpago()          { return idpago; }
    public String        getNombreArchivo()   { return nombreArchivo; }
    public String        getRutaRelativa()    { return rutaRelativa; }
    public String        getRutaDropbox()     { return rutaDropbox; }
    public String        getUrlDropbox()      { return urlDropbox; }
    public String        getContentType()     { return contentType; }
    public long          getTamano()          { return tamano; }
    public String        getUsuarioRegistro() { return usuarioRegistro; }
    public LocalDateTime getFechaSubida()     { return fechaSubida; }
}
