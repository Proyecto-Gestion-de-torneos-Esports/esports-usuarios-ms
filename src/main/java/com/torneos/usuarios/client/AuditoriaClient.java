package com.torneos.usuarios.client;

import com.torneos.usuarios.dto.AuditoriaRequestDTO;
import com.torneos.usuarios.dto.AuditoriaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservicio-auditoria", path = "api/auditoria")
//@FeignClient(name = "microservicio-auditoria", url = "http://localhost:8031/api/auditoria")
public interface AuditoriaClient {

    @PostMapping
    AuditoriaResponseDTO generarAuditoria(@RequestBody AuditoriaRequestDTO auditoria);
}
