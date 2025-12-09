/*
 * Codigo generado con ayuda de Gemini.
 * */
package es.deusto.sd.ecoembes.external;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.deusto.sd.ecoembes.entity.Asignacion;

import org.springframework.stereotype.Component;

@Component("PlasSBGateway") // Nombre para identificarlo en la factoría
public class PlasSBGateway implements IRecyclingPlantGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String endpoint = "http://localhost:8081";

    public PlasSBGateway() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<Double> getCapacidadReal(LocalDate fecha) {
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
    
    
    @Override
    public void notificarAsignacion(Asignacion asignacion) {
        // URL del endpoint de asignaciones
        String url = endpoint + "/api/asignaciones"; // Cambiamos /notificaciones por /asignaciones para ser más REST

        try {
            // Construimos un JSON con los datos útiles para la planta
            String jsonBody = String.format(
                "{\"containerId\": %d, \"nivelLlenado\": %.2f, \"fecha\": \"%s\"}",
                asignacion.getContainer().getId(),
                asignacion.getContainer().getCapacidad(), // O el nivel real si lo tuvieras
                asignacion.getFechaAsignacion().toString()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("--> [PlasSBGateway] Asignación enviada a PlasSB.");

        } catch (Exception e) {
            System.err.println("Error enviando asignación a PlasSB: " + e.getMessage());
        }
    }
}