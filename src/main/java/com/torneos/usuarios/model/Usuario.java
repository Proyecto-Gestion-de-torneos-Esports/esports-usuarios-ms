package com.torneos.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    private Long usuarioId;

    @Column(name ="nombre_usuario", nullable = false, length = 30, unique = true)
    private String nombreUsuario;

    @Column(nullable = false, length = 100, unique = true)
    private String correo;

    @Enumerated(EnumType.STRING) //Esta anotación permite guardar el rol literal como lo declare en Rol (jugador,coach,arbitro o admin)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /*este campo lo dejo como true porque no es necesario que un usuario se registre en un equipo altiro,
    lo puede hacer pero no es necesario por eso se complemente con el metodo en el microservicio de equipos
    el cual es inscribirIntegrante*/
    @Column(name = "equipo_id", nullable = true)
    private Long equipoId;

}
