package com.torneos.usuarios.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torneos.usuarios.assemblers.UsuariosModelAssembler;
import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Rol;
import com.torneos.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;


@WebMvcTest(UsuarioController.class)
@Import(UsuariosModelAssembler.class)
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
        responseDTO = new UsuarioResponseDTO(1L, "leonel", "leo@test.com", Rol.ADMIN, true, 5L);

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombreUsuario("leonel");
        requestDTO.setCorreo("leo@test.com");
        requestDTO.setRol(Rol.ADMIN);
        requestDTO.setEquipoId(5L);
    }
    @Test
    public void testBuscarPorId() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/usuarios/1")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("leonel"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.todos-los-usuarios.href").exists());
    }
    @Test
    public void testListarTodos() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }
    @Test
    public void testObtenerActivos() throws Exception {
        when(usuarioService.obtenerActivos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios/activos")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }
    @Test
    public void testCrearUsuario() throws Exception {
        when(usuarioService.guardar(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(1L))
                .andExpect(jsonPath("$._links.self.href").exists());
    }
    @Test
    public void testBuscarPorCorreo() throws Exception {
        when(usuarioService.buscarPorCorreo("leo@test.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/usuarios/buscar/correo")
                        .param("correo", "leo@test.com")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("leo@test.com"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }
    @Test
    public void testBuscarPorNombre() throws Exception {
        when(usuarioService.buscarPorNombreUsuario("leonel")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/usuarios/buscar/nombre")
                        .param("nombreUsuario", "leonel")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("leonel"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }
}
