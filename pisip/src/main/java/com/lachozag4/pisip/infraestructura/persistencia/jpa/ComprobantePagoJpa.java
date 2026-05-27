package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comprobante_pago")
@Getter
@Setter
@NoArgsConstructor
public class ComprobantePagoJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcomprobante;

    @ManyToOne
    @JoinColumn(name = "fk_pago", nullable = false)
    private PagoJpa fkPago;

    @Column(nullable = false, length = 255)
    private String nombreArchivo;

    @Column(nullable = false, length = 500)
    private String rutaRelativa;

    /** Ruta del archivo dentro de Dropbox, ej: /LaChoza/Comprobantes/nombre.jpg */
    @Column(name = "ruta_dropbox", length = 500)
    private String rutaDropbox;

    /** URL pública del comprobante en Dropbox para visualización directa en MAUI. */
    @Column(name = "url_dropbox", length = 1024)
    private String urlDropbox;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long tamano;

    @Column(nullable = false, length = 100)
    private String usuarioRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaSubida;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComprobantePagoJpa that)) return false;
        return idcomprobante != 0 && idcomprobante == that.idcomprobante;
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
