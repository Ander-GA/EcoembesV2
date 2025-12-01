package es.deusto.sd.ecoembes.external;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class RecyclingGatewayFactory {
    
    private final Map<String, IRecyclingPlantGateway> gateways;

    // Spring inyecta automáticamente todos los beans que implementen la interfaz en un Map
    // Key: nombre del bean ("plasSBGateway"), Value: la instancia
    public RecyclingGatewayFactory(Map<String, IRecyclingPlantGateway> gateways) {
        this.gateways = gateways;
    }

    public IRecyclingPlantGateway getGateway(String tipoServicio) {
        if ("REST".equalsIgnoreCase(tipoServicio)) {
            return gateways.get("PlasSBGateway");
        } else if ("SOCKET".equalsIgnoreCase(tipoServicio)) {
            return gateways.get("ContSocketGateway");
        }
        throw new IllegalArgumentException("Tipo de servicio desconocido: " + tipoServicio);
    }
}