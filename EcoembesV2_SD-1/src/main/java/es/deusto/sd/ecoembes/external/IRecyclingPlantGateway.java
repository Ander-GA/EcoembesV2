package es.deusto.sd.ecoembes.external;

import java.time.LocalDate;
import java.util.Optional;

import es.deusto.sd.ecoembes.entity.Asignacion;

public interface IRecyclingPlantGateway {
    // Método común para obtener capacidad, sin importar si es REST o Socket
    Optional<Double> getCapacidadReal(LocalDate fecha);
    
    void notificarAsignacion(Asignacion asignacion);
}