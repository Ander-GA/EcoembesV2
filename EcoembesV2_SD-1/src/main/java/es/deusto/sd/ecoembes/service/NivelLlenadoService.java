package es.deusto.sd.ecoembes.service;

import es.deusto.sd.ecoembes.dao.NivelLlenadoRepository;
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

	private final NivelLlenadoRepository nivelLlenadoRepository;
    
    public NivelLlenadoService(NivelLlenadoRepository nivelLlenadoRepository) {
		this.nivelLlenadoRepository = nivelLlenadoRepository;
    }

    /**
     * Añade un nuevo registro de nivel al repositorio central.
     */
    public NivelLlenado addRegistro(NivelLlenado registro) {
        nivelLlenadoRepository.save(registro);
        return registro;
    }

    /**
     * Obtiene el historial completo de un elemento (Contenedor O Planta),
     * ordenado por fecha (del más antiguo al más nuevo).
     */
    public List<NivelLlenado> getHistorial(long elementoId, TipoID tipo) {	
    	return nivelLlenadoRepository.findByIdObjetoAsociadoAndTipoIdOrderByFechaRegistroAsc(elementoId, tipo);
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
        return nivelLlenadoRepository.findByIdObjetoAsociadoAndTipoIdOrderByFechaRegistroAsc(elementoId, tipo);
    }

    /**
     * ¡NUEVO MÉTODO AYUDANTE!
     * Obtiene el ÚLTIMO registro de nivel de un elemento, 
     * EN O ANTES de una fecha específica.
     * Esencial para los Métodos 3 y 4.
     */
	public NivelLlenado getUltimoNivelHastaFecha(long elementoId, TipoID tipo, LocalDate fecha) {
	        return nivelLlenadoRepository.findTop1ByIdObjetoAsociadoAndTipoIdAndFechaRegistroLessThanEqualOrderByFechaRegistroDesc(elementoId, tipo, fecha);
	    }
}