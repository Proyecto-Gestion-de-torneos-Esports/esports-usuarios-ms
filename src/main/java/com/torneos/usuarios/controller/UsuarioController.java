package com.torneos.usuarios.controller;

import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(){
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long usuarioId){
        return usuarioService.buscarPorId(usuarioId).map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(dto));
    }
    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long usuarioId, @Valid @RequestBody UsuarioRequestDTO dto,
            @RequestHeader("usuarioId") Long ejecutorId) {
        return ResponseEntity.ok(usuarioService.actualizar(usuarioId, dto, ejecutorId));
    }
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @RequestHeader("usuarioId") Long ejecutorId) {
        usuarioService.eliminar(usuarioId, ejecutorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerActivos(){
        return ResponseEntity.ok(usuarioService.obtenerActivos());
    }

    @GetMapping("/buscar/correo")
    public ResponseEntity<UsuarioResponseDTO> buscarPorCorreo(@RequestParam String correo){
        return ResponseEntity.ok(usuarioService.buscarPorCorreo(correo));
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<UsuarioResponseDTO> buscarPorNombreUsuario(@RequestParam String nombreUsuario){
        return ResponseEntity.ok(usuarioService.buscarPorNombreUsuario(nombreUsuario));
    }

}
