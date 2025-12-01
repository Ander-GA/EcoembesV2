package es.deusto.sd.ecoembes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.dao.ContainerRepository;
import es.deusto.sd.ecoembes.entity.Container;

@Service
public class ContainerService {

    // Repositorio en memoria para el Prototipo 1
    // (Se vacía si reinicias el servidor)
	private final ContainerRepository containerRepository;
    
    // Generador de IDs para los nuevos contenedores
    private final AtomicLong idGenerator = new AtomicLong(0);

    public ContainerService(ContainerRepository containerRepository) {
			this.containerRepository = containerRepository;
    }

    // --- Métodos Requeridos ---

    public Optional<Container> getContainerById(Long id){
		return containerRepository.findById(id);
    }
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
        containerRepository.save(container);
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
       return containerRepository.findByCodigoPostal(codigoPostal);
    }
   
  
}