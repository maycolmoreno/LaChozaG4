package com.choza.consumochoza.controlador;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choza.consumochoza.modelo.dto.ClienteDTO;
import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.modelo.dto.PedidoDTO;
import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.modelo.dto.ReporteVentasDiaDTO;
import com.choza.consumochoza.service.IClienteService;
import com.choza.consumochoza.service.ICuentaService;
import com.choza.consumochoza.service.IPedidoService;
import com.choza.consumochoza.service.IProductoService;
import com.choza.consumochoza.service.IReporteService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST API para endpoints JSON usados por el POS y otros componentes.
 * Separado de los controladores MVC para respetar el principio de responsabilidad única (SRP).
 */
@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosApiControlador {

    private final IClienteService clienteService;
    private final IPedidoService pedidoService;
    private final ICuentaService cuentaService;
    private final IProductoService productoService;
    private final IReporteService reporteService;

    /**
     * Busca clientes por nombre o cédula para el autocompletado del POS.
     */
    @GetMapping("/clientes/buscar")
    public List<ClienteDTO> buscarClientes(@RequestParam String termino) {
        return clienteService.listarTodos().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(termino.toLowerCase()) ||
                             (c.getCedula() != null && c.getCedula().contains(termino)))
                .toList();
    }

    /**
     * Crea un cliente rápido desde el POS (modal de nuevo cliente).
     */
    @PostMapping("/clientes/crear-rapido")
    public ClienteDTO crearClienteRapido(@RequestBody ClienteDTO cliente) {
        cliente.setEstado(true);
        return clienteService.crear(cliente);
    }

    /**
     * Obtiene los pedidos activos (PENDIENTE, EN_COCINA o LISTO_PARA_ENTREGA) de una mesa específica.
     */
    @GetMapping("/mesas/{idMesa}/pedidos-activos")
    public List<PedidoDTO> pedidosActivosPorMesa(@PathVariable int idMesa) {
        return pedidoService.listarTodos().stream()
                .filter(p -> p.getMesa() != null
                        && p.getMesa().getIdmesa() == idMesa
                        && p.getEstado() != null
                        && ("PENDIENTE".equalsIgnoreCase(p.getEstado())
                            || "EN_COCINA".equalsIgnoreCase(p.getEstado())
                            || "LISTO_PARA_ENTREGA".equalsIgnoreCase(p.getEstado())))
                .toList();
    }

    /**
     * Lista las cuentas abiertas para mostrar en el POS al seleccionar mesa.
     */
    @GetMapping("/cuentas/abiertas")
    public List<CuentaDTO> listarCuentasAbiertas() {
        return cuentaService.listarAbiertas();
    }

    /**
     * Cambia el estado de un pedido vía AJAX (ej. enviar a cocina desde el POS).
     */
    @PatchMapping("/pedidos/{id}/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoPedido(
            @PathVariable int id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado requerido"));
        }
        try {
            PedidoDTO actualizado = pedidoService.cambiarEstado(id, nuevoEstado);
            Map<String, Object> resp = new HashMap<>();
            resp.put("idpedido", actualizado.getIdpedido());
            resp.put("estado", actualizado.getEstado());
            resp.put("mensaje", "Estado actualizado a " + nuevoEstado);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Devuelve productos activos con stock actualizado para refrescar el POS.
     */
    @GetMapping("/productos/activos")
    public List<ProductoDTO> listarProductosActivos() {
        return productoService.listarActivos();
    }

    /**
     * Devuelve la cantidad de pedidos en estado LISTO_PARA_ENTREGA.
     * Usado por el JS de notificaciones para alertar al mesero.
     */
    @GetMapping("/pedidos-listos-count")
    public Map<String, Object> contarPedidosListos() {
        long count = pedidoService.listarTodos().stream()
                .filter(p -> "LISTO_PARA_ENTREGA".equalsIgnoreCase(p.getEstado()))
                .count();
        return Map.of("count", count);
    }

    /**
     * Resumen rápido de ventas del día para el header del POS.
     */
    @GetMapping("/ventas-hoy")
    public Map<String, Object> ventasHoy() {
        Map<String, Object> resumen = new HashMap<>();
        try {
            ReporteVentasDiaDTO reporte = reporteService.obtenerVentasDia(LocalDate.now());
            resumen.put("totalVentas", reporte != null ? reporte.getTotalVentas() : 0);
            resumen.put("numeroPedidos", reporte != null ? reporte.getNumeroPedidos() : 0);
            resumen.put("ticketPromedio", reporte != null ? reporte.getTicketPromedio() : 0);
        } catch (Exception e) {
            resumen.put("totalVentas", 0);
            resumen.put("numeroPedidos", 0);
            resumen.put("ticketPromedio", 0);
        }
        return resumen;
    }
}
