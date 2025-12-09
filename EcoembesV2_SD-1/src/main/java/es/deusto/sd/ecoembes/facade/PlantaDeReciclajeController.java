//Codigo generado con ayuda de gemini para implementacion masiva de contenedores.
package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.service.PlantaDeReciclajeService;
import es.deusto.sd.ecoembes.dto.PlantaDTO;
import es.deusto.sd.ecoembes.dto.AsignacionMasivaDTO; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plantas")
@Tag(name = "Planta Controller", description = "Operaciones de Plantas")
public class PlantaDeReciclajeController {

    private final PlantaDeReciclajeService plantaService;

    public PlantaDeReciclajeController(PlantaDeReciclajeService plantaService) {
        this.plantaService = plantaService;
    }
    
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
     * Asignación MASIVA de contenedores a una planta.
     * Cumple con:
     * 1. Path Variable para ID Planta.
     * 2. Body para lista de contenedores y token.
     */
    @Operation(summary = "Asignar múltiples contenedores a una planta")
    @PostMapping("/{plantaId}/asignar") 
    public ResponseEntity<Map<String, Object>> crearAsignacionMasiva(
            
            @Parameter(description = "ID de la Planta de Reciclaje", required = true)
            @PathVariable("plantaId") long idPlantaDeReciclaje, // <--- PATH VARIABLE
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DTO con lista de contenedores y token", required = true)
            @RequestBody AsignacionMasivaDTO request // <--- BODY con datos
            ) {
        
        try {
            // Llamada al servicio con la lista de IDs
            List<Asignacion> asignaciones = plantaService.asignarContenedoresMasivos(
                    idPlantaDeReciclaje,
                    request.getContainerIds(),
                    request.getToken()
            );
            
            // Construimos respuesta con resumen
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Proceso de asignación finalizado");
            respuesta.put("totalAsignados", asignaciones.size());
            respuesta.put("plantaId", idPlantaDeReciclaje);
            
            List<Long> idsProcesados = new ArrayList<>();
            for(Asignacion a : asignaciones) {
                idsProcesados.add(a.getContainer().getId());
            }
            respuesta.put("contenedoresProcesados", idsProcesados);
            
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
            
        } catch (SecurityException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("error", "Error interno"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}