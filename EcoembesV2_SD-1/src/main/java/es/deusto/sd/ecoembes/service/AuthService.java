/**
 * This code is based on solutions provided by ChatGPT 4o and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.ecoembes.service;

import org.springframework.stereotype.Service;


import es.deusto.sd.ecoembes.entity.Empleado;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    // Simulating a Empleado repository
    private static Map<String, Empleado> EmpleadoRepository = new HashMap<>();
    
    // Storage to keep the session of the Empleados that are logged in
    private static Map<String, Empleado> tokenStore = new HashMap<>(); 

    // Login method that checks if the Empleado exists in the database and validates the password
    public Optional<String> login(String email, String password) {
        Empleado Empleado = EmpleadoRepository.get(email);
        
        if (Empleado != null && Empleado.checkPassword(password)) {
            String token = generateToken();  // Generate a random token for the session
            tokenStore.put(token, Empleado);     // Store the token and associate it with the Empleado

            return Optional.of(token);
        } else {
        	return Optional.empty();
        }
    }
    
    // Logout method to remove the token from the session store
    public Optional<Boolean> logout(String token) {
        if (tokenStore.containsKey(token)) {
            tokenStore.remove(token);

            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }
    
    // Method to add a new Empleado to the repository
    public void addEmpleado(Empleado Empleado) {
    	if (Empleado != null) {
    		EmpleadoRepository.putIfAbsent(Empleado.getEmail(), Empleado);
    	}
    }
    
    // Method to get the Empleado based on the token
    public Empleado getEmpleadoByToken(String token) {
        return tokenStore.get(token); 
    }
    
    // Method to get the Empleado based on the email
    public Empleado getEmpleadoByEmail(String email) {
		return EmpleadoRepository.get(email);
	}

    // Synchronized method to guarantee unique token generation
    private static synchronized String generateToken() {
        return Long.toHexString(System.currentTimeMillis());
    }
}