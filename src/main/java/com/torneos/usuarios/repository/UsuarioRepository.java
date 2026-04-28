package com.torneos.usuarios.repository;

import com.torneos.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Metodos personalizados

    List<Usuario> findByActivoTrue(); //Lista todos los usuarios que esten activos

    Optional<Usuario> findByCorreoAndActivoTrue(String correo); //Busca un usuario mediante su correo

    Optional<Usuario> findByNombreUsuarioAndActivoTrue(String nombreUsuario); //Busca un usuario por su nombre de usuario

}
