package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import com.lachozag4.pisip.dominio.entidades.Producto;
import java.util.List;

public interface ConsultarInventarioPort {
    List<Producto> obtenerProductosConBajoStock();
    void actualizarStock(Long productoId, Integer cantidadARestar);
}