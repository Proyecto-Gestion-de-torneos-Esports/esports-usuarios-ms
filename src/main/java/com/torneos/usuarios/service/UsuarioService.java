package com.torneos.usuarios.service;

import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import com.torneos.usuarios.webclient.EquipoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EquipoClient equipoClient;

    private UsuarioResponseDTO mapToDto(Usuario usuario){
        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getEquipoId()
        );
    }

    @Transactional
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto){
        validarEquipo(dto.getEquipoId());

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setClave(dto.getClave());
        usuario.setRol(dto.getRol());
        usuario.setEquipoId(dto.getEquipoId());
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
    public Optional<UsuarioResponseDTO> buscarPorId(Long usuarioId){
        Optional<UsuarioResponseDTO> resultado = usuarioRepository.findByUsuarioIdAndActivoTrue(usuarioId).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto-> log.info("Usuario '{}' encontrado", dto.getNombreUsuario()),
                ()-> log.warn("No se encontro ningún usuario activo con el ID: {}", usuarioId)
        );
        return resultado;
    }

    @Transactional
    public Optional<UsuarioResponseDTO> actualizar(Long usuarioId, UsuarioRequestDTO dto){
        return usuarioRepository.findByUsuarioIdAndActivoTrue(usuarioId).map(existente->{
            existente.setNombreUsuario(dto.getNombreUsuario());
            existente.setCorreo(dto.getCorreo());
            existente.setClave(dto.getClave());
            existente.setRol(dto.getRol());

            UsuarioResponseDTO respuesta = mapToDto(usuarioRepository.save(existente));
            log.info("Usuario '{}' (ID: {}) actualizado correctamente", respuesta.getNombreUsuario(), usuarioId);
            return respuesta;
        });
    }

    @Transactional
    public void eliminar(Long usuarioId){
        usuarioRepository.findByUsuarioIdAndActivoTrue(usuarioId).ifPresentOrElse(existente->{
            existente.setActivo(false);
            usuarioRepository.save(existente);
            log.info("Usuario '{}' (ID: {}) desactivado correctamente", existente.getNombreUsuario(), usuarioId);
        },()->{
            log.warn("Eliminación fallida: No se encontró ningún usuario activo con el ID: {}", usuarioId);

        });
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerActivos(){
        List<Usuario> usuariosActivos = usuarioRepository.findByActivoTrue();
        log.info("Hay: {} usuarios activos", usuariosActivos.size());
        return usuariosActivos.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorCorreo(String correo){
        Optional<UsuarioResponseDTO> resultado = usuarioRepository.findByCorreoAndActivoTrue(correo).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto-> log.info("Usuario encontrado con correo: {}", correo),
                ()-> log.warn("No se encontró ningun usuario activo con el correo: {}", correo)
        );
        return resultado;
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorNombreUsuario(String nombreUsuario){
        Optional<UsuarioResponseDTO> resultado = usuarioRepository.findByNombreUsuarioAndActivoTrue(nombreUsuario).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto->log.info("Usuario encontrado con nombre: {}",nombreUsuario),
                ()-> log.warn("No se encontró ningun usuario activo con el nombre {}",nombreUsuario)
        );
        return resultado;
    }

    private void validarEquipo(Long equipoId){
        if (equipoId != null){
                Map<String, Object> equipo = equipoClient.obtenerEquipoPorId(equipoId);
                if (equipo == null || equipo.isEmpty()){
                    throw new RuntimeException("El equipo no existe");
                }
                log.info("Equipo ID {} validado con exito", equipoId);
            }
        }

}
