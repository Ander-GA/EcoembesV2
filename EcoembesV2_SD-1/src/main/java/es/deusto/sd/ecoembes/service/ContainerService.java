package es.deusto.sd.ecoembes.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy; // Importante para evitar ciclos
import org.springframework.stereotype.Service;

import es.deusto.sd.ecoembes.dao.ContainerRepository;
import es.deusto.sd.ecoembes.dto.NivelLlenadoDTO; // Usamos el DTO de servidor
import es.deusto.sd.ecoembes.entity.Color;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;

@Service
public class ContainerService {

    private final ContainerRepository containerRepository;
    private final NivelLlenadoService nivelLlenadoService; // Necesitamos esto

    // Usamos @Lazy en NivelLlenadoService por si hubiera dependencias circulares
    public ContainerService(ContainerRepository containerRepository, 
                            @Lazy NivelLlenadoService nivelLlenadoService) {
        this.containerRepository = containerRepository;
        this.nivelLlenadoService = nivelLlenadoService;
    }

    public Optional<Container> getContainerById(Long id){
        return containerRepository.findById(id);
    }

    public Container createContainer(Container container) {
        return containerRepository.save(container);
    }

    // --- LÓGICA DE NEGOCIO MOVIDA DESDE EL CONTROLADOR ---

    public Optional<Container> updateContainer(Long id, Container datosNuevos) {
        Optional<Container> existente = containerRepository.findById(id);
        if (existente.isPresent()) {
            Container c = existente.get();
            if (datosNuevos.getDireccion() != null) c.setDireccion(datosNuevos.getDireccion());
            if (datosNuevos.getCodigoPostal() > 0) c.setCodigoPostal(datosNuevos.getCodigoPostal());
            if (datosNuevos.getCapacidad() > 0) c.setCapacidad(datosNuevos.getCapacidad());
            return Optional.of(containerRepository.save(c));
        }
        return Optional.empty();
    }

    // Lógica del Historial con Porcentajes
    public List<NivelLlenadoDTO> getHistorialConPorcentaje(Long id, LocalDate inicio, LocalDate fin) {
        Optional<Container> cOpt = containerRepository.findById(id);
        if (cOpt.isEmpty()) return new ArrayList<>();

        double capacidadMax = cOpt.get().getCapacidad();
        List<NivelLlenado> historial = nivelLlenadoService.getHistorialPorFechas(id, TipoID.CONTAINER, inicio, fin);

        return historial.stream().map(n -> {
            double porcentaje = (n.getNivelDeLlenado() / capacidadMax) * 100.0;
            if (porcentaje > 100.0) porcentaje = 100.0;
            return new NivelLlenadoDTO(n.getId(), porcentaje, n.getFechaRegistro());
        }).collect(Collectors.toList());
    }

    // Lógica del Estado de Zona
    public List<Map<String, Object>> getEstadoZona(int codigoPostal, LocalDate fecha) {
        List<Container> contenedores = containerRepository.findByCodigoPostal(codigoPostal);
        
        return contenedores.stream().map(c -> {
            NivelLlenado ultimo = nivelLlenadoService.getUltimoNivelHastaFecha(c.getId(), TipoID.CONTAINER, fecha);
            double nivel = (ultimo != null) ? ultimo.getNivelDeLlenado() : 0.0;
            Color color = c.calcularColor(nivel);

            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("direccion", c.getDireccion());
            map.put("capacidad", c.getCapacidad());
            map.put("nivelEnFecha", nivel);
            map.put("colorEnFecha", color);
            return map;
        }).collect(Collectors.toList());
    }
}