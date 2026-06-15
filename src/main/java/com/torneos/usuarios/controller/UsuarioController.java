package com.torneos.usuarios.controller;

import com.torneos.usuarios.assemblers.UsuariosModelAssembler;
import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endepoinst para la gestión de usuarios del sistema de gestión de Torneos Esports")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuariosModelAssembler assembler;

    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista completa de los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUGADOR', 'ARBITRO', 'COACH')")
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioResponseDTO>> listarTodos(){
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.listarTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioController.class).listarTodos()).withSelfRel());
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los detalles de un usuario específico mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUGADOR', 'ARBITRO', 'COACH')")
    @GetMapping(value = "/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorId(@PathVariable Long usuarioId){
        return usuarioService.buscarPorId(usuarioId)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un usuario en el sistema y genera una auditoría.")
    @ApiResponse(responseCode = "201", description = "Usuario creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de un usuario existente. Requiere permisos administrativos en el Header.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado por rol insuficiente")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    @PutMapping(value = "/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> actualizar(@PathVariable Long usuarioId,
                                                                      @Valid @RequestBody UsuarioRequestDTO dto,
                                                                      @RequestHeader("usuarioId") Long ejecutorId) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(usuarioId, dto, ejecutorId);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @Operation(summary = "Eliminar usuario", description = "Realiza un borrado lógico (desactiva) al usuario indicado. Requiere permisos administrativos.")
    @ApiResponse(responseCode = "204", description = "Usuario desactivado correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado por rol insuficiente")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @RequestHeader("usuarioId") Long ejecutorId) {
        usuarioService.eliminar(usuarioId, ejecutorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar usuarios activos", description = "Filtra y retorna únicamente los usuarios habilitados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO', 'JUGADOR', 'COACH')")
    @GetMapping(value = "/activos", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioResponseDTO>> obtenerActivos(){
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.obtenerActivos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioController.class).obtenerActivos()).withSelfRel());
    }

    @Operation(summary = "Buscar por correo", description = "Encuentra a un usuario mediante su dirección de correo electrónico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO', 'JUGADOR', 'COACH')")
    @GetMapping(value = "/buscar/correo", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorCorreo(@RequestParam String correo){
        return ResponseEntity.ok(assembler.toModel(usuarioService.buscarPorCorreo(correo)));
    }

    @Operation(summary = "Buscar por nombre de usuario", description = "Encuentra a un usuario mediante su nombre de usuario (username).")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO', 'JUGADOR', 'COACH')")
    @GetMapping(value = "/buscar/nombre", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorNombreUsuario(@RequestParam String nombreUsuario){
        return ResponseEntity.ok(assembler.toModel(usuarioService.buscarPorNombreUsuario(nombreUsuario)));
    }

}
