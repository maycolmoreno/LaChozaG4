package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pago;
import org.springframework.web.multipart.MultipartFile;

public interface IPagoUseCase {

	Pago registrarPago(int idcuenta, double monto, String metodo, String referencia, String usuario);

	Pago registrarPagoConComprobante(int idcuenta, double monto, String metodo, String referencia, String usuario,
			MultipartFile archivo);

	List<Pago> listarPorCuenta(int idcuenta);

	double totalPagadoCuenta(int idcuenta);

	double saldoPendienteCuenta(int idcuenta);
}
