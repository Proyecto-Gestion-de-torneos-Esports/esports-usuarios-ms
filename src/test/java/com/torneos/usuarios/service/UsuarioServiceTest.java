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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private AuditoriaClient auditoriaClient;

    @MockitoBean
    private EquipoClient equipoClient;

    private Usuario usuarioAdmin;
    private Usuario usuarioNormal;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp(){
        usuarioAdmin = new Usuario();
        usuarioAdmin.setUsuarioId(1L);
        usuarioAdmin.setNombreUsuario("admin_test");
        usuarioAdmin.setRol(Rol.ADMIN);
        usuarioAdmin.setActivo(true);

        usuarioNormal = new Usuario();
        usuarioNormal.setUsuarioId(2L);
        usuarioNormal.setNombreUsuario("jugador_test");
        usuarioNormal.setCorreo("jugador@test.com");
        usuarioNormal.setRol(Rol.JUGADOR);
        usuarioNormal.setEquipoId(10L);
        usuarioNormal.setActivo(true);

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombreUsuario("nuevo_jugador");
        requestDTO.setCorreo("nuevo@test.com");
        requestDTO.setRol(Rol.JUGADOR);
        requestDTO.setEquipoId(10L);

    }

    @Test
    public void testGuardarUsuarioExitoso(){
        when(equipoClient.obtenerEquipoPorId(10L)).thenReturn(Map.of("id", 10L, "nombre", "Equipo FC"));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioNormal);
        doNothing().when(auditoriaClient).generarAuditoria(any(AuditoriaRequestDTO.class));

        UsuarioResponseDTO response = usuarioService.guardar(requestDTO);

        assertNotNull(response);
        assertEquals("jugador_test", response.getNombreUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(auditoriaClient, times(1)).generarAuditoria(any(AuditoriaRequestDTO.class));

    }

    @Test
    public void testActualizarUsuarioPermitido(){
        Long ejecutorId = 1L;
        Long usuarioObjetivoId = 2L;

        when(usuarioRepository.findByUsuarioIdAndActivoTrue(ejecutorId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(usuarioObjetivoId)).thenReturn(Optional.of(usuarioNormal));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioNormal);
        doNothing().when(auditoriaClient).generarAuditoria(any(AuditoriaRequestDTO.class));

        UsuarioResponseDTO response = usuarioService.actualizar(usuarioObjetivoId, requestDTO, ejecutorId);
        assertNotNull(response);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(auditoriaClient, times(1)).generarAuditoria(any(AuditoriaRequestDTO.class));
    }

    @Test
    public void testActualizarUsuarioAccesoDenegado(){
        Long ejecutorId = 2L;
        Long usuarioObjetivoId = 3L;

        when(usuarioRepository.findByUsuarioIdAndActivoTrue(ejecutorId)).thenReturn(Optional.of(usuarioNormal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizar(usuarioObjetivoId, requestDTO, ejecutorId);
        });
        assertTrue(exception.getMessage().contains("Acceso denegado"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
    @Test
    public void testEliminarUsuarioPermitido(){
        Long ejecutorId = 1L;
        Long usuarioObjetivoId = 2L;

        when(usuarioRepository.findByUsuarioIdAndActivoTrue(ejecutorId)).thenReturn(Optional.of(usuarioAdmin));
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(usuarioObjetivoId)).thenReturn(Optional.of(usuarioNormal));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioNormal);
        doNothing().when(auditoriaClient).generarAuditoria(any(AuditoriaRequestDTO.class));

        usuarioService.eliminar(usuarioObjetivoId, ejecutorId);
        assertFalse(usuarioNormal.getActivo()); //Aqui verificamos el borrado logico, mantengo esa forma de borrado.
        verify(usuarioRepository, times(1)).save(usuarioNormal);
        verify(auditoriaClient, times(1)).generarAuditoria(any(AuditoriaRequestDTO.class));
    }
    @Test
    public void testBuscarPorIdExitoso(){
        when(usuarioRepository.findByUsuarioIdAndActivoTrue(2L)).thenReturn(Optional.of(usuarioNormal));
        Optional<UsuarioResponseDTO> response = usuarioService.buscarPorId(2L);

        assertTrue(response.isPresent());
        assertEquals("jugador_test", response.get().getNombreUsuario());
    }
    @Test
    public void testListarTodos(){
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioAdmin, usuarioNormal));
        List<UsuarioResponseDTO> response = usuarioService.listarTodos();
        assertNotNull(response);
        assertEquals(2, response.size());
    }



}
