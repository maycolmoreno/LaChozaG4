package com.lachozag4.pisip.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.presentacion.dto.request.ClienteRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ClienteResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IClienteDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteControlador {

    private final IClienteUseCase clienteUseCase;
    private final IClienteDtoMapper mapper;

    // CREATE
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(
            @Valid @RequestBody ClienteRequestDTO request) {

        var cliente = clienteUseCase.crear(mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(cliente));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        var lista = clienteUseCase.listar()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable int id) {
        var cliente = clienteUseCase.obtenerPorId(id);
        return ResponseEntity.ok(mapper.toResponseDTO(cliente));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable int id,
            @Valid @RequestBody ClienteRequestDTO request) {

        var actualizado = clienteUseCase.actualizar(id, mapper.toDomain(request));
        return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        clienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
