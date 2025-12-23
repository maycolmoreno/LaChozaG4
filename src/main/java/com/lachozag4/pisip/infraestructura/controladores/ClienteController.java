package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor // Genera el constructor para clienteRepo automáticamente
@CrossOrigin(origins = "*")
public class ClienteController {

	private final ClienteJpaRepository clienteRepo;

	@GetMapping
	public List<Cliente> listar() {
		return clienteRepo.findAll().stream().map(ClienteMapper::aDominio).toList();
	}

	@PostMapping
	public ResponseEntity<Cliente> guardar(@RequestBody Cliente cliente) {
		// 1. Convertir de Dominio a Entidad
		ClienteEntity entity = ClienteMapper.aEntity(cliente);

		// 2. Guardar en Base de Datos
		ClienteEntity guardado = clienteRepo.save(entity);

		// 3. Retornar el objeto convertido de nuevo a Dominio
		return ResponseEntity.ok(ClienteMapper.aDominio(guardado));
	}
}