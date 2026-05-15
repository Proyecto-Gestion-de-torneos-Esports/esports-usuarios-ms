package com.torneos.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "equipos", url = "http://localhost:8002/api/equipos")
public interface EquipoClient {

    @GetMapping("/{equipoId}")
    Map<String, Object> obtenerEquipoPorId(@PathVariable("equipoId") Long equipoId);
}
