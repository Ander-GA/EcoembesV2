package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.Empleado;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionService {

    // Repositorio de asignaciones (solo para auditoría)
    private final List<Asignacion> repositorioAsignaciones = new ArrayList<>();

    // Necesitamos todos los servicios para la lógica
    private final AuthService authService;
    private final ContainerService containerService;
    private final PlantaDeReciclajeService plantaService;
    private final NivelLlenadoService nivelLlenadoService;

    public AsignacionService(AuthService authService, ContainerService containerService,
                             PlantaDeReciclajeService plantaService, NivelLlenadoService nivelLlenadoService) {
        this.authService = authService;
        this.containerService = containerService;
        this.plantaService = plantaService;
        this.nivelLlenadoService = nivelLlenadoService;
    }

    /**
     * MÉTODO 5: Asignación de contenedores a plantas de reciclaje.
     */
    public Asignacion asignarContenedorAPlanta(long containerId, long plantaId, String token) {
        
        // 1. Obtener Empleado (para auditoría)
        Empleado empleado = authService.getEmpleadoByToken(token);
        if (empleado == null) {
            throw new SecurityException("Token inválido");
        }

        // 2. Obtener Contenedor y su Nivel actual
        Container container = containerService.getContainerById(containerId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
            
        NivelLlenado nivelContenedor = nivelLlenadoService.getUltimoNivelHastaFecha(
            containerId, TipoID.CONTAINER, LocalDate.now());
        
        double cantidadEnContenedor = (nivelContenedor != null) ? nivelContenedor.getNivelDeLlenado() : 0.0;
        
        // 3. Obtener Planta y su Capacidad actual
        PlantaDeReciclaje planta = plantaService.getPlantaById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));
            
        double capacidadDisponible = plantaService.getCapacidadDisponible(plantaId, LocalDate.now());

        // 4. Lógica de negocio
        if (cantidadEnContenedor > capacidadDisponible) {
            throw new RuntimeException("No hay capacidad disponible en la planta para este contenedor.");
        }
        
        // 5. ¡Éxito! Creamos la asignación para la auditoría
        Asignacion asignacion = new Asignacion(LocalDate.now(), empleado, planta, container);
        repositorioAsignaciones.add(asignacion);
        
        
        return asignacion;
    }
}