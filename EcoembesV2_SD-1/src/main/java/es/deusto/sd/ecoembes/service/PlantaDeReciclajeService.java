package es.deusto.sd.ecoembes.service;

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

    // Repositorio en memoria (como en ContainerService)
    private final Map<Long, PlantaDeReciclaje> repositorioEnMemoria = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    // Necesitamos el NivelLlenadoService para calcular la capacidad
    private final NivelLlenadoService nivelLlenadoService;

    // Inyección de dependencias
    @Autowired
    public PlantaDeReciclajeService(NivelLlenadoService nivelLlenadoService) {
        this.nivelLlenadoService = nivelLlenadoService;
    }

    /**
     * (Método para POST) Crea una nueva planta de reciclaje.
     */
    public PlantaDeReciclaje createPlanta(PlantaDeReciclaje planta) {
        Long nuevoId = idGenerator.incrementAndGet();
        planta.setId(nuevoId);
        repositorioEnMemoria.put(nuevoId, planta);
        return planta;
    }

    /**
     * (Método para GET) Obtiene una planta por su ID.
     */
    public Optional<PlantaDeReciclaje> getPlantaById(Long id) {
        return Optional.ofNullable(repositorioEnMemoria.get(id));
    }

    /**
     * MÉTODO 4: Consulta de la capacidad de las plantas de reciclaje.
     */
    public double getCapacidadDisponible(long plantaId, LocalDate fecha) {
        // 1. Buscamos la planta
        Optional<PlantaDeReciclaje> plantaOpt = getPlantaById(plantaId);
        
        if (plantaOpt.isEmpty()) {
            throw new RuntimeException("Planta no encontrada");
        }
        
        PlantaDeReciclaje planta = plantaOpt.get();
        double capacidadMaxima = planta.getCapacidadMaxima();

        // 2. Buscamos su último nivel de llenado en esa fecha
        NivelLlenado ultimoNivel = nivelLlenadoService.getUltimoNivelHastaFecha(plantaId, TipoID.PLANTA_DE_RECICLAJE, fecha);

        double nivelActual = (ultimoNivel != null) ? ultimoNivel.getNivelDeLlenado() : 0.0;

        // 3. Calculamos la capacidad disponible
        return capacidadMaxima - nivelActual;
    }
}