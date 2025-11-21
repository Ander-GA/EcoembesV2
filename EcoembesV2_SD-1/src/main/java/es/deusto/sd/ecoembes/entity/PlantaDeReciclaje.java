package es.deusto.sd.ecoembes.entity;

import java.util.Objects;

public class PlantaDeReciclaje {
	private long id;
	private String nombre;
	private String direccion;
	private int codigoPostal;
	private double capacidadMaxima;
	
	public PlantaDeReciclaje() {}
	
	public PlantaDeReciclaje(String nombre, String direccion, int codigoPostal, double capacidadMaxima) {
		this.nombre = nombre;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.capacidadMaxima = capacidadMaxima;
	}
	
	public PlantaDeReciclaje(long id, String nombre, String direccion, int codigoPostal, double capacidadMaxima) {
		this.id = id;
		this.nombre = nombre;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.capacidadMaxima = capacidadMaxima;
	}
	//Metodo para calcular el color de la planta segun su capacidad
	public Color calcularColor(double nivelDeLlenado) {
        // Usamos this.capacidadMaxima (de esta clase)
        if (this.capacidadMaxima == 0) {
            return Color.VERDE; // Evitar división por cero
        }
        
        double porcentaje = (nivelDeLlenado / this.capacidadMaxima) * 100.0;

        if (porcentaje > 90) {
            return Color.ROJO;
        } else if (porcentaje >= 50) {
            return Color.AMARILLO;
        } else {
            return Color.VERDE;
        }
    }
	
	
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

	@Override
	public int hashCode() {
		return Objects.hash(capacidadMaxima, codigoPostal, direccion, id, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlantaDeReciclaje other = (PlantaDeReciclaje) obj;
		return Double.doubleToLongBits(capacidadMaxima) == Double.doubleToLongBits(other.capacidadMaxima)
				&& codigoPostal == other.codigoPostal && Objects.equals(direccion, other.direccion) && id == other.id
				&& Objects.equals(nombre, other.nombre);
	}
	
	
	
	
}
