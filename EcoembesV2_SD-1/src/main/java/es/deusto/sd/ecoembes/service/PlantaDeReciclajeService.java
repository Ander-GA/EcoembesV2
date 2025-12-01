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
import java.util.Optional;

@Service
public class PlantaDeReciclajeService {

    private final PlantaDeReciclajeRepository plantaRepository;
    private final ContainerRepository containerRepository;
    private final AsignacionRepository asignacionRepository;
    private final AuthService authService;
    private final NivelLlenadoService nivelLlenadoService;
    
    // Inyectamos la factoría
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
        // JPA se encarga del ID
        return plantaRepository.save(planta);
    }

    public double getCapacidadDisponible(long plantaId, LocalDate fecha) {
        // 1. Datos estáticos de BBDD local
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        // 2. Intentar obtener capacidad REAL del servicio externo
        try {
            // Usamos el campo tipoServicio para pedir el gateway correcto
            // Usamos direccion como endpoint (ej: "http://localhost:8080" o "127.0.0.1:9090")
            IRecyclingPlantGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
            
            // Si el servicio externo responde, usamos ese dato
            Optional<Double> capacidadExterna = gateway.getCapacidadReal(planta.getDireccion(), fecha);
            if (capacidadExterna.isPresent()) {
                return capacidadExterna.get();
            }
        } catch (Exception e) {
            System.err.println("Error conectando con servicio externo: " + e.getMessage());
        }

        // 3. Fallback: Si falla el externo, usamos cálculo local (como tenías antes)
        double capacidadMaxima = planta.getCapacidadMaxima();
        NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(plantaId, TipoID.PLANTA_DE_RECICLAJE, fecha);
        double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;

        return capacidadMaxima - nivelActual;
    }

    public Asignacion asignarContenedorAPlanta(long containerId, long plantaId, String token) {
        // Lógica de validación (igual que antes)
        Empleado empleado = authService.getEmpleadoByToken(token);
        if (empleado == null) throw new SecurityException("Token inválido");

        Container container = containerRepository.findById(containerId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
        
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
             .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        // Usar la capacidad calculada (que puede venir del externo)
        double capacidadDisponible = getCapacidadDisponible(plantaId, LocalDate.now());
        
        // Asumiendo que obtenemos el llenado del contenedor
        NivelLlenado nivelContenedor = nivelLlenadoService.getUltimoNivelHastaFecha(containerId, TipoID.CONTAINER, LocalDate.now());
        double cantidadEnContenedor = (nivelContenedor != null) ? nivelContenedor.getNivelDeLlenado() : 0.0;

        if (cantidadEnContenedor > capacidadDisponible) {
            throw new RuntimeException("No hay capacidad disponible");
        }

        Asignacion asignacion = new Asignacion(LocalDate.now(), empleado, planta, container);
        return asignacionRepository.save(asignacion);
    }
}