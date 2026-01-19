package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.dto.ContainerDTO;
import es.deusto.sd.ecoembes.dto.NivelLlenadoDTO;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.service.ContainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/contenedores")
@Tag(name = "Container Controller", description = "Operaciones de Contenedores")
public class ContainerController {

    private final ContainerService containerService;

    // Ya no necesitamos inyectar NivelLlenadoService ni AuthService aquí, 
    // porque el ContainerService se encarga de todo. ¡Más limpio!
    public ContainerController(ContainerService containerService) {
        this.containerService = containerService;
    }

    @Operation(summary = "Crear un nuevo contenedor")
    @PostMapping
    public ResponseEntity<ContainerDTO> createContainer(@RequestBody ContainerDTO containerDTO) {
        try {
            Container creado = containerService.createContainer(containerDTO.toEntity());
            return new ResponseEntity<>(new ContainerDTO(creado), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Actualizar un contenedor existente")
    @PutMapping("/{id}")
    public ResponseEntity<ContainerDTO> updateContainer(@PathVariable("id") Long id, @RequestBody ContainerDTO containerDTO) {
        Optional<Container> actualizado = containerService.updateContainer(id, containerDTO.toEntity());
        return actualizado.map(c -> ResponseEntity.ok(new ContainerDTO(c)))
                          .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // --- MÉTODOS REFACTORIZADOS ---

    @Operation(summary = "Obtener historial (Devuelve %)")
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<NivelLlenadoDTO>> getHistorialContenedor(
            @PathVariable("id") Long id,
            @RequestParam("fechaInicio") LocalDate fechaInicio,
            @RequestParam("fechaFin") LocalDate fechaFin) {

        // Toda la lógica matemática ahora está en el servicio
        List<NivelLlenadoDTO> historial = containerService.getHistorialConPorcentaje(id, fechaInicio, fechaFin);
        
        if (historial.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(historial);
    }

    @Operation(summary = "Obtener estado de contenedores por zona")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getEstadoContenedoresPorZona(
            @RequestParam("codigoPostal") int codigoPostal,
            @RequestParam("fecha") LocalDate fecha) {

        // Toda la lógica de bucles y mapas ahora está en el servicio
        List<Map<String, Object>> estados = containerService.getEstadoZona(codigoPostal, fecha);
        
        if (estados.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return ResponseEntity.ok(estados);
    }
}