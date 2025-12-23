package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.MesaMapper;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")

@CrossOrigin(origins = "*")
public class MesaController {

	private final MesaJpaRepository mesaRepo;

	public MesaController(MesaJpaRepository mesaRepo) {
		this.mesaRepo = mesaRepo;
	}

	@GetMapping
	public List<Mesa> listar() {
		return mesaRepo.findAll().stream().map(MesaMapper::aDominio).toList();
	}

	@PostMapping
	public ResponseEntity<Mesa> guardar(@Valid @RequestBody Mesa mesa) {

		MesaEntity entity = MesaMapper.aEntity(mesa);
		MesaEntity guardada = mesaRepo.save(entity);

		return ResponseEntity.ok(MesaMapper.aDominio(guardada));
	}
}
