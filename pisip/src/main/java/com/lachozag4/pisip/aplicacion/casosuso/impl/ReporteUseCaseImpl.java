package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IReporteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.resultado.ReporteVentasDiaResultado;
import com.lachozag4.pisip.aplicacion.casosuso.resultado.ResumenProductoResultado;
import com.lachozag4.pisip.dominio.entidades.Pago;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReporteUseCaseImpl implements IReporteUseCase {

	private final IPedidoRepositorio pedidoRepositorio;
	private final IPagoRepositorio pagoRepositorio;

	@Override
	public ReporteVentasDiaResultado obtenerVentasDia(LocalDate fecha) {

		List<Pedido> pedidosDia = pedidoRepositorio.listarCompletadosPorFecha(fecha);
		List<Pago> pagosDia = pagoRepositorio.listarPorFecha(fecha);

		int numeroPedidos = pedidosDia.size();

		int totalProductos = pedidosDia.stream()
				.flatMap(p -> p.getDetalles().stream())
				.mapToInt(PedidoDetalle::getCantidad)
				.sum();

		double totalVentas = pedidosDia.stream()
				.flatMap(p -> p.getDetalles().stream())
				.mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
				.sum();

		double ticketPromedio = numeroPedidos > 0 ? totalVentas / numeroPedidos : 0.0;

		double totalEfectivo = pagosDia.stream()
				.filter(pago -> Pago.METODO_EFECTIVO.equalsIgnoreCase(pago.getMetodo()))
				.mapToDouble(Pago::getMonto)
				.sum();

		double totalTarjeta = pagosDia.stream()
				.filter(pago -> Pago.METODO_TARJETA.equalsIgnoreCase(pago.getMetodo()))
				.mapToDouble(Pago::getMonto)
				.sum();

		double totalTransferencias = pagosDia.stream()
				.filter(pago -> Pago.METODO_TRANSFERENCIA.equalsIgnoreCase(pago.getMetodo()))
				.mapToDouble(Pago::getMonto)
				.sum();

		double totalOtros = pagosDia.stream()
				.filter(pago -> !Pago.METODO_EFECTIVO.equalsIgnoreCase(pago.getMetodo())
						&& !Pago.METODO_TARJETA.equalsIgnoreCase(pago.getMetodo())
						&& !Pago.METODO_TRANSFERENCIA.equalsIgnoreCase(pago.getMetodo()))
				.mapToDouble(Pago::getMonto)
				.sum();

		// Resumen por producto usando merge para acumular cantidades
		Map<Integer, ResumenProductoResultado> resumenMap = new HashMap<>();
		for (Pedido pedido : pedidosDia) {
			for (PedidoDetalle detalle : pedido.getDetalles()) {
				Producto prod = detalle.getFkProducto();
				if (prod == null) {
					continue;
				}
				resumenMap.merge(
						prod.getIdproducto(),
						new ResumenProductoResultado(
								prod.getIdproducto(),
								prod.getNombre(),
								detalle.getCantidad(),
								detalle.getCantidad() * detalle.getPrecioUnitario()),
						(existente, nuevo) -> new ResumenProductoResultado(
								existente.idProducto(),
								existente.nombreProducto(),
								existente.cantidadVendida() + nuevo.cantidadVendida(),
								existente.totalVendido() + nuevo.totalVendido()));
			}
		}

		List<ResumenProductoResultado> productos = new ArrayList<>(resumenMap.values());
		productos.sort(Comparator.comparingDouble(ResumenProductoResultado::totalVendido).reversed());

		return new ReporteVentasDiaResultado(
				fecha,
				totalVentas,
				numeroPedidos,
				ticketPromedio,
				totalEfectivo,
				totalTarjeta,
				totalTransferencias,
				totalOtros,
				totalProductos,
				pedidosDia,
				productos);
	}
}
