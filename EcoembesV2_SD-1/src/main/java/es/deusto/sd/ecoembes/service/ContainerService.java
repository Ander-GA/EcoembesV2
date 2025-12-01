package es.deusto.sd.ecoembes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.dao.ContainerRepository;
import es.deusto.sd.ecoembes.entity.Container;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;
    
    public ContainerService(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    public Optional<Container> getContainerById(Long id){
        return containerRepository.findById(id);
    }

    /**
     * Creación de un nuevo contenedor.
     * La base de datos (H2) se encarga de asignar el ID automáticamente.
     */
    public Container createContainer(Container container) {
        return containerRepository.save(container);
    }

    /**
     * Consulta de contenedores por zona.
     */
    public List<Container> getContainersByCodigoPostal(int codigoPostal) {
       return containerRepository.findByCodigoPostal(codigoPostal);
    }
}