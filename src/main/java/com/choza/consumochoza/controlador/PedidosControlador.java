package com.choza.consumochoza.controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.choza.consumochoza.modelo.dto.PedidoDTO;
import com.choza.consumochoza.modelo.dto.PedidoDetalleDTO;
import com.choza.consumochoza.modelo.dto.PedidosPaginadosDTO;
import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.modelo.dto.UsuarioDTO;
import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.service.ICategoriaService;
import com.choza.consumochoza.service.IClienteService;
import com.choza.consumochoza.service.IMesaService;
import com.choza.consumochoza.service.IPedidoService;
import com.choza.consumochoza.service.IProductoService;
import com.choza.consumochoza.service.IUsuarioService;
import com.choza.consumochoza.service.ICuentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidosControlador {

    private final IPedidoService pedidoService;
    private final IUsuarioService usuarioService;
    private final IMesaService mesaService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final ICategoriaService categoriaService;
    private final ICuentaService cuentaService;

    @GetMapping
    public String listar(
            @org.springframework.web.bind.annotation.RequestParam(name = "estado", required = false) String estado,
            @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String q,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaDesde", required = false) String fechaDesde,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaHasta", required = false) String fechaHasta,
            @org.springframework.web.bind.annotation.RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Model model) {

        // Parsear fechas
        LocalDate desde = (fechaDesde != null && !fechaDesde.isBlank()) ? LocalDate.parse(fechaDesde) : null;
        LocalDate hasta = (fechaHasta != null && !fechaHasta.isBlank()) ? LocalDate.parse(fechaHasta) : null;

        // Delegar filtrado, ordenamiento y paginación al servicio (SRP)
        PedidosPaginadosDTO resultado = pedidoService.listarConFiltros(estado, q, desde, hasta, page, size);

        // Lógica específica de la vista: determinar qué pedidos muestran botón Cobrar
        List<CuentaDTO> cuentasAbiertas = cuentaService.listarAbiertas();
        Map<String, CuentaDTO> cuentasPorMesa = cuentasAbiertas.stream()
            .filter(c -> c.getMesa() != null && c.getCliente() != null)
            .collect(Collectors.toMap(
                    c -> c.getMesa().getIdmesa() + "-" + c.getCliente().getIdcliente(),
                    c -> c,
                    (c1, c2) -> c1));

        // Kanban + conteos: obtener TODOS los pedidos una sola vez
        List<PedidoDTO> todosLosPedidos = pedidoService.listarTodos();

        // Determinar por cada combinación mesa-cliente un único pedido con botón "Cobrar"
        // Usa todosLosPedidos para que funcione tanto en Kanban como en Tabla
        Map<String, Integer> pedidoCobroPorMesa = new HashMap<>();
        for (PedidoDTO p : todosLosPedidos) {
            if (p.getMesa() == null || p.getCliente() == null || p.getEstado() == null) {
                continue;
            }
            String clave = p.getMesa().getIdmesa() + "-" + p.getCliente().getIdcliente();
            if (!cuentasPorMesa.containsKey(clave)) {
                continue;
            }
            if (!"COMPLETADO".equalsIgnoreCase(p.getEstado())) {
                continue;
            }
            if (!pedidoCobroPorMesa.containsKey(clave)) {
                pedidoCobroPorMesa.put(clave, p.getIdpedido());
            }
        }
        Set<Integer> pedidosConCobrar = new HashSet<>(pedidoCobroPorMesa.values());

        model.addAttribute("pedidos", resultado.getPedidos());
        model.addAttribute("filtroEstado", (estado == null || estado.isBlank()) ? "TODOS" : estado.toUpperCase());

        // Contar pedidos listos para entrega (para alerta visual)
        long pedidosListosParaEntrega = todosLosPedidos.stream()
                .filter(p -> "LISTO_PARA_ENTREGA".equalsIgnoreCase(p.getEstado()))
                .count();
        model.addAttribute("pedidosListosParaEntrega", pedidosListosParaEntrega);
        model.addAttribute("q", q);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("page", resultado.getPaginaActual());
        model.addAttribute("size", resultado.getTamanioPagina());
        model.addAttribute("totalPaginas", resultado.getTotalPaginas());
        model.addAttribute("totalPedidos", resultado.getTotalPedidos());
        model.addAttribute("cuentasPorMesa", cuentasPorMesa);
        model.addAttribute("pedidosConCobrar", pedidosConCobrar);

        // Kanban: agrupar pedidos por estado
        Map<String, List<PedidoDTO>> pedidosPorEstado = new HashMap<>();
        pedidosPorEstado.put("PENDIENTE", new ArrayList<>());
        pedidosPorEstado.put("EN_COCINA", new ArrayList<>());
        pedidosPorEstado.put("LISTO_PARA_ENTREGA", new ArrayList<>());
        pedidosPorEstado.put("COMPLETADO", new ArrayList<>());
        pedidosPorEstado.put("CANCELADO", new ArrayList<>());
        for (PedidoDTO p : todosLosPedidos) {
            if (p.getEstado() != null && pedidosPorEstado.containsKey(p.getEstado())) {
                pedidosPorEstado.get(p.getEstado()).add(p);
            }
        }
        model.addAttribute("pedidosPorEstado", pedidosPorEstado);

        return "Pedido/Pedidos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication authentication) {
        PedidoDTO pedido = new PedidoDTO();
        
        prepararModeloPos(model, authentication, pedido);
        return "Pedido/POS";
    }

    /**
     * Rellena el modelo con todos los datos necesarios para la vista POS,
     * reutilizable tanto en el GET /nuevo como cuando hay errores de validación
     * en el POST /guardar.
     */
    private void prepararModeloPos(Model model, Authentication authentication, PedidoDTO pedido) {
        // Obtener el usuario logueado y asignarlo automáticamente si no viene en el pedido
        if (authentication != null) {
            String username = authentication.getName();
            UsuarioDTO usuarioLogueado = usuarioService.listarTodos().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
            if (usuarioLogueado != null) {
                if (pedido.getIdUsuario() == 0) {
                    pedido.setIdUsuario(usuarioLogueado.getIdusuario());
                }
                model.addAttribute("usuarioLogueado", usuarioLogueado);
            }
        }

        model.addAttribute("pedido", pedido);
        // En el POS solo mostramos usuarios activos como responsables del pedido
        model.addAttribute("usuarios", usuarioService.listarTodos().stream()
                .filter(UsuarioDTO::isEstado)
                .toList());
        // Mesas disponibles para nuevos pedidos
        model.addAttribute("mesas", mesaService.listarDisponibles());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("categorias", categoriaService.listarActivas());
        // Cuentas abiertas para mostrar en el POS si la mesa seleccionada ya tiene cuenta
        model.addAttribute("cuentasAbiertas", cuentaService.listarAbiertas());
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute PedidoDTO pedido,
                          @org.springframework.web.bind.annotation.RequestParam(name = "modoCuenta", required = false, defaultValue = "EXISTENTE") String modoCuenta,
                          @org.springframework.web.bind.annotation.RequestParam(name = "idCuentaSeleccionada", required = false) Integer idCuentaSeleccionada,
                          Model model,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        
        // Validar campos obligatorios (si falla, volvemos al POS sin perder lo cargado)
        if (pedido.getIdMesa() == 0) {
            model.addAttribute("error", "Debe seleccionar una mesa");
            prepararModeloPos(model, authentication, pedido);
            return "Pedido/POS";
        }
        if (pedido.getIdCliente() == 0) {
            model.addAttribute("error", "Debe seleccionar un cliente");
            prepararModeloPos(model, authentication, pedido);
            return "Pedido/POS";
        }
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            model.addAttribute("error", "Debe agregar al menos un producto al pedido");
            prepararModeloPos(model, authentication, pedido);
            return "Pedido/POS";
        }
        
        // Validar stock de cada producto antes de enviar a la API
        // Solo para pedidos NUEVOS. En edición, el backend ya se encarga de
        // restaurar el stock anterior y descontar el nuevo, por lo que no
        // aplicamos validación adicional aquí.
        if (pedido.getIdpedido() == 0) {
            try {
                Map<Integer, ProductoDTO> productosMap = productoService.listarActivos().stream()
                        .collect(Collectors.toMap(ProductoDTO::getIdproducto, p -> p));

                for (PedidoDetalleDTO detalle : pedido.getDetalles()) {
                    if (detalle.getIdProducto() == 0 || detalle.getCantidad() == 0) {
                        continue;
                    }
                    ProductoDTO producto = productosMap.get(detalle.getIdProducto());
                    if (producto == null) {
                        redirectAttributes.addFlashAttribute("error", 
                                "Producto no encontrado con ID: " + detalle.getIdProducto());
                        return "redirect:/pedidos/nuevo";
                    }
                    int stockDisponible = producto.getStockActual();

                    if (detalle.getCantidad() > stockDisponible) {
                        redirectAttributes.addFlashAttribute("error", 
                                "Stock insuficiente para '" + producto.getNombre() + "'. " +
                                "Disponible: " + stockDisponible + ", Solicitado: " + detalle.getCantidad());
                        return "redirect:/pedidos/nuevo";
                    }
                }
            } catch (Exception e) {
                log.error("Error al validar stock en pedido nuevo: {}", e.getMessage());
            }
        }
        
        // Setear fecha actual si no viene
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }
        
        log.info("Fecha: {}", pedido.getFecha());
        log.info("Estado: {}", pedido.getEstado());
        
        try {
            if (pedido.getIdpedido() == 0) {
                PedidoDTO pedidoCreado = pedidoService.crear(pedido);

                // Asociar el pedido a una cuenta (una cuenta abierta por mesa y cliente)
                try {
                    boolean crearNuevaCuenta = "NUEVA".equalsIgnoreCase(modoCuenta);

                    CuentaDTO cuenta = null;

                    // Si el usuario seleccionó explícitamente una cuenta existente, intentamos usarla
                    if (!crearNuevaCuenta && idCuentaSeleccionada != null && idCuentaSeleccionada > 0) {
                        try {
                            CuentaDTO cuentaSeleccionada = cuentaService.obtenerPorId(idCuentaSeleccionada);
                            if (cuentaSeleccionada != null && "ABIERTA".equalsIgnoreCase(cuentaSeleccionada.getEstado())) {
                                cuenta = cuentaSeleccionada;
                            }
                        } catch (Exception ex) {
                            log.error("No se pudo obtener la cuenta seleccionada {}: {}", idCuentaSeleccionada, ex.getMessage());
                        }
                    }

                    // Si no hay cuenta seleccionada válida, aplicamos la lógica anterior
                    if (cuenta == null) {
                        if (crearNuevaCuenta) {
                            cuenta = cuentaService.crearCuenta(pedido.getIdMesa(), pedido.getIdCliente());
                        } else {
                            cuenta = cuentaService.listarAbiertas().stream()
                                    .filter(c -> c.getMesa() != null && c.getMesa().getIdmesa() == pedido.getIdMesa()
                                            && c.getCliente() != null && c.getCliente().getIdcliente() == pedido.getIdCliente())
                                    .findFirst()
                                    .orElseGet(() -> cuentaService.crearCuenta(pedido.getIdMesa(), pedido.getIdCliente()));
                        }
                    }

                    cuentaService.agregarPedido(cuenta.getIdcuenta(), pedidoCreado.getIdpedido());
                } catch (Exception e) {
                    log.error("Error al asociar pedido a cuenta: {}", e.getMessage(), e);
                }
            } else {
                pedidoService.actualizar(pedido.getIdpedido(), pedido);
            }
        } catch (Exception e) {
            log.error("Error al guardar pedido: {}", e.getMessage(), e);

            String mensajeBackend = null;
            if (e instanceof WebClientResponseException webEx) {
                mensajeBackend = webEx.getResponseBodyAsString();
                log.error("Respuesta de backend al guardar pedido: {}", mensajeBackend);
            }

            String mensajeError = (mensajeBackend != null && !mensajeBackend.isBlank())
                    ? mensajeBackend
                    : e.getMessage();

            // Para nuevos pedidos volvemos al POS, para ediciones a la lista
            boolean esNuevo = pedido.getIdpedido() == 0;

            if (mensajeError != null && mensajeError.contains("Stock insuficiente")) {
                if (esNuevo) {
                    redirectAttributes.addFlashAttribute("error", mensajeError);
                } else {
                    redirectAttributes.addFlashAttribute("mensajeError", mensajeError);
                }
            } else if (mensajeError != null && mensajeError.toLowerCase().contains("mesa")) {
                if (esNuevo) {
                    redirectAttributes.addFlashAttribute("error", mensajeError);
                } else {
                    redirectAttributes.addFlashAttribute("mensajeError", mensajeError);
                }
            } else {
                String texto = "Error al guardar el pedido: " + mensajeError;
                if (esNuevo) {
                    redirectAttributes.addFlashAttribute("error", texto);
                } else {
                    redirectAttributes.addFlashAttribute("mensajeError", texto);
                }
            }

            return esNuevo ? "redirect:/pedidos/nuevo" : "redirect:/pedidos";
        }

        // Éxito: para nuevos pedidos mostramos mensaje en el POS, para ediciones en la lista
        if (pedido.getIdpedido() == 0) {
            redirectAttributes.addFlashAttribute("error", null);
            redirectAttributes.addFlashAttribute("exito", "Pedido creado exitosamente");
            return "redirect:/pedidos/nuevo";
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido actualizado exitosamente");
            return "redirect:/pedidos";
        }
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable int id, Model model) {
        model.addAttribute("pedido", pedidoService.obtenerPorId(id));
        return "Pedido/Pedidodetalles";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        PedidoDTO pedido = pedidoService.obtenerPorId(id);
        
        // Bloquear edición si ya está en cocina, listo para entrega o en estado final
        if ("EN_COCINA".equals(pedido.getEstado())) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede editar el pedido porque ya está en cocina");
            return "redirect:/pedidos";
        }
        if ("LISTO_PARA_ENTREGA".equals(pedido.getEstado())) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede editar el pedido porque ya está listo para entrega");
            return "redirect:/pedidos";
        }
        if ("COMPLETADO".equals(pedido.getEstado()) || "CANCELADO".equals(pedido.getEstado())) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede editar el pedido en estado " + pedido.getEstado());
            return "redirect:/pedidos";
        }
        
        // Poblar los IDs a partir de los objetos anidados para el formulario
        if (pedido.getUsuario() != null) {
            pedido.setIdUsuario(pedido.getUsuario().getIdusuario());
        }
        if (pedido.getMesa() != null) {
            pedido.setIdMesa(pedido.getMesa().getIdmesa());
        }
        if (pedido.getCliente() != null) {
            pedido.setIdCliente(pedido.getCliente().getIdcliente());
        }

        // Alinear la lista de detalles de respuesta ("detalle")
        // con la lista usada por el formulario y el JS del POS ("detalles").
        // De esta forma, al editar un pedido el carrito se inicializa
        // correctamente con los productos existentes.
        if (pedido.getDetalle() != null && !pedido.getDetalle().isEmpty()) {
            pedido.setDetalles(pedido.getDetalle());
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioService.listarTodos());
        // Asegura que la mesa asignada esté en la lista aunque esté ocupada
        java.util.List<com.choza.consumochoza.modelo.dto.MesaDTO> mesas = mesaService.listarDisponibles();
        if (pedido.getIdMesa() != 0 && mesas.stream().noneMatch(m -> m.getIdmesa() == pedido.getIdMesa())) {
            com.choza.consumochoza.modelo.dto.MesaDTO mesaAsignada = mesaService.obtenerPorId(pedido.getIdMesa());
            if (mesaAsignada != null) {
                mesas.add(mesaAsignada);
            }
        }
        model.addAttribute("mesas", mesas);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("categorias", categoriaService.listarActivas());
        // Cuentas abiertas para indicar en el POS si esta mesa ya tiene cuenta
        model.addAttribute("cuentasAbiertas", cuentaService.listarAbiertas());
        return "Pedido/POS";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido cancelado correctamente. Stock restaurado.");
        } catch (Exception e) {
            log.error("Error al cancelar pedido {}: {}", id, e.getMessage(), e);

            String mensajeBackend = null;
            if (e instanceof WebClientResponseException webEx) {
                mensajeBackend = webEx.getResponseBodyAsString();
                log.error("Respuesta de backend al cancelar pedido {}: {}", id, mensajeBackend);
            }

            String mensajeError = (mensajeBackend != null && !mensajeBackend.isBlank())
                    ? mensajeBackend
                    : e.getMessage();

            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo cancelar el pedido: " + mensajeError);
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/finalizar/{id}")
    public String finalizarPedido(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cambiarEstado(id, "LISTO_PARA_ENTREGA");
            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido marcado como listo para entrega.");
        } catch (Exception e) {
            log.error("Error al finalizar pedido {}: {}", id, e.getMessage(), e);

            String mensajeBackend = null;
            if (e instanceof WebClientResponseException webEx) {
                mensajeBackend = webEx.getResponseBodyAsString();
                log.error("Respuesta de backend al finalizar pedido {}: {}", id, mensajeBackend);
            }

            String mensajeError = (mensajeBackend != null && !mensajeBackend.isBlank())
                    ? mensajeBackend
                    : e.getMessage();

            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo finalizar el pedido: " + mensajeError);
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/entregar/{id}")
    public String entregarPedido(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cambiarEstado(id, "COMPLETADO");
            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido #" + id + " entregado correctamente. La mesa ha sido liberada.");
        } catch (Exception e) {
            log.error("Error al entregar pedido {}: {}", id, e.getMessage(), e);

            String mensajeBackend = null;
            if (e instanceof WebClientResponseException webEx) {
                mensajeBackend = webEx.getResponseBodyAsString();
                log.error("Respuesta de backend al entregar pedido {}: {}", id, mensajeBackend);
            }

            String mensajeError = (mensajeBackend != null && !mensajeBackend.isBlank())
                    ? mensajeBackend
                    : e.getMessage();

            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo entregar el pedido: " + mensajeError);
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/enviar-cocina/{id}")
    public String enviarACocina(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cambiarEstado(id, "EN_COCINA");
            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido #" + id + " enviado a cocina correctamente.");
        } catch (Exception e) {
            log.error("Error al enviar a cocina pedido {}: {}", id, e.getMessage(), e);

            String mensajeBackend = null;
            if (e instanceof WebClientResponseException webEx) {
                mensajeBackend = webEx.getResponseBodyAsString();
                log.error("Respuesta de backend al enviar a cocina pedido {}: {}", id, mensajeBackend);
            }

            String mensajeError = (mensajeBackend != null && !mensajeBackend.isBlank())
                    ? mensajeBackend
                    : e.getMessage();

            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo enviar a cocina: " + mensajeError);
        }
        return "redirect:/pedidos";
    }
}

