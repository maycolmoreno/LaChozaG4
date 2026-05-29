package com.choza.consumochoza.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.choza.consumochoza.modelo.dto.ComprobanteDTO;
import com.choza.consumochoza.modelo.dto.DropboxEstadoDTO;
import com.choza.consumochoza.modelo.dto.PagoDTO;

public interface IPagoService {

    PagoDTO registrarPago(int idCuenta, double monto, String metodo, String referencia, String usuario);

    List<PagoDTO> listarPorCuenta(int idCuenta);

    ComprobanteDTO subirComprobante(int idCuenta, int idPago, MultipartFile archivo, String usuario);

    ComprobanteDTO obtenerComprobante(int idCuenta, int idPago);

    DropboxEstadoDTO obtenerEstadoDropbox(int idCuenta);
}
