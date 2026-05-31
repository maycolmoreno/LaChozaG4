package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICajaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.aplicacion.servicios.NotificacionService;
import com.lachozag4.pisip.aplicacion.servicios.PedidoHistorialService;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.seguridad.Roles;
import com.lachozag4.pisip.presentacion.dto.request.CambiarEstadoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoHistorialResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoPaginadoResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDtoMapper;      // Dominio -> ResponseDTO
import com.lachozag4.pisip.presentacion.mapeadores.PedidoRequestMapper;   // RequestDTO -> Dominio

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/pedidos", produces = "application/json")
@RequiredArgsConstructor
public class PedidoControlador {

    private final IPedidoUseCase pedidoUseCase;
    private final ICajaUseCase cajaUseCase;
    private final IPedidoDtoMapper responseMapper;         // Dominio -> ResponseDTO
    private final PedidoRequestMapper pedidoRequestMapper; // RequestDTO -> Dominio
    private final NotificacionService notificaciones;
    private final PedidoHistorialService historialService;

    // =======================
    //         QUERIES
    // =======================

    @GetMapping
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        var lista = pedidoUseCase.listar()
                .stream()
                .map(responseMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable("id") int idpedido) {
        var pedido = pedidoUseCase.obtenerPorId(idpedido);
        return ResponseEntity.ok(responseMapper.toResponseDTO(pedido));
    }

    @GetMapping("/{id:\\d+}/historial")
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<List<PedidoHistorialResponseDTO>> obtenerHistorial(@PathVariable("id") int idpedido) {
        pedidoUseCase.obtenerPorId(idpedido);
        return ResponseEntity.ok(historialService.listarPorPedido(idpedido));
    }

    @GetMapping("/cuenta/{idCuenta:\\d+}/reciente")
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<PedidoResponseDTO> obtenerRecientePorCuenta(@PathVariable("idCuenta") int idcuenta) {
        var pedido = pedidoUseCase.obtenerRecientePorCuenta(idcuenta);
        return ResponseEntity.ok(responseMapper.toResponseDTO(pedido));
    }

    /**
     * Listado paginado con filtros opcionales: estado, texto libre (q), rango de fechas.
     * Ejemplo: GET /api/pedidos/paginado?page=0&size=10&estado=PENDIENTE&q=mesa&fechaDesde=2024-01-01
     */
    @GetMapping("/paginado")
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<PedidoPaginadoResponseDTO> listarPaginado(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Convertir LocalDate a LocalDateTime (inicio y fin del día)
        LocalDateTime desde = fechaDesde != null ? fechaDesde.atStartOfDay() : null;
        LocalDateTime hasta = fechaHasta != null ? fechaHasta.atTime(23, 59, 59) : null;

        // Normalizar filtros vacíos a null
        String estadoFiltro = (estado != null && !estado.isBlank() && !"TODOS".equalsIgnoreCase(estado))
                ? estado.toUpperCase() : null;
        String qFiltro = (q != null && !q.isBlank()) ? q.trim().toLowerCase() : "";

        var resultado = pedidoUseCase.listarPaginado(estadoFiltro, qFiltro, desde, hasta, page, size);

        var pedidosDto = resultado.getContenido().stream()
                .map(responseMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(new PedidoPaginadoResponseDTO(
                pedidosDto,
                resultado.getTotalElementos(),
                resultado.getTotalPaginas(),
                resultado.getPaginaActual(),
                resultado.getTamanioPagina()));
    }

    // =======================
    //        COMMANDS
    // =======================

    @PostMapping(consumes = "application/json")
    @PreAuthorize(Roles.ADMIN_CAMARERO_CAJERO_PEDIDOS)
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO request,
                                                   Authentication authentication) {
        validarCajaAbiertaSiCajero(authentication);
		var dominio = pedidoRequestMapper.toDomain(request);
        var creado  = pedidoUseCase.crear(dominio);
        var body    = responseMapper.toResponseDTO(creado);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + body.getIdpedido()))
                .body(body);
    }

    @PostMapping(value = "/con-cuenta", consumes = "application/json")
    @PreAuthorize(Roles.ADMIN_CAMARERO_CAJERO_PEDIDOS)
    public ResponseEntity<PedidoResponseDTO> crearConCuenta(
            @Valid @RequestBody PedidoRequestDTO request,
            @RequestParam(value = "estadoDestino", required = false) String estadoDestino,
            Authentication authentication) {
        validarCajaAbiertaSiCajero(authentication);
        var dominio = pedidoRequestMapper.toDomain(request);
        var creado = pedidoUseCase.crearConCuenta(dominio, estadoDestino);
        var body = responseMapper.toResponseDTO(creado);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + body.getIdpedido()))
                .body(body);
    }

    @PutMapping(value = "/{id:\\d+}", consumes = "application/json")
    @PreAuthorize(Roles.ADMIN_CAMARERO_CAJERO_PEDIDOS)
    public ResponseEntity<PedidoResponseDTO> actualizar(@PathVariable("id") int idpedido,
                                                        @Valid @RequestBody PedidoRequestDTO request,
                                                        Authentication authentication) {
        validarCajaAbiertaSiCajero(authentication);
		var dominio      = pedidoRequestMapper.toDomain(request);
        var actualizado  = pedidoUseCase.actualizar(idpedido, dominio);
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * Endpoint genérico de compatibilidad para clientes que aún usan
     * PATCH /api/pedidos/{id}/estado con body {"estado":"..."}.
     */
    @PatchMapping(value = "/{id:\\d+}/estado", consumes = "application/json")
    @PreAuthorize(Roles.TODOS)
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable("id") int idpedido,
                                                           @Valid @RequestBody CambiarEstadoRequestDTO request,
                                                           Authentication authentication) {
        validarPermisoCambioEstadoCompat(request.getEstado(), authentication);
        validarCajaAbiertaSiCajeroSiEnviaACocina(request.getEstado(), authentication);
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, request.getEstado());
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    // ─── Transiciones de estado (semánticas) ──────────────────────────────────

    /**
     * CAMARERO confirma el pedido y lo envía a cocina.
     * Transición: PENDIENTE → EN_COCINA
     */
    @PatchMapping("/{id:\\d+}/confirmar")
    @PreAuthorize(Roles.ADMIN_CAMARERO_CAJERO_PEDIDOS)
    public ResponseEntity<PedidoResponseDTO> confirmar(@PathVariable("id") int idpedido,
                                                       Authentication authentication) {
        validarCajaAbiertaSiCajero(authentication);
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, Pedido.ESTADO_EN_COCINA);
        var origen = obtenerRolOperativo(authentication);
        notificaciones.notificarCocina(idpedido, actualizado.getEstado(), origen);
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * COCINA comienza a preparar el pedido.
     * Transición: PENDIENTE → EN_COCINA (asignación directa desde cocina)
     */
    @PatchMapping("/{id:\\d+}/preparando")
    @PreAuthorize(Roles.ADMIN_COCINA)
    public ResponseEntity<PedidoResponseDTO> preparando(@PathVariable("id") int idpedido) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, Pedido.ESTADO_EN_COCINA);
        notificaciones.notificarCocinaPrepara(idpedido, actualizado.getEstado(), "COCINA");
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * COCINA marca el pedido como listo para entregar.
     * Transición: EN_COCINA | EN_BAR → LISTO_PARA_ENTREGA
     */
    @PatchMapping("/{id:\\d+}/listo")
    @PreAuthorize(Roles.ADMIN_COCINA)
    public ResponseEntity<PedidoResponseDTO> listo(@PathVariable("id") int idpedido) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, Pedido.ESTADO_LISTO_PARA_ENTREGA);
        notificaciones.notificarCamareroListo(idpedido, actualizado.getEstado(), "COCINA");
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * CAMARERO entrega el pedido y lo marca como completado.
     * Transición: LISTO_PARA_ENTREGA → COMPLETADO
     */
    @PatchMapping("/{id:\\d+}/entregado")
    @PreAuthorize(Roles.ADMIN_CAMARERO)
    public ResponseEntity<PedidoResponseDTO> entregado(@PathVariable("id") int idpedido) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, Pedido.ESTADO_COMPLETADO);
        notificaciones.notificarCamareroEntregado(idpedido, actualizado.getEstado(), "CAMARERO");
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * ADMIN cancela el pedido en cualquier estado activo.
     * Transición: PENDIENTE | EN_COCINA | EN_BAR | LISTO_PARA_ENTREGA → CANCELADO
     */
    @PatchMapping("/{id:\\d+}/cancelar")
    @PreAuthorize(Roles.SOLO_ADMIN)
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable("id") int idpedido) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, Pedido.ESTADO_CANCELADO);
        notificaciones.notificarCancelado(idpedido, actualizado.getEstado(), "ADMIN");
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize(Roles.SOLO_ADMIN)
    public ResponseEntity<Void> eliminar(@PathVariable("id") int idpedido) {
        pedidoUseCase.eliminar(idpedido);
        return ResponseEntity.noContent().build();
    }

    private void validarCajaAbiertaSiCajeroSiEnviaACocina(String estadoSolicitado, Authentication authentication) {
        var estado = (estadoSolicitado == null ? "" : estadoSolicitado.trim().toUpperCase());
        if (Pedido.ESTADO_EN_COCINA.equals(estado)) {
            validarCajaAbiertaSiCajero(authentication);
        }
    }

    private void validarCajaAbiertaSiCajero(Authentication authentication) {
        if (!tieneRol(authentication, Roles.CAJERO)) {
            return;
        }

        try {
            cajaUseCase.obtenerCajaAbierta();
        } catch (NotFoundException ex) {
            throw new BusinessException("Debe aperturar caja antes de crear o enviar pedidos como cajero.");
        }
    }

    private void validarPermisoCambioEstadoCompat(String estadoSolicitado, Authentication authentication) {
        var estado = (estadoSolicitado == null ? "" : estadoSolicitado.trim().toUpperCase());
        if (estado.isEmpty()) {
            return;
        }

        boolean esAdmin = tieneRol(authentication, Roles.ADMIN);
        boolean esCamarero = tieneRol(authentication, Roles.CAMARERO);
        boolean esCajero = tieneRol(authentication, Roles.CAJERO);
        boolean esCocina = tieneRol(authentication, Roles.COCINA);
        boolean permitido;
        switch (estado) {
            case Pedido.ESTADO_EN_COCINA:
            permitido = esAdmin || esCamarero || esCajero;
                break;
            case "LISTO":
            case Pedido.ESTADO_LISTO_PARA_ENTREGA:
                permitido = esAdmin || esCocina;
                break;
            case Pedido.ESTADO_COMPLETADO:
            case "ENTREGADO":
                permitido = esAdmin || esCamarero;
                break;
            case Pedido.ESTADO_CANCELADO:
                permitido = esAdmin;
                break;
            default:
                permitido = esAdmin;
                break;
        }

        if (!permitido) {
            throw new AccessDeniedException("Tu rol no puede cambiar el pedido a " + estado + ".");
        }
    }

    private boolean tieneRol(Authentication authentication, String rol) {
        if (authentication == null) {
            return false;
        }
        String authority = "ROLE_" + rol;
        return authentication.getAuthorities().stream().anyMatch(a -> authority.equals(a.getAuthority()));
    }

    private String obtenerRolOperativo(Authentication authentication) {
        if (tieneRol(authentication, Roles.ADMIN)) {
            return Roles.ADMIN;
        }
        if (tieneRol(authentication, Roles.CAJERO)) {
            return Roles.CAJERO;
        }
        if (tieneRol(authentication, Roles.CAMARERO)) {
            return Roles.CAMARERO;
        }
        if (tieneRol(authentication, Roles.COCINA)) {
            return Roles.COCINA;
        }
        return "SISTEMA";
    }
}
