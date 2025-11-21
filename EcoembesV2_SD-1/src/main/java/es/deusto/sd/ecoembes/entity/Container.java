	package es.deusto.sd.ecoembes.entity;

import java.util.Objects;

public class Container {
	private Long id;
	private String direccion;
	private int codigoPostal;
	private double capacidad;
	
	public Container() {}
	
	public Container(String direccion, int codigoPostal, double capacidad) {
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.capacidad = capacidad;
	}
	
	public Container(Long id, String direccion, int codigoPostal, double capacidad, boolean estado) {
		this.id = id;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.capacidad = capacidad;
	}
	//Metodo para calcular el color del contenedor segun su capacidad
	public Color calcularColor(double nivelDeLlenado) {
        if (this.capacidad == 0) {
            return Color.VERDE; // Evitar división por cero
        }
        
        double porcentaje = (nivelDeLlenado / this.capacidad) * 100.0;

        if (porcentaje > 90) {
            return Color.ROJO;
        } else if (porcentaje >= 50) {
            return Color.AMARILLO;
        } else {
            return Color.VERDE;
        }
    }
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public double getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(double capacidad) {
		this.capacidad = capacidad;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacidad, codigoPostal, direccion, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
		return Double.doubleToLongBits(capacidad) == Double.doubleToLongBits(other.capacidad)
				&& codigoPostal == other.codigoPostal && Objects.equals(direccion, other.direccion)
				&& Objects.equals(id, other.id);
	}
		
}
