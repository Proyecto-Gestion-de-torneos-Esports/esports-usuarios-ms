package com.torneos.usuarios.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.torneos.usuarios.client.AuditoriaClient;
import com.torneos.usuarios.client.EquipoClient;
import com.torneos.usuarios.dto.AuditoriaRequestDTO;
import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Rol;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaClient auditoriaClient;

    @Mock
    private EquipoClient equipoClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Usuario admin;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Jugador1", "jugador@test.com", Rol.JUGADOR, true, 5L);
        admin = new Usuario(2L, "Admin1", "admin@test.com", Rol.ADMIN, true, null);

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombreUsuario("NuevoUser");
        requestDTO.setCorreo("nuevo@test.com");
        requestDTO.setRol(Rol.JUGADOR);
        requestDTO.setEquipoId(5L);
    }

    @Test
    public void testGuardarUsuario_Exito() {
        when(equipoClient.obtenerEquipoPorId(5L)).thenReturn(Map.of("id", 5L, "nombre", "Test Team"));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(auditoriaClient.generarAuditoria(any(AuditoriaRequestDTO.class))).thenReturn(null);

        UsuarioResponseDTO response = usuarioService.guardar(requestDTO);

        assertNotNull(response);
        assertTrue(response.getActivo());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testActualizarUsuario_AccesoDenegado() {
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizar(2L, requestDTO, 1L); // El ejecutor 1 intenta actualizar al usuario 2
        });

        assertTrue(exception.getMessage().contains("Acceso denegado"));
    }

    @Test
    public void testEliminarUsuario_Exito() {
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(2L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario)); // Usuario a eliminar

        usuarioService.eliminar(1L, 2L);

        assertFalse(usuario.getActivo());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(auditoriaClient, times(1)).generarAuditoria(any(AuditoriaRequestDTO.class));
    }

    @Test
    public void testListarTodos_Exito() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, admin));

        List<UsuarioResponseDTO> response = usuarioService.listarTodos();

        assertNotNull(response);
        assertEquals(2, response.size());
        verify(usuarioRepository, times(1)).findAll();
    }
    @Test
    public void testBuscarPorId_Exito() {
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> response = usuarioService.buscarPorId(1L);

        assertTrue(response.isPresent());
        assertEquals("Jugador1", response.get().getNombreUsuario());
        verify(usuarioRepository, times(1)).findByUsuarioIdAndActivoTrue(1L);
    }

    @Test
    public void testBuscarPorId_NoEncontrado() {
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> response = usuarioService.buscarPorId(99L);

        assertFalse(response.isPresent());
    }
    @Test
    public void testActualizarUsuario_ExitoComoAdmin() {
        // Ejecutor es Admin (ID 2)
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(2L)).thenReturn(Optional.of(admin));
        // Usuario a actualizar (ID 1)
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(auditoriaClient.generarAuditoria(any(AuditoriaRequestDTO.class))).thenReturn(null);

        UsuarioResponseDTO response = usuarioService.actualizar(1L, requestDTO, 2L);

        assertNotNull(response);
        assertEquals("NuevoUser", response.getNombreUsuario()); // Verifica que tomó los datos del DTO
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(auditoriaClient, times(1)).generarAuditoria(any(AuditoriaRequestDTO.class));
    }

    @Test
    public void testActualizarUsuario_EjecutorNoExiste() {
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        java.util.NoSuchElementException exception = assertThrows(java.util.NoSuchElementException.class, () -> {
            usuarioService.actualizar(1L, requestDTO, 99L);
        });

        assertTrue(exception.getMessage().contains("no existe o está inactivo"));
    }
    @Test
    public void testObtenerActivos_Exito() {
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> response = usuarioService.obtenerActivos();

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(usuarioRepository, times(1)).findByActivoTrue();
    }
    @Test
    public void testBuscarPorCorreo_Exito() {
        when(usuarioRepository.findByCorreoAndActivoTrue("jugador@test.com")).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.buscarPorCorreo("jugador@test.com");

        assertNotNull(response);
        assertEquals("jugador@test.com", response.getCorreo());
    }

    @Test
    public void testBuscarPorCorreo_NoEncontrado() {
        when(usuarioRepository.findByCorreoAndActivoTrue("noexiste@test.com")).thenReturn(Optional.empty());

        java.util.NoSuchElementException exception = assertThrows(java.util.NoSuchElementException.class, () -> {
            usuarioService.buscarPorCorreo("noexiste@test.com");
        });

        assertTrue(exception.getMessage().contains("No se encontro ningun usuario activo con el correo"));
    }
    @Test
    public void testBuscarPorNombreUsuario_Exito() {
        when(usuarioRepository.findByNombreUsuarioAndActivoTrue("Jugador1")).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.buscarPorNombreUsuario("Jugador1");

        assertNotNull(response);
        assertEquals("Jugador1", response.getNombreUsuario());
    }

    @Test
    public void testBuscarPorNombreUsuario_NoEncontrado() {
        when(usuarioRepository.findByNombreUsuarioAndActivoTrue("Fantasma")).thenReturn(Optional.empty());

        java.util.NoSuchElementException exception = assertThrows(java.util.NoSuchElementException.class, () -> {
            usuarioService.buscarPorNombreUsuario("Fantasma");
        });

        assertTrue(exception.getMessage().contains("No se encontro ningun usuario activo con el nombre"));
    }
    @Test
    public void testGuardarUsuario_EquipoNoExiste() {
        when(equipoClient.obtenerEquipoPorId(5L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.guardar(requestDTO);
        });

        assertEquals("El equipo no existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


}
