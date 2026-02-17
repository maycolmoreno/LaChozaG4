package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;

public class ClienteUseCaseImpl implements IClienteUseCase {

    private final IClienteRepositorio repositorio;

    public ClienteUseCaseImpl(IClienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Cliente crear(Cliente cliente) {
        // Regla simple de negocio
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar cédula única
        repositorio.buscarPorCedula(cliente.getCedula()).ifPresent(existente -> {
            throw new BusinessException("Ya existe un cliente con la cédula: " + cliente.getCedula());
        });

        // Validar email único (solo si viene informado)
        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            repositorio.buscarPorEmail(cliente.getEmail()).ifPresent(existente -> {
                throw new BusinessException("Ya existe un cliente con el email: " + cliente.getEmail());
            });
        }

        return repositorio.guardar(cliente);
    }

    @Override
    public Cliente obtenerPorId(int idcliente) {
        return repositorio.buscarPorId(idcliente)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con ID: " + idcliente));
    }

    @Override
    public List<Cliente> listar() {
        // Ahora devolvemos todos los clientes (activos e inactivos)
        // para que el frontend pueda aplicar filtros por estado.
        return repositorio.listarTodos();
    }

    @Override
    public void eliminar(int idcliente) {

        Cliente cliente = obtenerPorId(idcliente);

        Cliente clienteInactivo = new Cliente(
                cliente.getIdcliente(),
                cliente.getNombre(),
                cliente.getCedula(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getEmail(),
                false
        );

        repositorio.guardar(clienteInactivo);
    }

    @Override
    public Cliente actualizar(int idcliente, Cliente cliente) {
        Cliente existente = obtenerPorId(idcliente);

        // Validar cédula única (excluir el propio cliente)
        repositorio.buscarPorCedula(cliente.getCedula()).ifPresent(otro -> {
            if (otro.getIdcliente() != idcliente) {
                throw new BusinessException("Ya existe un cliente con la cédula: " + cliente.getCedula());
            }
        });

        // Validar email único (excluir el propio cliente, solo si viene informado)
        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            repositorio.buscarPorEmail(cliente.getEmail()).ifPresent(otro -> {
                if (otro.getIdcliente() != idcliente) {
                    throw new BusinessException("Ya existe un cliente con el email: " + cliente.getEmail());
                }
            });
        }

        Cliente actualizado = new Cliente(
                existente.getIdcliente(),
                cliente.getNombre(),
                cliente.getCedula(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getEmail(),
                cliente.getEstado()
        );

        if (actualizado.getNombre() == null || actualizado.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        return repositorio.guardar(actualizado);
    }
}
