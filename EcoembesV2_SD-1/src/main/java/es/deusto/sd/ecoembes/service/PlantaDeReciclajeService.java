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
        // 1. Obtener datos de la planta de BBDD
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        try {
            // 2. Obtener el Gateway adecuado (Factory)
            IRecyclingPlantGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
            
            // 3. Llamada al servicio externo (Polimorfismo)
            // Nota: Ya no pasamos la URL, el gateway la tiene configurada internamente
            Optional<Double> capacidadExterna = gateway.getCapacidadReal(fecha);
            
            if (capacidadExterna.isPresent()) {
                return capacidadExterna.get();
            }
        } catch (Exception e) {
            System.err.println("Advertencia: Fallo al conectar con servicio externo (" + planta.getNombre() + "): " + e.getMessage());
        }

        // 4. Fallback: Si falla el externo o no devuelve dato, usamos cálculo local
        NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(plantaId, TipoID.PLANTA_DE_RECICLAJE, fecha);
        double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;

        return planta.getCapacidadMaxima() - nivelActual;
    }

    public Asignacion asignarContenedorAPlanta(long containerId, long plantaId, String token) {
        // 1. Validar Token y Empleado
        Empleado empleado = authService.getEmpleadoByToken(token);
        if (empleado == null) {
            throw new SecurityException("Token inválido o sesión expirada.");
        }

        // 2. Recuperar Entidades
        Container container = containerRepository.findById(containerId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con ID: " + containerId));
        
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
             .orElseThrow(() -> new RuntimeException("Planta no encontrada con ID: " + plantaId));

        // 3. Verificar Capacidad Disponible en la Planta
        // (Esto llama internamente al Gateway para preguntar a la planta externa)
        double capacidadDisponible = getCapacidadDisponible(plantaId, LocalDate.now());
        
        // Obtener el nivel actual del contenedor
        NivelLlenado nivelContenedor = nivelLlenadoService.getUltimoNivelHastaFecha(containerId, TipoID.CONTAINER, LocalDate.now());
        double cantidadEnContenedor = (nivelContenedor != null) ? nivelContenedor.getNivelDeLlenado() : 0.0;

        if (cantidadEnContenedor > capacidadDisponible) {
            throw new RuntimeException("Operación denegada: No hay capacidad suficiente en la planta.");
        }

        // 4. Guardar la Asignación en Base de Datos Local (Ecoembes)
        Asignacion asignacion = new Asignacion(LocalDate.now(), empleado, planta, container);
        Asignacion nuevaAsignacion = asignacionRepository.save(asignacion);

        // 5. Notificar a la Planta Externa (Envío de datos de asignación)
        try {
            IRecyclingPlantGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
            
            // Polimorfismo: Envía JSON (REST) o Trama de texto (Socket) según corresponda
            gateway.notificarAsignacion(nuevaAsignacion);
            
        } catch (Exception e) {
            // Logueamos el error pero NO fallamos la transacción principal
            System.err.println("Advertencia: Asignación guardada localmente, pero falló la notificación a la planta externa: " + e.getMessage());
        }

        return nuevaAsignacion;
    }
}