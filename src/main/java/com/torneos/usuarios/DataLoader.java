package com.torneos.usuarios;

import com.torneos.usuarios.model.Rol;
import com.torneos.usuarios.model.Usuario;
import com.torneos.usuarios.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            System.out.println("La base de datos ya tiene usuarios. Omitiendo generación de datos...");
            return;
        }
        Faker faker = new Faker();
        Random random = new Random();
        Rol[] roles = Rol.values();
        System.out.println("Iniciando DataLoader: Generando datos falsos de Usuarios...");

        for (int i = 1; i <= 30; i++) {
            Usuario usuario = new Usuario();

            usuario.setUsuarioId((long) i);

            usuario.setNombreUsuario(faker.name().username());
            usuario.setCorreo(faker.internet().emailAddress());

            usuario.setRol(roles[random.nextInt(roles.length)]);

            usuario.setActivo(random.nextInt(10) < 8);
            if (random.nextBoolean()) {
                usuario.setEquipoId((long) faker.number().numberBetween(1, 16));
            } else {
                usuario.setEquipoId(null);
            }

            usuarioRepository.save(usuario);
        }

        System.out.println("¡DataLoader finalizado! 30 usuarios falsos generados con éxito.");
    }
}