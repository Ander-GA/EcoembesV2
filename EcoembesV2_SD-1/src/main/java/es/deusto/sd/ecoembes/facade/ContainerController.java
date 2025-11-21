package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.entity.Color;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.Empleado;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;
import es.deusto.sd.ecoembes.service.AuthService;
import es.deusto.sd.ecoembes.service.ContainerService;
import es.deusto.sd.ecoembes.service.NivelLlenadoService;
// ¡¡Importamos tus DTOs!!
import es.deusto.sd.ecoembes.dto.ContainerDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contenedores")
@Tag(name = "Container Controller", description = "Operaciones de Contenedores")
public class ContainerController {

    private final ContainerService containerService;
    private final NivelLlenadoService nivelLlenadoService;
    private final AuthService authService;

    public ContainerController(ContainerService containerService, 
                               NivelLlenadoService nivelLlenadoService,
                               AuthService authService) {
        this.containerService = containerService;
        this.nivelLlenadoService = nivelLlenadoService;
        this.authService = authService;
    }

    /**
     * MÉTODO 1: Creación de un nuevo contenedor.
     * URL: POST /contenedores
     */
    @Operation(summary = "Crear un nuevo contenedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Container successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
    @PostMapping
    public ResponseEntity<ContainerDTO> createContainer(
            
    		@io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Container object to be created",
                    required = true
                )
            @RequestBody ContainerDTO containerDTO) { 
        
        try { 
            Container containerCreado = containerService.createContainer(containerDTO.toEntity());
            
            // Devolvemos el DTO con el ID
            return new ResponseEntity<>(new ContainerDTO(containerCreado), HttpStatus.CREATED);
            
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * MÉTODO 2: Consulta del uso/estado de un contenedor por fecha.
     * URL: GET /contenedores/{id}/historial?fechaInicio=...&fechaFin=...
     */
    @Operation(summary = "Obtener historial de un contenedor por fecha")
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<NivelLlenado>> getHistorialContenedor(
    		@Parameter(name = "id", description = "ID del contenedor", required = true)
    		@PathVariable("id") Long id,

    		@Parameter(name = "fechaInicio", description = "Fecha de inicio (Formato: YYYY-MM-DD)", required = true)
            @RequestParam("fechaInicio") LocalDate fechaInicio,

    		@Parameter(name = "fechaFin", description = "Fecha de fin (Formato: YYYY-MM-DD)", required = true)
            @RequestParam("fechaFin") LocalDate fechaFin) {


        try {
            
            if (containerService.getContainerById(id).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // Usamos las variables LocalDate directamente
            List<NivelLlenado> historial = nivelLlenadoService.getHistorialPorFechas(id, TipoID.CONTAINER, fechaInicio, fechaFin);
            
            if (historial.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * MÉTODO 3: Consulta del estado de los contenedores de una zona.
     * URL: GET /contenedores?codigoPostal=...&fecha=...
     */
    @Operation(summary = "Obtener estado de contenedores por zona y fecha")
    @GetMapping
public ResponseEntity<List<Map<String, Object>>> getEstadoContenedoresPorZona(
    		
    		@Parameter(name = "codigoPostal", description = "Código postal de la zona", required = true)
    		@RequestParam("codigoPostal") int codigoPostal, // Nombre explícito
    		
    		@Parameter(name = "fecha", description = "Fecha (Formato: YYYY-MM-DD)", required = true)
            @RequestParam("fecha") LocalDate fecha) { // Acepta LocalDate (ya probamos que funciona)

        try {
            // 1. Obtiene los contenedores de esa zona
            List<Container> contenedores = containerService.getContainersByCodigoPostal(codigoPostal);
            
            if (contenedores.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // 2. Para cada contenedor, calcula su estado en esa fecha
            List<Map<String, Object>> listaDeEstados = contenedores.stream()
                .map(container -> {
                    
                    // 3. ¡¡AQUÍ ESTÁ LA LÓGICA CLAVE!!
                    // Llama al servicio para encontrar el último nivel EN O ANTES de la 'fecha'
                    NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(container.getId(), TipoID.CONTAINER, fecha);
                    
                    // 4. Obtiene el nivel (o 0.0 si no hay historial)
                    double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;
                    
                    // 5. Calcula el color (este método ya es seguro,)
                    Color colorActual = container.calcularColor(nivelActual);
                    
                    // 6. Crea el objeto JSON de respuesta con los datos que pediste
                    Map<String, Object> estado = new HashMap<>();
                    estado.put("id", container.getId());
                    estado.put("direccion", container.getDireccion());
                    estado.put("capacidad", container.getCapacidad());
                    estado.put("nivelEnFecha", nivelActual); // El nivel que tenía en esa fecha
                    estado.put("colorEnFecha", colorActual);
                    return estado;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(listaDeEstados);
            
        } catch (Exception e) {
            // Si algo falla (como el NivelLlenadoService), dará 500
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}