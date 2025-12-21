package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ClienteRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ClienteMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClientePersistenceAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Optional<Cliente> buscarPorIdentificacion(String identificacion) {
        return clienteJpaRepository
                .findByIdentificacion(identificacion)
                .map(ClienteMapper::aDominio);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = ClienteMapper.aEntity(cliente);
        ClienteEntity guardado = clienteJpaRepository.save(entity);
        return ClienteMapper.aDominio(guardado);
    }

    @Override
    public boolean existePorIdentificacion(String identificacion) {
        return clienteJpaRepository.existsByIdentificacion(identificacion);
    }
}
