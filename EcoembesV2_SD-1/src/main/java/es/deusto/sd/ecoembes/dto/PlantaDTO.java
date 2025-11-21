package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;

/**
 * DTO para crear y devolver información de Plantas de Reciclaje.
 */
public class PlantaDTO {

    private long id;
    private String nombre;
    private String direccion;
    private int codigoPostal;
    private double capacidadMaxima; // Coincide con tu entidad

    public PlantaDTO() {}

    // Convertir de Entidad a DTO
    public PlantaDTO(PlantaDeReciclaje planta) {
        this.id = planta.getId();
        this.nombre = planta.getNombre();
        this.direccion = planta.getDireccion();
        this.codigoPostal = planta.getCodigoPostal();
        this.capacidadMaxima = planta.getCapacidadMaxima();
    }

    // Convertir de DTO a Entidad
    public PlantaDeReciclaje toEntity() {
        return new PlantaDeReciclaje(this.nombre, this.direccion, this.codigoPostal, this.capacidadMaxima);
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(double capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
}