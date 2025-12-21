package com.lachozag4.pisip.aplicacion.casosuso.entradas;

public interface GestionarConfiguracionPort {
    void actualizarPorcentajeIva(Double nuevoIva);
    Double obtenerIvaActual();
}