package com.torneos.usuarios.dto;

import com.torneos.usuarios.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long usuarioId;
    private String nombreUsuario;
    private String correo;
    private Rol rol;
    private Boolean activo;
    private Long equipoId;


}
