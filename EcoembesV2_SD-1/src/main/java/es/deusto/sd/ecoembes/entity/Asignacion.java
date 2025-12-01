package es.deusto.sd.ecoembes.entity;

import java.time.LocalDate;
import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "asignacion")
public class Asignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fechaAsignacion;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "planta_id", nullable = false)
    private PlantaDeReciclaje plantaDeReciclaje;

    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;
    
    public Asignacion() {}

    public Asignacion(LocalDate fechaAsignacion, Empleado empleado, PlantaDeReciclaje plantaDeReciclaje, Container container) {
        this.fechaAsignacion = fechaAsignacion;
        this.empleado = empleado;
        this.plantaDeReciclaje = plantaDeReciclaje;
        this.container = container;
    }

    // Getters, Setters...
    public Long getId() { return id; } // Getter de ID necesario
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    public PlantaDeReciclaje getPlantaDeReciclaje() { return plantaDeReciclaje; }
    public void setPlantaDeReciclaje(PlantaDeReciclaje plantaDeReciclaje) { this.plantaDeReciclaje = plantaDeReciclaje; }
    public Container getContainer() { return container; }
    public void setContainer(Container container) { this.container = container; }
}