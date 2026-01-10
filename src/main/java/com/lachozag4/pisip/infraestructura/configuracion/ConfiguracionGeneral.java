package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.impl.CategoriaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ClienteUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.MesaUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.PedidoDetalleUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.PedidoUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.ProductoUseCaseImpl;
import com.lachozag4.pisip.aplicacion.casosuso.impl.UsuarioUseCaseImpl;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IUsuarioRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.CategoriaRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.ClienteRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.MesaRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.PedidoDetalleRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.PedidoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.ProductoRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.adaptadores.UsuarioRepositorioImpl;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IClienteJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IMesaJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoDetalleJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.ICategoriaJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IClienteJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IMesaJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoDetalleJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IProductoJpaRepositorio;
import com.lachozag4.pisip.infraestructura.repositorios.IUsuarioJpaRepositorio;

@Configuration
public class ConfiguracionGeneral {
	@Bean
	ICategoriaRepositorio categoriaRepositorio(ICategoriaJpaRepositorio jpaRepository, ICategoriaJpaMapper mapper) {
		return new CategoriaRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	ICategoriaUseCase categoriaUseCase(ICategoriaRepositorio repositorio) {
		return new CategoriaUseCaseImpl(repositorio);
	}

	@Bean
	IProductoRepositorio productoRepositorio(IProductoJpaRepositorio jpaRepository, IProductoJpaMapper mapper) {
		return new ProductoRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IProductoUseCase productoUseCase(IProductoRepositorio repositorio) {
		return new ProductoUseCaseImpl(repositorio);
	}

	@Bean
	IClienteRepositorio clienteRepositorio(IClienteJpaRepositorio jpaRepository, IClienteJpaMapper mapper) {
		return new ClienteRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IClienteUseCase clienteUseCase(IClienteRepositorio repositorio) {
		return new ClienteUseCaseImpl(repositorio);
	}

	@Bean
	IMesaRepositorio mesaRepositorio(IMesaJpaRepositorio jpaRepository, IMesaJpaMapper mapper) {
		return new MesaRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IMesaUseCase mesaUseCase(IMesaRepositorio repositorio) {
		return new MesaUseCaseImpl(repositorio);
	}

	@Bean
	IPedidoRepositorio pedidoRepositorio(IPedidoJpaRepositorio jpaRepository, IPedidoJpaMapper mapper) {
		return new PedidoRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IPedidoUseCase pedidoUseCase(IPedidoRepositorio repositorio) {
		return new PedidoUseCaseImpl(repositorio);
	}

	@Bean
	IPedidoDetalleRepositorio pedidodetalleRepositorio(IPedidoDetalleJpaRepositorio jpaRepository,
			IPedidoDetalleJpaMapper mapper) {
		return new PedidoDetalleRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IPedidoDetalleUseCase pedidodetalleUseCase(IPedidoDetalleRepositorio repositorio) {
		return new PedidoDetalleUseCaseImpl(repositorio);
	}

	@Bean
	IUsuarioRepositorio usuarioRepositorio(IUsuarioJpaRepositorio jpaRepository, IUsuarioJpaMapper mapper) {
		return new UsuarioRepositorioImpl(jpaRepository, mapper);
	}

	@Bean
	IUsuarioUseCase ssuarioUseCase(IUsuarioRepositorio repositorio) {
		return new UsuarioUseCaseImpl(repositorio);
	}
}
