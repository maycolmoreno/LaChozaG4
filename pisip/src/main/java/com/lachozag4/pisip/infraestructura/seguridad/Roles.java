package com.lachozag4.pisip.infraestructura.seguridad;

/**
 * Constantes centralizadas de roles y expresiones SpEL para @PreAuthorize.
 *
 * <p>Uso en controladores:
 * <pre>
 *   {@literal @}PreAuthorize(Roles.ADMIN_CAMARERO)
 *   public ResponseEntity<?> miMetodo() { ... }
 * </pre>
 *
 * <p>IMPORTANTE: Spring Security antepone "ROLE_" internamente.
 * hasRole('ADMIN') verifica la autoridad "ROLE_ADMIN".
 */
public final class Roles {

    private Roles() { /* no instanciar */ }

    // ─── Nombres de rol (sin prefijo ROLE_) ───────────────────────────────────
    public static final String ADMIN    = "ADMIN";
    public static final String CAMARERO = "CAMARERO";
    public static final String COCINA   = "COCINA";
    public static final String CAJERO   = "CAJERO";

    // ─── Expresiones SpEL reutilizables para @PreAuthorize ────────────────────

    /** Solo ADMIN */
    public static final String SOLO_ADMIN =
            "hasRole('ADMIN')";

    /** ADMIN + CAMARERO (gestión de pedidos, mesas) */
    public static final String ADMIN_CAMARERO =
            "hasAnyRole('ADMIN','CAMARERO')";

    /** ADMIN + CAMARERO + CAJERO (creación/edición operativa de pedidos desde mesas) */
    public static final String ADMIN_CAMARERO_CAJERO_PEDIDOS =
            "hasAnyRole('ADMIN','CAMARERO','CAJERO')";

    /** ADMIN + COCINA (operaciones de cocina) */
    public static final String ADMIN_COCINA =
            "hasAnyRole('ADMIN','COCINA')";

    /** ADMIN + CAJERO (cobros, pagos, caja) */
    public static final String ADMIN_CAJERO =
            "hasAnyRole('ADMIN','CAJERO')";

    /** ADMIN + CAMARERO + CAJERO (consulta de clientes, mesas, cuentas) */
    public static final String ADMIN_CAMARERO_CAJERO =
            "hasAnyRole('ADMIN','CAMARERO','CAJERO')";

    /** ADMIN + CAMARERO + COCINA (cambios de estado de pedidos de sala/cocina) */
    public static final String ADMIN_CAMARERO_COCINA =
            "hasAnyRole('ADMIN','CAMARERO','COCINA')";

    /** Todos los roles operativos */
    public static final String TODOS =
            "hasAnyRole('ADMIN','CAMARERO','COCINA','CAJERO')";
}
