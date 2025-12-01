package es.deusto.sd.ecoembes.entity;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "planta_de_reciclaje")
public class PlantaDeReciclaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;
    private String direccion;
    private int codigoPostal;
    private double capacidadMaxima;
    
    // Nuevo campo para saber a qué servicio llamar (PlasSB o ContSocket)
    // Esto es vital para el patrón Factory
    private String tipoServicio; // ej: "REST", "SOCKET"

    public PlantaDeReciclaje() {}
    
    public PlantaDeReciclaje(String nombre, String direccion, int codigoPostal, double capacidadMaxima, String tipoServicio) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.capacidadMaxima = capacidadMaxima;
        this.tipoServicio = tipoServicio;
    }

    // Getters, Setters, HashCode, Equals y calcularColor...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(int codigoPostal) { this.codigoPostal = codigoPostal; }
    public double getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(double capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
    
    // Metodo calcularColor se mantiene igual...
}