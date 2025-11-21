package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona el repositorio central de NivelLlenado.
 * Adaptado 100% a los nombres de tus entidades.
 */
@Service
public class NivelLlenadoService {

    private final List<NivelLlenado> todosLosRegistros = new ArrayList<>();
    
    public NivelLlenadoService() {
        // Vacío. El DataInitializer lo llenará.
    }

    /**
     * Añade un nuevo registro de nivel al repositorio central.
     */
    public NivelLlenado addRegistro(NivelLlenado registro) {
        todosLosRegistros.add(registro);
        return registro;
    }

    /**
     * Obtiene el historial completo de un elemento (Contenedor O Planta),
     * ordenado por fecha (del más antiguo al más nuevo).
     */
    public List<NivelLlenado> getHistorial(long elementoId, TipoID tipo) {
        return todosLosRegistros.stream()
            .filter(reg -> reg.getIdObjetoAsociado() == elementoId && reg.getTipoId() == tipo)
            .sorted(Comparator.comparing(NivelLlenado::getFechaRegistro)) 
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el ÚLTIMO registro de nivel de un elemento.
     * Esencial para saber el estado actual.
     */
    public NivelLlenado getUltimoNivel(long elementoId, TipoID tipo) {
        // Llama al historial completo y coge el último
        return getHistorial(elementoId, tipo).stream()
            .reduce((primero, segundo) -> segundo) // Se queda con el último elemento
            .orElse(null); // Devuelve null si el historial está vacío
    }
    
    /**
     * MÉTODO 2: "Consulta del uso/estado... por fecha"
     * (Este es el método corregido y completado)
     */
    public List<NivelLlenado> getHistorialPorFechas(long elementoId, TipoID tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        return todosLosRegistros.stream()
            .filter(reg -> reg.getIdObjetoAsociado() == elementoId && reg.getTipoId() == tipo)
            // Filtra por rango de fechas
            .filter(reg -> !reg.getFechaRegistro().isBefore(fechaInicio))
            .filter(reg -> !reg.getFechaRegistro().isAfter(fechaFin)) // La parte que faltaba
            .sorted(Comparator.comparing(NivelLlenado::getFechaRegistro))
            .collect(Collectors.toList());
    }

    /**
     * ¡NUEVO MÉTODO AYUDANTE!
     * Obtiene el ÚLTIMO registro de nivel de un elemento, 
     * EN O ANTES de una fecha específica.
     * Esencial para los Métodos 3 y 4.
     */
	public NivelLlenado getUltimoNivelHastaFecha(long elementoId, TipoID tipo, LocalDate fecha) {
	        
	        return todosLosRegistros.stream()
	            // 1. Filtra por el objeto (Ej: Contenedor 1) y el tipo (CONTAINER)
	            .filter(reg -> reg.getIdObjetoAsociado() == elementoId && reg.getTipoId() == tipo)
	            
	            // 2. Filtra solo fechas ANTERIORES o IGUALES a la que buscamos
	            .filter(reg -> !reg.getFechaRegistro().isAfter(fecha))
	            
	            // 3. De los que quedan, coge el que tenga la fecha más alta (el más nuevo)
	            .max(Comparator.comparing(NivelLlenado::getFechaRegistro))
	            
	            // 4. Si no encuentra NINGUNO, devuelve null de forma segura
	            .orElse(null); 
	    }
}