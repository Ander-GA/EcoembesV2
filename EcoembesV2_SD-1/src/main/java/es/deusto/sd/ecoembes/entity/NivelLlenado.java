package es.deusto.sd.ecoembes.entity;

import java.time.LocalDate;
import java.util.Objects;

public class NivelLlenado {
	private LocalDate fechaRegistro;
	private double nivelDeLlenado;
	private long idObjetoAsociado;
	//Especificamos para que tipo de entidad es el nivel de llenado
	public enum TipoID {
		CONTAINER,
		PLANTA_DE_RECICLAJE
	}
	private TipoID tipoId;
	public NivelLlenado() {	}
	
	public NivelLlenado(LocalDate fechaRegistro, double nivelDeLlenado,long idObjetoAsociado ,TipoID tipoId) {
		super();
		this.fechaRegistro = fechaRegistro;
		this.nivelDeLlenado = nivelDeLlenado;
		this.idObjetoAsociado = idObjetoAsociado;
		this.tipoId = tipoId;
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
