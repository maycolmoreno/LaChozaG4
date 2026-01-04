package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ProductoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @GetMapping
    public ResponseEntity<List<Producto>> listarMenu() {
        List<Producto> productos = productoRepo.listarTodos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepo.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        producto.setId(null);
        
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        if (producto.getDisponible() == null) {
            producto.setDisponible(true);
        }
        
        if (producto.getStockActual() == null) {
            producto.setStockActual(0);
        }
        
        Producto guardado = productoRepo.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        if (!productoRepo.existePorId(id)) {
            return ResponseEntity.notFound().build();
        }
        
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        producto.setId(id);
        Producto actualizado = productoRepo.guardar(producto);
        return ResponseEntity.ok(actualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!productoRepo.existePorId(id)) {
            return ResponseEntity.notFound().build();
        }
        
        productoRepo.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}