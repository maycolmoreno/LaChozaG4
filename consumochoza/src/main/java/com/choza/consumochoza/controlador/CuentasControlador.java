package com.choza.consumochoza.controlador;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.modelo.dto.PagoDTO;
import com.choza.consumochoza.service.IClienteService;
import com.choza.consumochoza.service.ICuentaService;
import com.choza.consumochoza.service.IPagoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentasControlador {

    private final ICuentaService cuentaService;
    private final IPagoService pagoService;
    private final IClienteService clienteService;

    @GetMapping
    public String listarCuentas(
            @org.springframework.web.bind.annotation.RequestParam(name = "estado", required = false) String estado,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaDesde", required = false) String fechaDesde,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaHasta", required = false) String fechaHasta,
            Model model) {

        // Por defecto mostrar solo cuentas ABIERTAS (pendientes de pago)
        if (estado == null || estado.isBlank()) {
            estado = "ABIERTA";
        }

        List<CuentaDTO> cuentas = cuentaService.listarTodas();

        // Filtro por estado (ABIERTA, PAGADA, ANULADA)
        if (estado != null && !estado.isBlank() && !"TODAS".equalsIgnoreCase(estado)) {
            String estadoUpper = estado.toUpperCase();
            cuentas = cuentas.stream()
                    .filter(c -> c.getEstado() != null && c.getEstado().equalsIgnoreCase(estadoUpper))
                    .toList();
        }

        // Filtro por rango de fechas de apertura
        LocalDate desde = null;
        LocalDate hasta = null;
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            desde = LocalDate.parse(fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isBlank()) {
            hasta = LocalDate.parse(fechaHasta);
        }
        if (desde != null || hasta != null) {
            LocalDate finalDesde = desde;
            LocalDate finalHasta = hasta;
            cuentas = cuentas.stream()
                    .filter(c -> {
                        if (c.getFechaApertura() == null) return false;
                        LocalDate f = c.getFechaApertura().toLocalDate();
                        boolean okDesde = finalDesde == null || !f.isBefore(finalDesde);
                        boolean okHasta = finalHasta == null || !f.isAfter(finalHasta);
                        return okDesde && okHasta;
                    })
                    .toList();
        }

        cuentas = cuentas.stream()
                .sorted(Comparator
                        .comparing(CuentaDTO::getFechaApertura, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed()
                        .thenComparing(CuentaDTO::getIdcuenta, Comparator.reverseOrder()))
                .toList();

        model.addAttribute("cuentas", cuentas);
        model.addAttribute("filtroEstado", (estado == null || estado.isBlank()) ? "TODAS" : estado.toUpperCase());
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "Cuenta/Cuentas";
    }

    @PostMapping("/cobrar/{id}")
    public String cobrarCuenta(
            @PathVariable("id") int idcuenta,
            @org.springframework.web.bind.annotation.RequestParam(name = "origen", required = false) String origen,
            RedirectAttributes redirectAttributes) {
        try {
            CuentaDTO cuenta = cuentaService.obtenerPorId(idcuenta);
            if (!"ABIERTA".equalsIgnoreCase(cuenta.getEstado())) {
                redirectAttributes.addFlashAttribute("mensajeError", "Solo se pueden cobrar cuentas abiertas.");
            } else {
                List<PagoDTO> pagos = pagoService.listarPorCuenta(idcuenta);
                double totalPagado = pagos.stream().mapToDouble(PagoDTO::getMonto).sum();
                double saldoPendiente = cuenta.getTotal() - totalPagado;

                if (saldoPendiente <= 0) {
                    redirectAttributes.addFlashAttribute("mensajeError",
                            "La cuenta ya no tiene saldo pendiente.");
                } else {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    String username = auth != null ? auth.getName() : "sistema";

                    pagoService.registrarPago(
                            idcuenta,
                            saldoPendiente,
                            "EFECTIVO",
                            "COBRO-RAPIDO",
                            username);
                    redirectAttributes.addFlashAttribute("mensajeExito",
                            "Cuenta cobrada correctamente y pago registrado.");
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cobrar la cuenta: " + e.getMessage());
        }
        // Si viene desde la lista de pedidos, regresar allí; de lo contrario, a cuentas
        if (origen != null && origen.equalsIgnoreCase("pedidos")) {
            return "redirect:/pedidos";
        }
        return "redirect:/cuentas";
    }

    @PostMapping("/anular/{id}")
    public String anularCuenta(@PathVariable("id") int idcuenta, RedirectAttributes redirectAttributes) {
        try {
            cuentaService.cambiarEstado(idcuenta, "ANULADA");
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta anulada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo anular la cuenta: " + e.getMessage());
        }
        return "redirect:/cuentas";
    }

    @PostMapping("/{id}/asignar-cliente")
    public String asignarCliente(
            @PathVariable("id") int idcuenta,
            @RequestParam("idCliente") int idCliente,
            @RequestParam(name = "origen", required = false) String origen,
            RedirectAttributes redirectAttributes) {
        try {
            cuentaService.asignarCliente(idcuenta, idCliente);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente asignado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo asignar el cliente: " + e.getMessage());
        }
        String redirect = "redirect:/cuentas/" + idcuenta + "/pagos";
        if (origen != null && !origen.isBlank()) redirect += "?origen=" + origen;
        return redirect;
    }

    @GetMapping("/{id}/pagos")
    public String verPagosCuenta(
            @PathVariable("id") int idcuenta,
            @RequestParam(name = "origen", required = false) String origen,
            Model model) {
        CuentaDTO cuenta = cuentaService.obtenerPorId(idcuenta);
        List<PagoDTO> pagos = pagoService.listarPorCuenta(idcuenta).stream()
                .sorted(Comparator
                        .comparing(PagoDTO::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed()
                        .thenComparing(PagoDTO::getIdpago, Comparator.reverseOrder()))
                .toList();

        double totalPagado = pagos.stream().mapToDouble(PagoDTO::getMonto).sum();
        double saldoPendiente = cuenta.getTotal() - totalPagado;
        if (saldoPendiente < 0) {
            saldoPendiente = 0.0;
        }

        model.addAttribute("cuenta", cuenta);
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagado", totalPagado);
        model.addAttribute("saldoPendiente", saldoPendiente);
        model.addAttribute("origen", origen);
        model.addAttribute("clientes", clienteService.listarTodos());
        return "Cuenta/PagosCuenta";
    }

    @PostMapping("/{id}/pagos")
    public String registrarPago(@PathVariable("id") int idcuenta,
            @RequestParam("monto") double monto,
            @RequestParam("metodo") String metodo,
            @RequestParam(name = "referencia", required = false) String referencia,
            @RequestParam(name = "montoRecibido", required = false) Double montoRecibido,
            @RequestParam(name = "canalTransferencia", required = false) String canalTransferencia,
            @RequestParam(name = "nroOperacion", required = false) String nroOperacion,
            @RequestParam(name = "origen", required = false) String origen,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "sistema";

        try {
            String metodoNormalizado = metodo == null ? "" : metodo.trim().toUpperCase();
            String referenciaFinal = referencia == null ? "" : referencia.trim();

            if ("EFECTIVO".equals(metodoNormalizado) && montoRecibido != null) {
                if (montoRecibido < monto) {
                    throw new IllegalArgumentException("El valor recibido no puede ser menor al monto a cobrar.");
                }
                double cambio = montoRecibido - monto;
                referenciaFinal = String.format("RECIBIDO: %.2f | CAMBIO: %.2f", montoRecibido, cambio);
            }

            if ("TRANSFERENCIA".equals(metodoNormalizado)) {
                if (canalTransferencia == null || canalTransferencia.isBlank()) {
                    throw new IllegalArgumentException("Debe seleccionar la plataforma de transferencia.");
                }
                String canalNormalizado = canalTransferencia.trim().toUpperCase();
                String canalDescripcion;
                if (canalNormalizado.contains("DE_UNA")
                        || canalNormalizado.contains("UNA_BANCO_PICHINCHA")
                        || "UNA".equals(canalNormalizado)) {
                    canalDescripcion = "DE UNA - BANCO PICHINCHA";
                } else {
                    canalDescripcion = "AHORITA - BANCO DE LOJA";
                }

                StringBuilder ref = new StringBuilder(canalDescripcion);
                if (nroOperacion != null && !nroOperacion.isBlank()) {
                    ref.append(" | OP: ").append(nroOperacion.trim());
                }
                referenciaFinal = ref.toString();
            }

            pagoService.registrarPago(idcuenta, monto, metodoNormalizado, referenciaFinal, username);
            redirectAttributes.addFlashAttribute("mensajeExito", "Pago registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo registrar el pago: " + e.getMessage());
        }
        if (origen != null && !origen.isBlank()) {
            return "redirect:/cuentas/" + idcuenta + "/pagos?origen=" + origen;
        }
        return "redirect:/cuentas/" + idcuenta + "/pagos";
    }
}
