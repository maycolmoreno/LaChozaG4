package com.lachozag4.pisip.infraestructura.controladores;

import org.springframework.stereotype.Controller;
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
}
