package com.torneos.usuarios.service;

import com.torneos.usuarios.client.AuditoriaClient;
import com.torneos.usuarios.client.EquipoClient;
import com.torneos.usuarios.dto.AuditoriaRequestDTO;
import com.torneos.usuarios.dto.UsuarioRequestDTO;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaClient auditoriaClient;
    private final EquipoClient equipoClient;

    //Usuario con sus respectivos atributos
    private UsuarioResponseDTO mapToDto(Usuario usuario){
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(), // CAMBIO: Actualizado a getIdUsuario()
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getEquipoId()
        );
    }

    //creamos un usuarios y el campo activo al iniciar siempre sera true y mandamos a auditoria.
    @Transactional
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto){
        validarEquipo(dto.getEquipoId());

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario()); // CAMBIO: Actualizado a getIdUsuario() según el RequestDTO
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        usuario.setEquipoId(dto.getEquipoId());
        usuario.setActivo(true);

        UsuarioResponseDTO respuesta = mapToDto(usuarioRepository.save(usuario));
        /* Mandamos un log para guardar lo que esta haciendo el sistema por debajo,
         No confundir con los mensajes que van a los microservicios*/
        log.info("Usuario '{}' creado y guardado correctamente", dto.getNombreUsuario());

        String detalleAuditoria = "se creo un nuevo usuario: " + dto.getNombreUsuario() + " con el rol:" + dto.getRol();
        generarAuditoria(detalleAuditoria);
        return respuesta;
    }

    /*se listan todos.El campo transactional con readOnly true
     estamos diciendo que el metodo a continuacion solo es por ejemplo listar, buscar, etc.*/
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Hay {} usuarios en total",usuarios.size());
        return usuarios.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorId(Long idUsuario){ // CAMBIO: usuarioId -> idUsuario
        Optional<UsuarioResponseDTO> resultado = usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario).map(this::mapToDto);

        resultado.ifPresentOrElse(
                dto-> log.info("Usuario '{}' encontrado", dto.getNombreUsuario()),
                ()-> log.warn("No se encontro ningún usuario activo con el ID: {}", idUsuario)
        );
        return resultado;
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long idUsuario, UsuarioRequestDTO dto, Long ejecutorId) { // CAMBIO: usuarioId -> idUsuario
        Usuario ejecutor = usuarioRepository.findByIdUsuarioAndActivoTrue(ejecutorId)
                .orElseThrow(() -> new java.util.NoSuchElementException("El usuario ejecutor con ID " + ejecutorId + " no existe o está inactivo."));
        String rolEjecutor = ejecutor.getRol().name();

        if (!"ADMIN".equalsIgnoreCase(rolEjecutor) && !"ARBITRO".equalsIgnoreCase(rolEjecutor)) {
            log.warn("Intento de actualización no autorizado por el usuario ID: {}", ejecutorId);
            throw new IllegalArgumentException("Acceso denegado: Tu rol (" + rolEjecutor + ") no tiene permisos para actualizar usuarios.");
        }
        Usuario existente = usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario) // CAMBIO: usuarioId -> idUsuario
                .orElseThrow(() -> {
                    log.warn("Actualización fallida: No se encontró ningún usuario activo con el ID: {}", idUsuario); // CAMBIO
                    return new java.util.NoSuchElementException("No se encontró ningún usuario activo con el ID: " + idUsuario); // CAMBIO
                });
        existente.setNombreUsuario(dto.getNombreUsuario());
        existente.setCorreo(dto.getCorreo());
        existente.setRol(dto.getRol());
        Usuario usuarioGuardado = usuarioRepository.save(existente);
        UsuarioResponseDTO respuesta = mapToDto(usuarioGuardado);
        log.info("Usuario '{}' (ID: {}) actualizado correctamente por el ejecutor ID: {}",
                respuesta.getNombreUsuario(), idUsuario, ejecutorId); // CAMBIO
        String detalleAuditoria = "Se actualizo el Usuario con el ID: " + idUsuario; // CAMBIO
        generarAuditoria(detalleAuditoria);

        return respuesta;
    }

    //Metodo con transactional sin readOnly true porque estamos realizando una accion que requiere ingresar datos
    @Transactional
    public void eliminar(Long idUsuario, Long ejecutorId) { // CAMBIO: usuarioId -> idUsuario
        Usuario ejecutor = usuarioRepository.findByIdUsuarioAndActivoTrue(ejecutorId)
                .orElseThrow(() -> new java.util.NoSuchElementException("El usuario ejecutor con ID " + ejecutorId + " no existe o está inactivo."));
        String rol = ejecutor.getRol().name();
        if (!"ADMIN".equalsIgnoreCase(rol) && !"ARBITRO".equalsIgnoreCase(rol)) {
            log.warn("Intento de eliminación no autorizado por el usuario ID: {}", ejecutorId);
            throw new IllegalArgumentException("Acceso denegado: Tu rol (" + rol + ") no tiene permisos para dar de baja a usuarios.");
        }
        Usuario existente = usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario) // CAMBIO: usuarioId -> idUsuario
                .orElseThrow(() -> {
                    log.warn("Eliminación fallida: No se encontró ningún usuario activo con el ID: {}", idUsuario); // CAMBIO
                    return new java.util.NoSuchElementException("No se encontró ningún usuario activo con el ID: " + idUsuario); // CAMBIO
                });
        existente.setActivo(false);
        usuarioRepository.save(existente);
        log.info("Usuario '{}' (ID: {}) desactivado correctamente por el ejecutor ID: {}",
                existente.getNombreUsuario(), idUsuario, ejecutorId); // CAMBIO
        String detalleAuditoria = "Se elimino el usuario con ID " + idUsuario; // CAMBIO
        generarAuditoria(detalleAuditoria);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerActivos(){
        List<Usuario> usuariosActivos = usuarioRepository.findByActivoTrue();
        log.info("Hay: {} usuarios activos", usuariosActivos.size());
        return usuariosActivos.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorCorreo(String correo){
        return usuarioRepository.findByCorreoAndActivoTrue(correo).map(usuario -> {
            log.info("Usuario encontrado con correo: {}", correo);
            return mapToDto(usuario);
        }) .orElseThrow(()->{
            log.warn("No se encontró ningun usuario activo con el correo: {}", correo);
            return new java.util.NoSuchElementException("No se encontro ningun usuario activo con el correo: " + correo);
        });
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorNombreUsuario(String nombreUsuario){
        return usuarioRepository.findByNombreUsuarioAndActivoTrue(nombreUsuario).map(usuario -> {
            log.info("Usuario encontrado con nombre de usuario: {}", nombreUsuario);
            return mapToDto(usuario);
        }).orElseThrow(()->{
            log.warn("No se encontro ningun usuario activo con el nombre: {}", nombreUsuario);
            return new java.util.NoSuchElementException("No se encontro ningun usuario activo con el nombre: " + nombreUsuario);
        });
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

    public void generarAuditoria(String detalle){
        AuditoriaRequestDTO dto = new AuditoriaRequestDTO();
        LocalDate ahora = LocalDate.now();
        dto.setDetalle(detalle);
        dto.setFecha(ahora);
        auditoriaClient.generarAuditoria(dto);
        log.info("Auditoria generada con exito!");
    }
}