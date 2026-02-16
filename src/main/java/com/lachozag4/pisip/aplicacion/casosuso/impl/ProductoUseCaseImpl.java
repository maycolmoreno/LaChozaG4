package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;

public class ProductoUseCaseImpl implements IProductoUseCase {

    private final IProductoRepositorio productoRepositorio;
    private final ICategoriaRepositorio categoriaRepositorio;

    public ProductoUseCaseImpl(IProductoRepositorio productoRepositorio, ICategoriaRepositorio categoriaRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
    }

    @Override
    public Producto crear(Producto producto, int categoriaId) {
        Categoria categoria = categoriaRepositorio.buscarPorId(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con id: " + categoriaId));

        // Validar nombre único dentro de la misma categoría
        productoRepositorio.buscarPorNombreYCategoria(producto.getNombre(), categoriaId).ifPresent(existente -> {
            throw new BusinessException("Ya existe un producto con el nombre '" + producto.getNombre() + "' en esta categoría");
        });

        Producto productoConCategoria = new Producto(
                0,
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getDescripcion(),
                producto.getImagenUrl(),
                producto.getEstado(),
                categoria
        );

        return productoRepositorio.guardar(productoConCategoria);
    }

    @Override
    public Producto buscarPorId(int id) {
        return productoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id: " + id));
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepositorio.listarTodos();
    }

    @Override
    public List<Producto> listarPorCategoria(int idCategoria) {
        return productoRepositorio.listarPorCategoria(idCategoria);
    }

    @Override
    public List<Producto> listarActivos() {
        return productoRepositorio.listarActivos();
    }

    @Override
    public Producto actualizar(int id, Producto producto, int categoriaId) {
        buscarPorId(id);

        Categoria categoria = categoriaRepositorio.buscarPorId(categoriaId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con id: " + categoriaId));

        // Validar nombre único dentro de la misma categoría (excluir el propio producto)
        productoRepositorio.buscarPorNombreYCategoria(producto.getNombre(), categoriaId).ifPresent(existente -> {
            if (existente.getIdproducto() != id) {
                throw new BusinessException("Ya existe un producto con el nombre '" + producto.getNombre() + "' en esta categoría");
            }
        });

        Producto productoActualizado = new Producto(
                id,
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getDescripcion(),
                producto.getImagenUrl(),
                producto.getEstado(),
                categoria
        );

        return productoRepositorio.guardar(productoActualizado);
    }

	/*
	 * @Override public Producto actualizarStock(Integer id, Integer cantidad) {
	 * Producto producto = buscarPorId(id); Producto productoActualizado =
	 * producto.actualizarStock(cantidad); return
	 * productoRepositorio.guardar(productoActualizado); }
	 */

    @Override
    public void eliminar(int id) {
        Producto producto = buscarPorId(id);
        // Eliminación lógica: desactivar el producto en vez de borrarlo físicamente
        // para evitar errores de FK con pedido_detalle
        Producto desactivado = new Producto(
                producto.getIdproducto(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getDescripcion(),
                producto.getImagenUrl(),
                false, // estado = false (dado de baja)
                producto.getFkCategoria()
        );
        productoRepositorio.guardar(desactivado);
    }
}