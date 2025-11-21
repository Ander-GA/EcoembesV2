package es.deusto.sd.ecoembes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.entity.Container;

@Service
public class ContainerService {

    // Repositorio en memoria para el Prototipo 1
    // (Se vacía si reinicias el servidor)
    private final Map<Long, Container> repositorioEnMemoria = new HashMap<>();
    
    // Generador de IDs para los nuevos contenedores
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Constructor vacío.
     * El DataInitializer se encargará de llamar a 'createContainer'.
     */
    public ContainerService() {
        // No se inicializan datos aquí
    }

    // --- Métodos Requeridos ---

    /**
     * 1. Creación de un nuevo contenedor.
     * (Responde a "Creación de un nuevo contenedor")
     * * El ID se genera aquí; no confiamos en el que viene del DTO.
     */
    public Container createContainer(Container container) {
        // Generamos un ID nuevo y seguro
        Long nuevoId = idGenerator.incrementAndGet();
        container.setId(nuevoId);
        
        // Guardamos en el "repositorio" en memoria
        repositorioEnMemoria.put(nuevoId, container);
        return container;
    }

    /**
     * 2. Consulta de contenedores por zona.
     * (Responde a "Consulta del estado de los contenedores de una zona")
     * * Este método devuelve los contenedores. El Controlador
     * luego usará otro servicio (como NivelDeLlenadoService)
     * para ver el estado de cada uno.
     */
    public List<Container> getContainersByCodigoPostal(int codigoPostal) {
        return repositorioEnMemoria.values().stream()
                // 2. Compara los dos int usando ==
                .filter(container -> container.getCodigoPostal() == codigoPostal)
                .collect(Collectors.toList());
    }
    // --- Métodos de Ayuda (Helpers) ---

    /**
     * 3. Obtener un contenedor por su ID.
     * (Necesario para tu método 'getColor' y 'getUsoPorFecha')
     */
    public Optional<Container> getContainerById(Long id) {
        // Optional es más seguro que devolver null
        return Optional.ofNullable(repositorioEnMemoria.get(id));
    }

    /**
     * 4. Obtener todos los contenedores.
     */
    public List<Container> getAllContainers() {
        return new ArrayList<>(repositorioEnMemoria.values());
    }
    
    // --- NOTAS SOBRE OTROS MÉTODOS ---
    
    /*
     * "Consulta del uso/estado de un contenedor por fecha"
     *
     * Esta lógica NO vive aquí. Vive en el `NivelDeLlenadoService`.
     * El Controlador llamará a:
     * 1. `containerService.getContainerById(id)` (para saber la capacidad)
     * 2. `nivelDeLlenadoService.getHistorialPorFechas(id, fechaInicio, fechaFin)`
     */
}