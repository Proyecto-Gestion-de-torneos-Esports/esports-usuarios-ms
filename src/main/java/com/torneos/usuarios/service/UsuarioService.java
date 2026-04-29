package com.torneos.usuarios.service;

import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private UsuarioResponseDTO mapToDto(Usuario usuario){
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo()
        );
    }

    @Transactional
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto){
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setClave(dto.getClave());
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);

        UsuarioResponseDTO respuesta = mapToDto(usuarioRepository.save(usuario));
        log.info("Usuario '{}' creado y guardado correctamente", dto.getNombreUsuario());
        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Hay {} usuarios en total",usuarios.size());
        return usuarios.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorId(Long id){
        Optional<UsuarioResponseDTO> resultado = usuarioRepository.findByIdAndActivoTrue(id).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto-> log.info("Usuario '{}' encontrado", dto.getNombreUsuario()),
                ()-> log.warn("No se encontro ningún usuario activo con el ID: {}", id)
        );
        return resultado;
    }

    @Transactional
    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto){
        return usuarioRepository.findByIdAndActivoTrue(id).map(existente->{
            existente.setNombreUsuario(dto.getNombreUsuario());
            existente.setCorreo(dto.getCorreo());
            existente.setClave(dto.getClave());
            existente.setRol(dto.getRol());

            UsuarioResponseDTO respuesta = mapToDto(usuarioRepository.save(existente));
            log.info("Usuario '{}' (ID: {}) actualizado correctamente", respuesta.getNombreUsuario(), id);
            return respuesta;
        });
    }

    @Transactional
    public void eliminar(Long id){
        usuarioRepository.findByIdAndActivoTrue(id).ifPresentOrElse(existente->{
            existente.setActivo(false);
            usuarioRepository.save(existente);
            log.info("Usuario '{}' (ID: {}) desactivado correctamente", existente.getNombreUsuario(), id);
        },()->{
            log.warn("Eliminación fallida: No se encontró ningún usuario activo con el ID: {}", id);

        });
    }
}
