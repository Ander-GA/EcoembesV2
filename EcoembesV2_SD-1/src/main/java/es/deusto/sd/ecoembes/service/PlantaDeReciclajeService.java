package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.dao.AsignacionRepository;

import es.deusto.sd.ecoembes.dao.ContainerRepository;
import es.deusto.sd.ecoembes.dao.PlantaDeReciclajeRepository;
import es.deusto.sd.ecoembes.entity.Asignacion;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.Empleado;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.service.NivelLlenadoService;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PlantaDeReciclajeService {

	private final PlantaDeReciclajeRepository plantaRepository;
	private final ContainerRepository containerRepository;
	private final AsignacionRepository asignacionRepository;
    private final AuthService authService;
    private final AtomicLong idGenerator = new AtomicLong(0);

    // Necesitamos el NivelLlenadoService para calcular la capacidad
    private final NivelLlenadoService nivelLlenadoService;
    private final ContainerService containerService;

    // Inyección de dependencias
    public PlantaDeReciclajeService(PlantaDeReciclajeRepository plantaRepository,
			ContainerRepository containerRepository, AsignacionRepository asignacionRepository,AuthService authService, NivelLlenadoService nivelLlenadoService,
			ContainerService containerService) {
		super();
		this.plantaRepository = plantaRepository;
		this.containerRepository = containerRepository;
		this.asignacionRepository = asignacionRepository;
		this.authService = authService;
		this.nivelLlenadoService = nivelLlenadoService;
		this.containerService = containerService;
	}
    /**
     * (Método para POST) Crea una nueva planta de reciclaje.
     */
    public PlantaDeReciclaje createPlanta(PlantaDeReciclaje planta) {
        Long nuevoId = idGenerator.incrementAndGet();
        planta.setId(nuevoId);
        plantaRepository.save(planta);
        return planta;
    }

    public Asignacion asignarContenedorAPlanta(long containerId, long plantaId, String token) {
        
        // 1. Obtener Empleado (para auditoría)
        Empleado empleado = authService.getEmpleadoByToken(token);
        if (empleado == null) {
            throw new SecurityException("Token inválido");
        }

        // 2. Obtener Contenedor y su Nivel actual
        Container container = containerRepository.findById(containerId)
            .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
            /*
             * 	LO DE NIVEL DE LLENADO HABRA QUE CAMBIAR EL METODO PARA LA PERSISTENCIA
             * */
        NivelLlenado nivelContenedor = nivelLlenadoService.getUltimoNivelHastaFecha(
            containerId, TipoID.CONTAINER, LocalDate.now());
        
        double cantidadEnContenedor = (nivelContenedor != null) ? nivelContenedor.getNivelDeLlenado() : 0.0;
        
        // 3. Obtener Planta y su Capacidad actual
        PlantaDeReciclaje planta = plantaRepository.findById(plantaId)
            .orElseThrow(() -> new RuntimeException("Planta no encontrada"));
            
        double capacidadDisponible = getCapacidadDisponible(plantaId, LocalDate.now());

        // 4. Lógica de negocio
        if (cantidadEnContenedor > capacidadDisponible) {
            throw new RuntimeException("No hay capacidad disponible en la planta para este contenedor.");
        }
        
        // 5. ¡Éxito! Creamos la asignación para la auditoría
        Asignacion asignacion = new Asignacion(LocalDate.now(), empleado, planta, container);

        asignacionRepository.save(asignacion);
        
        
        return asignacion;
    }
    
    
    /**
     * (Método para GET) Obtiene una planta por su ID.
     */

    /**
     * MÉTODO 4: Consulta de la capacidad de las plantas de reciclaje.
     */
    public double getCapacidadDisponible(long plantaId, LocalDate fecha) {
        // 1. Buscamos la planta
        Optional<PlantaDeReciclaje> plantaOpt = plantaRepository.findById(plantaId);
        
        if (plantaOpt.isEmpty()) {
            throw new RuntimeException("Planta no encontrada");
        }
        
        PlantaDeReciclaje planta = plantaOpt.get();
        double capacidadMaxima = planta.getCapacidadMaxima();

        // 2. Buscamos su último nivel de llenado en esa fecha
        /*
         * SEGURAMENTE CAMBIAR NIVEL DE LLENADO POR LA PERSISTENCIA
         * */
        NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(plantaId, TipoID.PLANTA_DE_RECICLAJE, fecha);

        double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;

        // 3. Calculamos la capacidad disponible
        return capacidadMaxima - nivelActual;
    }
}