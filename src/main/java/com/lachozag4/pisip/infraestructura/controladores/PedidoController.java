package com.lachozag4.pisip.infraestructura.controladores;


import com.lachozag4.pisip.aplicacion.puertos.salida.PedidoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.presentacion.dto.PedidoRequestDTO;
import com.lachozag4.pisip.presentacion.mapeadores.PedidoDTOMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepositoryPort pedidoRepo;
    private final PedidoDTOMapper pedidoMapper;

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody PedidoRequestDTO dto) {

        if (dto.getMesaId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Pedido pedido = pedidoMapper.requestToEntity(dto);

        if (pedido.getMesa() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }

        Pedido guardado = pedidoRepo.guardar(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        List<Pedido> pedidos = pedidoRepo.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestBody String estado) {
        return pedidoRepo.buscarPorId(id)
            .map(p -> {
                p.setEstado(estado.replace("\"",""));
                return ResponseEntity.ok(pedidoRepo.guardar(p));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
