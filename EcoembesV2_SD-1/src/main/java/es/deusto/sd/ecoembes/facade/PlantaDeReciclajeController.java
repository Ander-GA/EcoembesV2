package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.service.PlantaDeReciclajeService;
import es.deusto.sd.ecoembes.dto.PlantaDTO; // ¡Usa tu DTO!

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/plantas")
@Tag(name = "Planta Controller", description = "Operaciones de Plantas")
public class PlantaDeReciclajeController {

    private final PlantaDeReciclajeService plantaService;

    public PlantaDeReciclajeController(PlantaDeReciclajeService plantaService) {
        this.plantaService = plantaService;
    }
    
    /**
     * (Método POST, igual que en ContainerController)
     */
    @Operation(summary = "Crear una nueva planta de reciclaje")
    @PostMapping
    public ResponseEntity<PlantaDTO> createPlanta(@RequestBody PlantaDTO plantaDTO) {
        try {
            PlantaDeReciclaje plantaCreada = plantaService.createPlanta(plantaDTO.toEntity());
            return new ResponseEntity<>(new PlantaDTO(plantaCreada), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * MÉTODO 4: Consulta de la capacidad de las plantas de reciclaje.
     * URL: GET /plantas/{id}/capacidad?fecha=...
     */
    @Operation(summary = "Obtener capacidad disponible de una planta")
    @GetMapping("/{id}/capacidad")
    public ResponseEntity<Map<String, Object>> getCapacidadPlanta(
            @Parameter(name = "id", description = "ID de la planta", required = true)
            @PathVariable("id") long id,
            
            @Parameter(name = "fecha", description = "Fecha (YYYY-MM-DD)", required = true)
            @RequestParam("fecha") LocalDate fecha) {
        
        try {
            double capacidad = plantaService.getCapacidadDisponible(id, fecha);
            // Devolvemos un JSON simple, ej: {"capacidadDisponible": 4500.0}
            return ResponseEntity.ok(Map.of("capacidadDisponible", capacidad));
            
        } catch (RuntimeException e) {
            // Esto captura el "Planta no encontrada"
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error interno"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * MÉTODO 5: Asignación de contenedores a plantas de reciclaje.
     * (¡CON TOKEN OBLIGATORIO!)
     */
    @Operation(summary = "Asignar un contenedor a una planta de reciclaje")
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearAsignacion(
            
            @Parameter(description = "Token de autenticación", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Id del Contenedor", required = true)
            @RequestParam("Id del Contenedor")  long idContainer,
            @Parameter(description = "Id de la Planta de Reciclaje", required = true)
            @RequestParam("Id de la Planta de Reciclaje") long idPlantaDeReciclaje) {
        
        try {
            Asignacion nuevaAsignacion = plantaService.asignarContenedorAPlanta(
            		idContainer,
					idPlantaDeReciclaje,
                    token // Pasamos el token al servicio para la auditoría
            );
            
            // Creamos una respuesta segura con los datos de auditoría
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("idAsignacion", nuevaAsignacion.getId()); // Asumiendo que Asignacion tiene ID
            respuesta.put("fechaAsignacion", nuevaAsignacion.getFechaAsignacion());
            respuesta.put("emailEmpleado", nuevaAsignacion.getEmpleado().getEmail());
            respuesta.put("empleadoNombre", nuevaAsignacion.getEmpleado().getNombre());
            respuesta.put("containerId", nuevaAsignacion.getContainer().getId());
            respuesta.put("plantaId", nuevaAsignacion.getPlantaDeReciclaje().getId());
            
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
            
        } catch (SecurityException e) {
            // Captura el "Token inválido"
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            // Captura "No hay capacidad", "No encontrado", etc.
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error interno"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}