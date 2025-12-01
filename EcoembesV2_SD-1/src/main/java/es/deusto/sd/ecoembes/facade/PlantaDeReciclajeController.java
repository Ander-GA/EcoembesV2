package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.service.PlantaDeReciclajeService;
import es.deusto.sd.ecoembes.dto.PlantaDTO;

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
     * Crear una nueva planta de reciclaje.
     * URL: POST /plantas
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
     * Consulta de la capacidad de las plantas de reciclaje.
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
            return ResponseEntity.ok(Map.of("capacidadDisponible", capacidad));
            
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error interno"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Asignación de contenedores a plantas de reciclaje.
     * URL: POST /plantas/asignar  <-- CAMBIO IMPORTANTE: AÑADIDA RUTA ESPECÍFICA
     */
    @Operation(summary = "Asignar un contenedor a una planta de reciclaje")
    @PostMapping("/asignar") // <--- AQUÍ ESTABA EL ERROR
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
                    token 
            );
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("idAsignacion", nuevaAsignacion.getId());
            respuesta.put("fechaAsignacion", nuevaAsignacion.getFechaAsignacion());
            respuesta.put("emailEmpleado", nuevaAsignacion.getEmpleado().getEmail());
            respuesta.put("empleadoNombre", nuevaAsignacion.getEmpleado().getNombre());
            respuesta.put("containerId", nuevaAsignacion.getContainer().getId());
            respuesta.put("plantaId", nuevaAsignacion.getPlantaDeReciclaje().getId());
            
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
            
        } catch (SecurityException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Error interno"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}