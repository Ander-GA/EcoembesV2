//Codigo generado con ayuda de gemini para implementacion masiva de contenedores.
package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.dao.PlantaDeReciclajeRepository;
import es.deusto.sd.ecoembes.dao.AsignacionRepository;
import es.deusto.sd.ecoembes.dao.ContainerRepository;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.Empleado;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;
import es.deusto.sd.ecoembes.external.RecyclingGatewayFactory;
import es.deusto.sd.ecoembes.external.IRecyclingPlantGateway;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlantaDeReciclajeService {

    private final PlantaDeReciclajeRepository plantaRepository;
    private final ContainerRepository containerRepository;
    private final AsignacionRepository asignacionRepository;
    private final AuthService authService;
    private final NivelLlenadoService nivelLlenadoService;
    private final RecyclingGatewayFactory gatewayFactory;

    public PlantaDeReciclajeService(PlantaDeReciclajeRepository plantaRepository,
                                    ContainerRepository containerRepository, 
                                    AsignacionRepository asignacionRepository,
                                    AuthService authService, 
                                    NivelLlenadoService nivelLlenadoService,
                                    RecyclingGatewayFactory gatewayFactory) {
        this.plantaRepository = plantaRepository;
        this.containerRepository = containerRepository;
        this.asignacionRepository = asignacionRepository;
        this.authService = authService;
        this.nivelLlenadoService = nivelLlenadoService;
        this.gatewayFactory = gatewayFactory;
    }

    public PlantaDeReciclaje createPlanta(PlantaDeReciclaje planta) {
        return plantaRepository.save(planta);
    }

    public double getCapacidadDisponible(long plantaId, LocalDate fecha) {
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        try {
            IRecyclingPlantGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
            Optional<Double> capacidadExterna = gateway.getCapacidadReal(fecha);
            
            if (capacidadExterna.isPresent()) {
                return capacidadExterna.get();
            }
        } catch (Exception e) {
            System.err.println("Advertencia: Fallo al conectar con servicio externo (" + planta.getNombre() + "): " + e.getMessage());
        }

        // Fallback local si falla la conexión
        NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(plantaId, TipoID.PLANTA_DE_RECICLAJE, fecha);
        double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;

        return planta.getCapacidadMaxima() - nivelActual;
    }

    /**
     * Método para asignación MASIVA.
     * Recibe una lista de IDs de contenedores y los asigna uno a uno si hay capacidad.
     */
    public List<Asignacion> asignarContenedoresMasivos(long plantaId, List<Long> containerIds, String token) {
        // 1. Validar Token y Empleado (Solo una vez)
        Empleado empleado = authService.getEmpleadoByToken(token);
        if (empleado == null) {
            throw new SecurityException("Token inválido o sesión expirada.");
        }

        // 2. Obtener Planta y Gateway (Solo una vez)
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
             .orElseThrow(() -> new RuntimeException("Planta no encontrada con ID: " + plantaId));

        IRecyclingPlantGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
        
        // 3. Consultar Capacidad Disponible (Solo una vez al inicio)
        double capacidadDisponible = getCapacidadDisponible(plantaId, LocalDate.now());

        List<Asignacion> asignacionesRealizadas = new ArrayList<>();

        // 4. Iterar sobre los contenedores
        for (Long containerId : containerIds) {
            try {
                Container container = containerRepository.findById(containerId)
                    .orElseThrow(() -> new RuntimeException("Contenedor " + containerId + " no encontrado"));

                // Calcular llenado actual del contenedor
                NivelLlenado nivel = nivelLlenadoService.getUltimoNivelHastaFecha(containerId, TipoID.CONTAINER, LocalDate.now());
                double cantidadBasura = (nivel != null) ? nivel.getNivelDeLlenado() : 0.0;

                // Verificar si cabe en la planta
                if (capacidadDisponible >= cantidadBasura) {
                    // Restamos capacidad para la siguiente iteración (simulación en memoria durante la transacción)
                    capacidadDisponible -= cantidadBasura;

                    // A. Guardar Asignación Local
                    Asignacion asignacion = new Asignacion(LocalDate.now(), empleado, planta, container);
                    asignacion = asignacionRepository.save(asignacion);
                    asignacionesRealizadas.add(asignacion);

                    // B. Notificar a la Planta Externa
                    try {
                        gateway.notificarAsignacion(asignacion);
                    } catch (Exception e) {
                        System.err.println("Error notificando contenedor " + containerId + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("Planta llena. No se pudo asignar contenedor: " + containerId);
                    // Paramos de asignar si ya no cabe más
                    break; 
                }
            } catch (Exception e) {
                System.err.println("Error procesando contenedor " + containerId + ": " + e.getMessage());
            }
        }

        return asignacionesRealizadas;
    }
}