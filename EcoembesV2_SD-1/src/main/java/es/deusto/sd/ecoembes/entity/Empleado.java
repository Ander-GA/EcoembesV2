package es.deusto.sd.ecoembes.entity;

import java.util.Objects;

public class Empleado {
	private String nombre;
	private String apellidos;
	private String password;
	private String email;
	
	public Empleado() {}
	
	public Empleado(String nombre, String apellidos, String password, String email) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.password = password;
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public int hashCode() {
		return Objects.hash(apellidos, email, nombre, password);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Empleado other = (Empleado) obj;
		return Objects.equals(apellidos, other.apellidos) && Objects.equals(email, other.email)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(password, other.password);
	}
	
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
}