package com.torneos.usuarios.assemblers;

import com.torneos.usuarios.controller.UsuarioController;
import com.torneos.usuarios.dto.UsuarioResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuariosModelAssembler implements RepresentationModelAssembler<UsuarioResponseDTO, EntityModel<UsuarioResponseDTO>> {

    @Override
    public EntityModel<UsuarioResponseDTO> toModel(UsuarioResponseDTO usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).buscarPorId(usuario.getUsuarioId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("todos-los-usuarios"),
                linkTo(methodOn(UsuarioController.class).obtenerActivos()).withRel("usuarios-activos")
        );
    }
}
