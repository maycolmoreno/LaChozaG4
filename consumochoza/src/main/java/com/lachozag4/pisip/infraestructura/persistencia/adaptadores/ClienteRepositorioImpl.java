package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;



import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IClienteJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IClienteJpaRepositorio;


public class ClienteRepositorioImpl implements IClienteRepositorio {

    private final IClienteJpaRepositorio jpaRepository;
    private final IClienteJpaMapper mapper;

    public ClienteRepositorioImpl(
            IClienteJpaRepositorio jpaRepository,
            IClienteJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(int idcliente) {
        return jpaRepository.findById(idcliente).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorCedula(String cedula) {
        return jpaRepository.findByCedula(cedula).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> listarActivos() {
        return jpaRepository.findByEstadoTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        return guardar(cliente);
    }

    @Override
    public boolean existePorId(int idcliente) {
        return jpaRepository.existsById(idcliente);
    }

    @Override
    public void eliminar(int idcliente) {
        if (existePorId(idcliente)) {
            jpaRepository.deleteById(idcliente);
        }
    }
}
