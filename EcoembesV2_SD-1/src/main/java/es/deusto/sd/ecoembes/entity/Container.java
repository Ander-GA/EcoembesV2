package es.deusto.sd.ecoembes.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "container")
public class Container {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private int codigoPostal;

    @Column(nullable = false)
    private double capacidad;
    
    // Constructor vacío (obligatorio para JPA)
    public Container() {}
    
    // Constructor sin ID
    public Container(String direccion, int codigoPostal, double capacidad) {
        this.direccion = direccion;
        this.codigoPostal = codigoPostal;
        this.capacidad = capacidad;
    }

    // Metodo de negocio (se mantiene igual)
    public Color calcularColor(double nivelDeLlenado) {
        if (this.capacidad == 0) return Color.VERDE;
        double porcentaje = (nivelDeLlenado / this.capacidad) * 100.0;
        if (porcentaje > 90) return Color.ROJO;
        else if (porcentaje >= 50) return Color.AMARILLO;
        else return Color.VERDE;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(int codigoPostal) { this.codigoPostal = codigoPostal; }
    public double getCapacidad() { return capacidad; }
    public void setCapacidad(double capacidad) { this.capacidad = capacidad; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Container other = (Container) obj;
        return Objects.equals(id, other.id);
    }
}