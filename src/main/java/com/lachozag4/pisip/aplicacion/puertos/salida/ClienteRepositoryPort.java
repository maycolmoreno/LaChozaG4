package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {

    /**
     * Busca un cliente por su número de cédula o RUC.
     * @param identificacion Cédula o RUC del cliente.
     * @return Un Optional con el cliente si existe.
     */
    Optional<Cliente> buscarPorIdentificacion(String identificacion);

    /**
     * Guarda un nuevo cliente o actualiza uno existente.
     * @param cliente Objeto cliente con los datos a persistir.
     * @return El cliente guardado.
     */
    Cliente guardar(Cliente cliente);

    /**
     * Verifica si un cliente ya está registrado por su identificación.
     */
    boolean existePorIdentificacion(String identificacion);
}