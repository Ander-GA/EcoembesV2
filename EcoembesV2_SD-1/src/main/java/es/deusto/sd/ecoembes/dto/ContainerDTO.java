package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.Container;

/**
 * DTO para crear y devolver información de Contenedores.
 */
public class ContainerDTO {

    private String direccion;
    private int codigoPostal;
    private double capacidad;
    // Opcional: podemos añadir el color actual aquí
    // private String color; 

    public ContainerDTO() {}

    // Convertir de Entidad a DTO
    public ContainerDTO(Container container) {
        this.direccion = container.getDireccion();
        this.codigoPostal = container.getCodigoPostal();
        this.capacidad = container.getCapacidad();
    }

    // Convertir de DTO a Entidad (para crear uno nuevo)
    public Container toEntity() {
        return new Container(this.direccion, this.codigoPostal, this.capacidad);
    }

    // Getters y Setters

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

    public double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(double capacidad) {
        this.capacidad = capacidad;
    }
}