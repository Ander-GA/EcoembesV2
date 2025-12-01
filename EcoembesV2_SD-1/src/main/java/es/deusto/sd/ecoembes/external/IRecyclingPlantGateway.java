package es.deusto.sd.ecoembes.external;

import java.time.LocalDate;
import java.util.Optional;

public interface IRecyclingPlantGateway {
    // Método común para obtener capacidad, sin importar si es REST o Socket
    Optional<Double> getCapacidadReal(String endpoint, LocalDate fecha);
}