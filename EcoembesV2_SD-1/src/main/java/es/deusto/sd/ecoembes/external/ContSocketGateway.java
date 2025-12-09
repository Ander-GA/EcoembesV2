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
                
                // CORRECCIÓN: Usamos el protocolo "ASIGNAR" que espera el servidor
                // Formato: ASIGNAR:id_contenedor;capacidad;fecha
                String mensaje = String.format("ASIGNAR:%d;%.2f;%s",
                        asignacion.getContainer().getId(),
                        asignacion.getContainer().getCapacidad(),
                        asignacion.getFechaAsignacion().toString());
                
                out.writeUTF(mensaje);
                System.out.println("--> [ContSocketGateway] Asignación enviada: " + mensaje);
            }
        } catch (Exception e) {
            System.err.println("Error notificando a ContSocket: " + e.getMessage());
        }
    }
}