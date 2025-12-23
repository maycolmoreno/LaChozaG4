package com.lachozag4.pisip.infraestructura.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/producto")
	public String producto() {
		return "producto";
	}

	@GetMapping("/agregar-producto")
	public String mostrarFormularioRegistro() {
		return "registro";
	}

	@GetMapping("/categoria")
	public String paginaCategorias(Model model) {
		model.addAttribute("contenido", "categoria :: contenido");
		return "base";
	}

	@GetMapping("/mesa")
	public String mesas(Model model) {
		model.addAttribute("contenido", "mesas :: contenido");
		return "base";
	}

	// --- NUEVO ENDPOINT PARA CLIENTES ---
	@GetMapping("/cliente")
	public String paginaClientes(Model model) {
		// Esto buscará el archivo cliente.html y el fragmento 'contenido'
		model.addAttribute("contenido", "cliente :: contenido");
		return "base";
	}
}