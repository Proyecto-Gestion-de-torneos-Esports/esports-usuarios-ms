package com.torneos.usuarios.controller;

import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endepoinst para la gestión de usuarios del sistema de gestión de Torneos Esports")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listas todos los usuarios", description = "Retorna una lista completa de los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(){
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los detalles de un usuario específico mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long usuarioId){
        return usuarioService.buscarPorId(usuarioId).map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario en el sistema y genera una auditoría.")
    @ApiResponse(responseCode = "201", description = "Usuario creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente. Requiere permisos administrativos en el Header.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado por rol insuficiente")
    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long usuarioId, @Valid @RequestBody UsuarioRequestDTO dto,
            @RequestHeader("usuarioId") Long ejecutorId) {
        return ResponseEntity.ok(usuarioService.actualizar(usuarioId, dto, ejecutorId));
    }

    @Operation(summary = "Eliminar usuario", description = "Realiza un borrado lógico (desactiva) al usuario indicado. Requiere permisos administrativos.")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado por rol insuficiente")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @RequestHeader("usuarioId") Long ejecutorId) {
        usuarioService.eliminar(usuarioId, ejecutorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar usuarios activos", description = "Filtra y retorna únicamente los usuarios habilitados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerActivos(){
        return ResponseEntity.ok(usuarioService.obtenerActivos());
    }

    @Operation(summary = "Buscar por correo", description = "Encuentra a un usuario mediante su dirección de correo electrónico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/buscar/correo")
    public ResponseEntity<UsuarioResponseDTO> buscarPorCorreo(@RequestParam String correo){
        return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
    }


    @Operation(summary = "Buscar por nombre de usuario", description = "Encuentra a un usuario mediante su nombre de usuario (username).")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<UsuarioResponseDTO> buscarPorNombreUsuario(@RequestParam String nombreUsuario){
        return ResponseEntity.ok(usuarioService.buscarPorNombreUsuario(nombreUsuario));
    }

}
