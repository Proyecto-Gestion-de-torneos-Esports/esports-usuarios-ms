package com.torneos.usuarios.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Rol;
import com.torneos.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UsuarioService usuarioService;

    private UsuarioResponseDTO responseDTO;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new UsuarioResponseDTO(
                1L, "jugador_test", "jugador@test.com", Rol.JUGADOR, true, 10L);

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombreUsuario("jugador_test");
        requestDTO.setCorreo("jugador@test.com");
        requestDTO.setRol(Rol.JUGADOR);
        requestDTO.setEquipoId(10L);
    }
    @Test
    public void testListarTodos() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1L))
                .andExpect(jsonPath("$[0].nombreUsuario").value("jugador_test"));
    }
    @Test
    public void testBuscarPorId() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.correo").value("jugador@test.com"));
    }
    @Test
    public void testCrearUsuario() throws Exception {
        when(usuarioService.guardar(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$.nombreUsuario").value("jugador_test"));
    }
    @Test
    public void testActualizarUsuario() throws Exception {
        Long ejecutorId = 2L; // aqui es tipo simulacion de quien ejecuta la orden
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class), eq(ejecutorId))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .header("usuarioId", ejecutorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1L));
    }

    @Test
    public void testEliminarUsuario() throws Exception {
        Long ejecutorId = 2L; //aqui lo mismo simulamos el id de quien ejecuta la orden
        doNothing().when(usuarioService).eliminar(1L, ejecutorId);

        mockMvc.perform(delete("/api/usuarios/1")
                        .header("usuarioId", ejecutorId))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminar(1L, ejecutorId);
    }
}
