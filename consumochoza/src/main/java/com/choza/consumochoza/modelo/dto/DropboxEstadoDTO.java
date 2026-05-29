package com.choza.consumochoza.modelo.dto;

public class DropboxEstadoDTO {

    private boolean disponible;
    private String  mensaje;

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}