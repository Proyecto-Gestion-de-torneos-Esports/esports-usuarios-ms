package com.torneos.usuarios.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class EquipoClient {

    private final WebClient webClient;

    public EquipoClient(@Value("${equipo-service.url}") String equipoServidor){
        this.webClient = WebClient.builder().baseUrl(equipoServidor).build();
    }

    public Map<String, Object> obtenerEquipoPorId(Long id){
        return this.webClient.get().uri("/{id}", id).retrieve().onStatus(HttpStatusCode::is4xxClientError,
                response-> response.bodyToMono(String.class)
                .map(body-> new RuntimeException("El equipo con ID " + id + " no existe en el sistema"))).bodyToMono(Map.class).block();
    }
}
