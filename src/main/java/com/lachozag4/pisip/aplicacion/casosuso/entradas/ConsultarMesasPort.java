package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import java.util.List;

public interface ConsultarMesasPort {
    List<Mesa> listarMesas();
}
