package es.deusto.sd.ecoembes.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "nivel_llenado")
public class NivelLlenado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaRegistro;
    private double nivelDeLlenado;
    private long idObjetoAsociado;

    @Enumerated(EnumType.STRING)
    private TipoID tipoId;

    public enum TipoID {
        CONTAINER,
        PLANTA_DE_RECICLAJE
    }

    // Constructor vacío obligatorio para JPA
    public NivelLlenado() {}
    
    // Constructor sin ID (la BBDD lo genera)
    public NivelLlenado(LocalDate fechaRegistro, double nivelDeLlenado, long idObjetoAsociado, TipoID tipoId) {
        this.fechaRegistro = fechaRegistro;
        this.nivelDeLlenado = nivelDeLlenado;
        this.idObjetoAsociado = idObjetoAsociado;
        this.tipoId = tipoId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public double getNivelDeLlenado() {
        return nivelDeLlenado;
    }

    public void setNivelDeLlenado(double nivelDeLlenado) {
        this.nivelDeLlenado = nivelDeLlenado;
    }

    public long getIdObjetoAsociado() {
        return idObjetoAsociado;
    }

    public void setIdObjetoAsociado(long idObjetoAsociado) {
        this.idObjetoAsociado = idObjetoAsociado;
    }

    public TipoID getTipoId() {
        return tipoId;
    }

    public void setTipoId(TipoID tipoId) {
        this.tipoId = tipoId;
    }
}