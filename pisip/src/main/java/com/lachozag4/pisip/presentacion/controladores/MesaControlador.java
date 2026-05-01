package com.lachozag4.pisip.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IComedorUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.MesaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.MesaResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IMesaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mesas")
public class MesaControlador {

	private final IMesaUseCase mesaUseCase;
	private final IComedorUseCase comedorUseCase;
	private final IMesaDtoMapper mapper;

	public MesaControlador(IMesaUseCase mesaUseCase, IComedorUseCase comedorUseCase, IMesaDtoMapper mapper) {
		this.mesaUseCase = mesaUseCase;
		this.comedorUseCase = comedorUseCase;
		this.mapper = mapper;
	}

	private MesaResponseDTO enriquecerConComedor(MesaResponseDTO dto) {
		if (dto.getIdcomedor() != null) {
			try {
				var comedor = comedorUseCase.buscarPorId(dto.getIdcomedor());
				dto.setNombreComedor(comedor.getNombre());
			} catch (Exception e) {
				dto.setNombreComedor("Sin asignar");
			}
		}
		return dto;
	}

	@PostMapping
	public ResponseEntity<MesaResponseDTO> crear(@Valid @RequestBody MesaRequestDTO request) {

		var mesa = mesaUseCase.crear(mapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(mesa));
	}

	@GetMapping
	public ResponseEntity<List<MesaResponseDTO>> listar() {
		var lista = mesaUseCase.listar().stream().map(mapper::toResponseDTO).map(this::enriquecerConComedor).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/disponibles")
	public ResponseEntity<List<MesaResponseDTO>> listarDisponibles() {
		var lista = mesaUseCase.listarDisponibles().stream().map(mapper::toResponseDTO).map(this::enriquecerConComedor).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/ocupadas")
	public ResponseEntity<List<MesaResponseDTO>> listarOcupadas() {
		var lista = mesaUseCase.listarOcupadas().stream().map(mapper::toResponseDTO).map(this::enriquecerConComedor).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MesaResponseDTO> obtenerPorId(@PathVariable("id") int idmesa) {
		return ResponseEntity.ok(enriquecerConComedor(mapper.toResponseDTO(mesaUseCase.obtenerPorId(idmesa))));
	}

	@PutMapping("/{id}")
	public ResponseEntity<MesaResponseDTO> actualizar(@PathVariable("id") int idmesa,
			@Valid @RequestBody MesaRequestDTO request) {
		var mesa = mesaUseCase.actualizar(idmesa, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDTO(mesa));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idmesa) {
		mesaUseCase.eliminar(idmesa);
		return ResponseEntity.noContent().build();
	}
}
