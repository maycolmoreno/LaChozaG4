package com.lachozag4.pisip.presentacion.dto.response;

public class DropboxEstadoResponseDTO {

    private boolean disponible;
    private String  mensaje;

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}