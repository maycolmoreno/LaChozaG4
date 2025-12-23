package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ProductoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Producto;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

	private final ProductoRepositoryPort productoRepo;

	public ProductoController(ProductoRepositoryPort productoRepo) {
		this.productoRepo = productoRepo;
	}

	@PostMapping
	public Producto crear(@RequestBody Producto producto) {
		return productoRepo.guardar(producto);
	}

	@GetMapping
	public List<Producto> listarMenu() {
		return productoRepo.listarTodos();
	}
}