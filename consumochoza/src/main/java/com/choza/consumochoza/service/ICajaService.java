package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.CajaTurnoDTO;

public interface ICajaService {

    CajaTurnoDTO abrirCaja(double montoInicial, String usuarioApertura, String observaciones);

    CajaTurnoDTO obtenerCajaAbierta();

    CajaTurnoDTO cerrarCaja(double montoDeclaradoCierre, String usuarioCierre, String observaciones);

    List<CajaTurnoDTO> listarCajas();
}
