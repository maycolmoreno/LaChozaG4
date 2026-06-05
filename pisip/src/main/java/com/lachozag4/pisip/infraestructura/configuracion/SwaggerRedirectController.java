package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping({"/swagger-ui", "/swagger-ui/"})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}
