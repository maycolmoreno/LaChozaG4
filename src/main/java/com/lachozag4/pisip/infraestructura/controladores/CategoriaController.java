package com.lachozag4.pisip.infraestructura.controladores;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CategoriaJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.CategoriaEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.CategoriaMapper;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

	private final CategoriaJpaRepository categoriaRepo;

	public CategoriaController(CategoriaJpaRepository categoriaRepo) {
		this.categoriaRepo = categoriaRepo;
	}

	@GetMapping
	public List<Categoria> listar() {
		return categoriaRepo.findAll().stream().map(CategoriaMapper::aDominio).toList();
	}

	@PostMapping
	public ResponseEntity<Categoria> guardar(@Valid @RequestBody Categoria categoria) {

		CategoriaEntity entity = CategoriaMapper.aEntity(categoria);
		CategoriaEntity guardada = categoriaRepo.save(entity);

		return ResponseEntity.ok(CategoriaMapper.aDominio(guardada));
	}
}
