package es.deusto.sd.ecoembes.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Asignacion {
	private long id;
	private LocalDate fechaAsignacion;
	private Empleado empleado;
	private PlantaDeReciclaje plantaDeReciclaje;
	private Container container;
	
	public Asignacion() {	}
	
	public Asignacion(Long id, LocalDate fechaAsignacion, Empleado empleado, PlantaDeReciclaje plantaDeReciclaje,
			Container container) {
		this.id = id;
		this.fechaAsignacion = fechaAsignacion;
		this.empleado = empleado;
		this.plantaDeReciclaje = plantaDeReciclaje;
		this.container = container;
	}
	
	public Asignacion(LocalDate fechaAsignacion, Empleado empleado, PlantaDeReciclaje plantaDeReciclaje,
			Container container) {
		this.fechaAsignacion = fechaAsignacion;
		this.empleado = empleado;
		this.plantaDeReciclaje = plantaDeReciclaje;
		this.container = container;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public LocalDate getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(LocalDate fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public PlantaDeReciclaje getPlantaDeReciclaje() {
		return plantaDeReciclaje;
	}

	public void setPlantaDeReciclaje(PlantaDeReciclaje plantaDeReciclaje) {
		this.plantaDeReciclaje = plantaDeReciclaje;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public int hashCode() {
		return Objects.hash(container, empleado, fechaAsignacion, plantaDeReciclaje);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Asignacion other = (Asignacion) obj;
		return Objects.equals(container, other.container) && Objects.equals(empleado, other.empleado)
				&& Objects.equals(fechaAsignacion, other.fechaAsignacion)
				&& Objects.equals(plantaDeReciclaje, other.plantaDeReciclaje);
	}	
}
