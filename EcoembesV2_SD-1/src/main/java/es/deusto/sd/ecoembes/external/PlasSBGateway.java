package es.deusto.sd.ecoembes.external;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component("PlasSBGateway") // Nombre para identificarlo en la factoría
public class PlasSBGateway implements IRecyclingPlantGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PlasSBGateway() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<Double> getCapacidadReal(String endpoint, LocalDate fecha) {
        // endpoint sería la URL base, ej: "http://localhost:8082"
        String url = endpoint + "/api/capacidades?fecha=" + fecha.toString();
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Suponiendo que devuelve un JSON {"capacidad": 5000.0}
                Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);
                Object capacidad = json.get("capacidad");
                return Optional.of(Double.parseDouble(capacidad.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}