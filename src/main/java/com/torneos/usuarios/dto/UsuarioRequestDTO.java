package com.torneos.usuarios.dto;

import com.torneos.usuarios.model.Rol;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDTO {

    @Id
    private Long usuarioId;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo valido")
    private String correo;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @Positive(message = "El ID del equipo debe ser mayor a cero")
    private Long equipoId;
}
