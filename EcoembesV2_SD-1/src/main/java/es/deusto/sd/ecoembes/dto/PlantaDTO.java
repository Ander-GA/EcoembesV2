package es.deusto.sd.ecoembes.dto;

import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;

public class PlantaDTO {

    private long id;
    private String nombre;
    private String direccion;
    private int codigoPostal;
    private double capacidadMaxima;
    
    // 1. NUEVO CAMPO
    private String tipoServicio; 

    public PlantaDTO() {}

    // Convertir de Entidad a DTO
    public PlantaDTO(PlantaDeReciclaje planta) {
        this.id = planta.getId();
        this.nombre = planta.getNombre();
        this.direccion = planta.getDireccion();
        this.codigoPostal = planta.getCodigoPostal();
        this.capacidadMaxima = planta.getCapacidadMaxima();
        // 2. Mapear el nuevo campo
        this.tipoServicio = planta.getTipoServicio(); 
    }

    // Convertir de DTO a Entidad
    public PlantaDeReciclaje toEntity() {
        // 3. CORRECCIÓN DEL ERROR: Ahora pasamos los 5 argumentos requeridos por el nuevo constructor
        return new PlantaDeReciclaje(
            this.nombre, 
            this.direccion, 
            this.codigoPostal, 
            this.capacidadMaxima, 
            this.tipoServicio // <--- Esto es lo que faltaba
        );
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(int codigoPostal) { this.codigoPostal = codigoPostal; }

    public double getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(double capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    
    // 4. Getters y Setters para el nuevo campo
    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }
}