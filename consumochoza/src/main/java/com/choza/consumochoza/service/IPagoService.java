package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.PagoDTO;

public interface IPagoService {

    PagoDTO registrarPago(int idCuenta, double monto, String metodo, String referencia, String usuario);

    List<PagoDTO> listarPorCuenta(int idCuenta);
}
