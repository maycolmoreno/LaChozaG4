package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.presentacion.dto.request.CambiarEstadoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;
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
    private final IPedidoDtoMapper responseMapper;       // Dominio -> ResponseDTO
    private final PedidoRequestMapper pedidoRequestMapper;    // RequestDTO -> Dominio

    // =======================
    //         QUERIES
    // =======================

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO','COCINERO')")
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        var lista = pedidoUseCase.listar()
                .stream()
                .map(responseMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id:\\d+}")
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO','COCINERO')")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable("id") int idpedido) {
        var pedido = pedidoUseCase.obtenerPorId(idpedido);
        return ResponseEntity.ok(responseMapper.toResponseDTO(pedido));
    }

    /**
     * Listado paginado con filtros opcionales: estado, texto libre (q), rango de fechas.
     * Ejemplo: GET /api/pedidos/paginado?page=0&size=10&estado=PENDIENTE&q=mesa&fechaDesde=2024-01-01
     */
    @GetMapping("/paginado")
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
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO')")
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO request) {
		var dominio = pedidoRequestMapper.toDomain(request);
        var creado  = pedidoUseCase.crear(dominio);
        var body    = responseMapper.toResponseDTO(creado);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + body.getIdpedido()))
                .body(body);
    }

    @PutMapping(value = "/{id:\\d+}", consumes = "application/json")
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO')")
    public ResponseEntity<PedidoResponseDTO> actualizar(@PathVariable("id") int idpedido,
                                                        @Valid @RequestBody PedidoRequestDTO request) {
		var dominio      = pedidoRequestMapper.toDomain(request);
        var actualizado  = pedidoUseCase.actualizar(idpedido, dominio);
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * Cambiar estado del pedido (ej.: PENDIENTE -> EN_COCINA -> ENTREGADO -> CERRADO/CANCELADO).
     * Si prefieres PUT, puedes mantenerlo, pero PATCH es más semántico para cambios parciales.
     */
    @PatchMapping(value = "/{id:\\d+}/estado", consumes = "application/json")
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO','COCINERO')")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable("id") int idpedido,
                                                           @Valid @RequestBody CambiarEstadoRequestDTO request) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, request.getEstado());
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    // Soporte adicional para PUT /{id}/estado para el front consumochoza
    @PutMapping(value = "/{id:\\d+}/estado", consumes = "application/json")
    public ResponseEntity<PedidoResponseDTO> cambiarEstadoPut(@PathVariable("id") int idpedido,
                                                              @Valid @RequestBody CambiarEstadoRequestDTO request) {
        var actualizado = pedidoUseCase.cambiarEstado(idpedido, request.getEstado());
        return ResponseEntity.ok(responseMapper.toResponseDTO(actualizado));
    }

    /**
     * Semánticamente, en tu caso de uso `eliminar` realiza una "cancelación".
     * Si quieres hacerlo explícito, puedes exponer POST /{id}/cancelar y conservar DELETE para borrado físico.
     */
    @DeleteMapping("/{id:\\d+}")
    // @PreAuthorize("hasAnyRole('ADMIN','CAMARERO')")
    public ResponseEntity<Void> eliminar(@PathVariable("id") int idpedido) {
        pedidoUseCase.eliminar(idpedido);
        return ResponseEntity.noContent().build();
    }
}
