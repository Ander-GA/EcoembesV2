package es.deusto.sd.ecoembes.facade;

import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.service.AsignacionService;
import es.deusto.sd.ecoembes.service.AuthService; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/asignaciones")
@Tag(name = "Asignacion Controller", description = "Operaciones de Asignación")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final AuthService authService;

    public AsignacionController(AsignacionService asignacionService, AuthService authService) {
        this.asignacionService = asignacionService;
        this.authService = authService;
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
            Asignacion nuevaAsignacion = asignacionService.asignarContenedorAPlanta(
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