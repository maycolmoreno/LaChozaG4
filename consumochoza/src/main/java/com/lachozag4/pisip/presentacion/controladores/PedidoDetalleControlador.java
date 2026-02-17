package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.presentacion.dto.request.PedidoDetalleRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoDetalleResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDetalleDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/pedidos/{idPedido}/detalles")
@RequiredArgsConstructor
@Validated
public class PedidoDetalleControlador {

    private final IPedidoDetalleUseCase detalleUseCase;
    private final IPedidoDetalleDtoMapper mapper;

    // GET /api/pedidos/{idPedido}/detalles
    @GetMapping
    public ResponseEntity<List<PedidoDetalleResponseDTO>> listarPorPedido(@PathVariable int idPedido) {
        var detalles = detalleUseCase.listarPorPedido(idPedido);
        return ResponseEntity.ok(mapper.toResponseDTOList(detalles));
    }

    // GET /api/pedidos/{idPedido}/detalles/{idDetalle}
    @GetMapping("/{idDetalle}")
    public ResponseEntity<PedidoDetalleResponseDTO> obtener(
            @PathVariable int idPedido,
            @PathVariable int idDetalle) {

        var detalle = detalleUseCase.obtenerPorId(idPedido, idDetalle);
        return ResponseEntity.ok(mapper.toResponseDTO(detalle));
    }

    // POST /api/pedidos/{idPedido}/detalles
    @PostMapping
    public ResponseEntity<PedidoDetalleResponseDTO> crear(
            @PathVariable int idPedido,
            @Valid @RequestBody PedidoDetalleRequestDTO request) {

        var creado = detalleUseCase.crear(
                idPedido,
                request.getIdProducto(),
                request.getCantidad(),
                request.getPrecioUnitario()
        );

        var dto = mapper.toResponseDTO(creado);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idDetalle}")
                .buildAndExpand(dto.getIdpedidodetalle())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    // PUT /api/pedidos/{idPedido}/detalles/{idDetalle}
    @PutMapping("/{idDetalle}")
    public ResponseEntity<PedidoDetalleResponseDTO> actualizar(
            @PathVariable int idPedido,
            @PathVariable int idDetalle,
            @Valid @RequestBody PedidoDetalleRequestDTO request) {

        var actualizado = detalleUseCase.actualizar(
                idPedido,
                idDetalle,
                request.getIdProducto(),
                request.getCantidad(),
                request.getPrecioUnitario()
        );

        return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
    }

    // DELETE /api/pedidos/{idPedido}/detalles/{idDetalle}
    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminar(
            @PathVariable int idPedido,
            @PathVariable int idDetalle) {

        detalleUseCase.eliminar(idPedido, idDetalle);
        return ResponseEntity.noContent().build();
    }
}