package com.choza.consumochoza.service;

import java.util.List;

import com.choza.consumochoza.modelo.dto.CuentaDTO;

public interface ICuentaService {

    List<CuentaDTO> listarTodas();

    List<CuentaDTO> listarAbiertas();

    CuentaDTO obtenerPorId(int id);

    CuentaDTO crearCuenta(int idMesa, int idCliente);

    CuentaDTO agregarPedido(int idCuenta, int idPedido);

    CuentaDTO cambiarEstado(int idCuenta, String estado);
}
