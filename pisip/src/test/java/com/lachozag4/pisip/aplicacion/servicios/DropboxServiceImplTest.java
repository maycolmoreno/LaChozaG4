package com.lachozag4.pisip.aplicacion.servicios;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.sharing.*;
import com.lachozag4.pisip.dominio.servicios.IDropboxService.DropboxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DropboxServiceImpl — tests unitarios")
class DropboxServiceImplTest {

    @Mock DbxClientV2 dropboxClient;
    @Mock DbxUserFilesRequests filesRequests;
    @Mock DbxUserSharingRequests sharingRequests;
        @Mock UploadBuilder uploadBuilder;

    DropboxServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DropboxServiceImpl(dropboxClient, "/LaChoza/Comprobantes");
    }

    // ─── subirArchivo ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("subirArchivo — éxito devuelve la ruta del archivo en Dropbox")
    void subirArchivo_exito() throws Exception {
        InputStream stream = new ByteArrayInputStream("datos".getBytes());
        long tamano = 10;

        FileMetadata metadata = mock(FileMetadata.class);
        when(metadata.getPathDisplay()).thenReturn("/LaChoza/Comprobantes/foto.jpg");

        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(uploadBuilder.withMode(any())).thenReturn(uploadBuilder);
        when(uploadBuilder.withAutorename(anyBoolean())).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any(InputStream.class))).thenReturn(metadata);

        String resultado = service.subirArchivo("foto.jpg", stream, tamano);

        assertThat(resultado).isEqualTo("/LaChoza/Comprobantes/foto.jpg");
    }

    @Test
    @DisplayName("subirArchivo — lanza DropboxException si el archivo supera 5 MB")
    void subirArchivo_archivoDemasiadoGrande() {
        InputStream stream = new ByteArrayInputStream(new byte[0]);
        long tamanoExcedido = 6L * 1024 * 1024;

        assertThatThrownBy(() -> service.subirArchivo("grande.jpg", stream, tamanoExcedido))
                .isInstanceOf(DropboxException.class)
                .hasMessageContaining("excede el límite");
    }

    @Test
    @DisplayName("subirArchivo — lanza DropboxException si la API falla")
    void subirArchivo_fallaApi() throws Exception {
        InputStream stream = new ByteArrayInputStream("datos".getBytes());

        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(uploadBuilder.withMode(any())).thenReturn(uploadBuilder);
        when(uploadBuilder.withAutorename(anyBoolean())).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(any(InputStream.class)))
                .thenThrow(new DbxException("API error"));

        assertThatThrownBy(() -> service.subirArchivo("foto.jpg", stream, 100))
                .isInstanceOf(DropboxException.class)
                .hasMessageContaining("Error al subir comprobante");
    }

    // ─── obtenerUrlPublica ──────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerUrlPublica — devuelve URL directa de Dropbox (nuevo link)")
    void obtenerUrlPublica_nuevoLink() throws Exception {
        SharedLinkMetadata mockLink = mock(SharedLinkMetadata.class);
        when(mockLink.getUrl())
                .thenReturn("https://www.dropbox.com/s/abc123/foto.jpg?dl=0");

        when(dropboxClient.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(anyString())).thenReturn(mockLink);

        String url = service.obtenerUrlPublica("/LaChoza/Comprobantes/foto.jpg");

        assertThat(url)
                .doesNotContain("www.dropbox.com")
                .doesNotContain("?dl=0")
                .startsWith("https://dl.dropboxusercontent.com");
    }

    @Test
    @DisplayName("obtenerUrlPublica — reutiliza link existente si ya existe")
    void obtenerUrlPublica_linkExistente() throws Exception {
        // Simular que ya existe un shared link
        CreateSharedLinkWithSettingsErrorException alreadyExists =
                mock(CreateSharedLinkWithSettingsErrorException.class);
        CreateSharedLinkWithSettingsError error =
                CreateSharedLinkWithSettingsError.sharedLinkAlreadyExists(null);
        ReflectionTestUtils.setField(alreadyExists, "errorValue", error);

        SharedLinkMetadata existingLink = mock(SharedLinkMetadata.class);
        when(existingLink.getUrl())
                .thenReturn("https://www.dropbox.com/s/xyz/foto.jpg?dl=0");

        ListSharedLinksResult listResult = mock(ListSharedLinksResult.class);
        when(listResult.getLinks()).thenReturn(List.of(existingLink));

        ListSharedLinksBuilder listBuilder = mock(ListSharedLinksBuilder.class);
        when(listBuilder.withPath(anyString())).thenReturn(listBuilder);
        when(listBuilder.withDirectOnly(anyBoolean())).thenReturn(listBuilder);
        when(listBuilder.start()).thenReturn(listResult);

        when(dropboxClient.sharing()).thenReturn(sharingRequests);
        when(sharingRequests.createSharedLinkWithSettings(anyString())).thenThrow(alreadyExists);
        when(sharingRequests.listSharedLinksBuilder()).thenReturn(listBuilder);

        String url = service.obtenerUrlPublica("/LaChoza/Comprobantes/foto.jpg");

        assertThat(url).startsWith("https://dl.dropboxusercontent.com");
    }

    // ─── eliminarArchivo ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarArchivo — llama a deleteV2 con la ruta correcta")
    void eliminarArchivo_exito() throws Exception {
        DeleteResult deleteResult = mock(DeleteResult.class);

        when(dropboxClient.files()).thenReturn(filesRequests);
        when(filesRequests.deleteV2(anyString())).thenReturn(deleteResult);

        assertThatNoException().isThrownBy(
                () -> service.eliminarArchivo("/LaChoza/Comprobantes/foto.jpg"));

        verify(filesRequests).deleteV2("/LaChoza/Comprobantes/foto.jpg");
    }

    // ─── validarExtension ───────────────────────────────────────────────────────

    @Test
    @DisplayName("validarExtension — acepta jpg, jpeg, png")
    void validarExtension_extensionesValidas() {
        assertThatNoException().isThrownBy(() -> service.validarExtension("foto.jpg"));
        assertThatNoException().isThrownBy(() -> service.validarExtension("foto.JPEG"));
        assertThatNoException().isThrownBy(() -> service.validarExtension("foto.PNG"));
    }

    @Test
    @DisplayName("validarExtension — rechaza extensiones no permitidas")
    void validarExtension_extensionInvalida() {
        assertThatThrownBy(() -> service.validarExtension("malware.exe"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Extensión");
        assertThatThrownBy(() -> service.validarExtension("documento.pdf"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
