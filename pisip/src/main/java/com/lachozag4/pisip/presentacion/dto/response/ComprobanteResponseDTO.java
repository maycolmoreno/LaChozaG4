package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

public class ComprobanteResponseDTO {

    private int           idcomprobante;
    private int           idpago;
    private String        nombreArchivo;
    private String        urlDescarga;    // URL relativa para descargar la imagen
    private String        contentType;
    private long          tamano;
    private String        usuarioRegistro;
    private LocalDateTime fechaSubida;

    // ─── Getters / Setters ─────────────────────────────────────────────────────
    public int           getIdcomprobante()   { return idcomprobante; }
    public void          setIdcomprobante(int v) { this.idcomprobante = v; }
    public int           getIdpago()          { return idpago; }
    public void          setIdpago(int v)     { this.idpago = v; }
    public String        getNombreArchivo()   { return nombreArchivo; }
    public void          setNombreArchivo(String v) { this.nombreArchivo = v; }
    public String        getUrlDescarga()     { return urlDescarga; }
    public void          setUrlDescarga(String v) { this.urlDescarga = v; }
    public String        getContentType()     { return contentType; }
    public void          setContentType(String v) { this.contentType = v; }
    public long          getTamano()          { return tamano; }
    public void          setTamano(long v)    { this.tamano = v; }
    public String        getUsuarioRegistro() { return usuarioRegistro; }
    public void          setUsuarioRegistro(String v) { this.usuarioRegistro = v; }
    public LocalDateTime getFechaSubida()     { return fechaSubida; }
    public void          setFechaSubida(LocalDateTime v) { this.fechaSubida = v; }
}
