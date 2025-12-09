/*
 * Codigo generado con ayuda de Gemini.
 * */
package es.deusto.sd.ecoembes.external;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Component;

import es.deusto.sd.ecoembes.entity.Asignacion;

@Component("ContSocketGateway")
public class ContSocketGateway implements IRecyclingPlantGateway {

	private final String endpoint = "127.0.0.1:9090";
    @Override
    public Optional<Double> getCapacidadReal(LocalDate fecha) {
        // endpoint sería "IP:PUERTO", ej: "127.0.0.1:9090"
        String[] parts = endpoint.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Protocolo simple: Enviamos fecha -> Recibimos double
            out.writeUTF(fecha.toString());
            double capacidad = in.readDouble();
            return Optional.of(capacidad);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    

    @Override
    public void notificarAsignacion(Asignacion asignacion) {
        try {
            String[] parts = endpoint.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            try (Socket socket = new Socket(host, port);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                
                // Enviamos el mensaje con el prefijo especial
                String mensaje = "NOTIFY:Contenedor " + asignacion.getContainer().getId() + " asignado.";
                out.writeUTF(mensaje);
                // No esperamos respuesta (fire and forget)
                System.out.println("--> [ContSocketGateway] Notificación enviada: " + mensaje);
            }
        } catch (Exception e) {
            System.err.println("Error notificando a ContSocket: " + e.getMessage());
        }
    }
}