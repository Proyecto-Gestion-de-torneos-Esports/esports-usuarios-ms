package com.torneos.usuarios.config;
import com.torneos.usuarios.model.Rol;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception{

        if (usuarioRepository.count() > 0){
            log.info("La base de datos ya tiene {} registros. Omitiendo carga inicial.", usuarioRepository.count());
            return;
        }
        log.info("Base de datos vacía. Cargando usuarios de respaldo");

        usuarioRepository.saveAll(List.of(
                new Usuario(null, "s1mple", "clave123", "s1mple@navi.gg", Rol.JUGADOR, true, 1L),
                new Usuario(null, "ZywOo", "clave123", "zywoo@vitality.gg", Rol.JUGADOR, true, 1L),
                new Usuario(null, "NiKo", "clave123", "niko@g2.gg", Rol.JUGADOR, true, 2L),
                new Usuario(null, "donk", "clave123", "donk@spirit.gg", Rol.JUGADOR, true, 2L),
                new Usuario(null, "Faker", "clave123", "faker@t1.gg", Rol.ADMIN, true, null),
                new Usuario(null, "karrigan", "clave123", "karrigan@faze.gg", Rol.ADMIN, true, null),
                new Usuario(null, "dev1ce", "clave123", "device@astralis.gg", Rol.JUGADOR, true, 3L),
                new Usuario(null, "m0NESY", "clave123", "m0nesy@g2.gg", Rol.JUGADOR, true, 3L),
                new Usuario(null, "Twistzz", "clave123", "twistzz@liquid.gg", Rol.JUGADOR, true, 4L),
                new Usuario(null, "ropz", "clave123", "ropz@faze.gg", Rol.JUGADOR, true, 4L)


        ));

        log.info("Carga de respaldo completada {} usuarios insertados", usuarioRepository.count());

    }
}
