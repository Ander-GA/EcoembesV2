package es.deusto.sd.ecoembes; 

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.deusto.sd.ecoembes.dao.EmpleadoRepository;
import es.deusto.sd.ecoembes.entity.Container;
import es.deusto.sd.ecoembes.entity.Empleado;
import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;
import es.deusto.sd.ecoembes.service.ContainerService;
import es.deusto.sd.ecoembes.service.NivelLlenadoService;
import es.deusto.sd.ecoembes.service.PlantaDeReciclajeService;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    CommandLineRunner initData(
            EmpleadoRepository empleadoRepository,
            ContainerService containerService,
            PlantaDeReciclajeService plantaService,
            NivelLlenadoService nivelLlenadoService) {
                
        return args -> {
            
            // 1. Crear Empleados
            Empleado ander = new Empleado("Ander", "Gonzalez Alonso", "pass1", "ander.gonzalez.a@opendeusto.es");
            Empleado iñigo = new Empleado("Iñigo", "Melchisidor Urquijo", "pass2", "i.melchisidor@opendeusto.com");
            Empleado emilio = new Empleado("Emilio", "Gil del Rio Perez", "pass3", "emilio.gildelrio@opendeusto.es");
            Empleado gaizka = new Empleado("Gaizka", "Gredilla Yarritu", "pass4", "gaizka.gredilla@opendeusto.es");
            Empleado admin = new Empleado("Admin", "Sistema Ecoembes", "1", "1");
            
            empleadoRepository.saveAll(List.of(ander,iñigo,emilio,gaizka,admin));            
            logger.info("Empleados de Ecoembes guardados!");
            
            // 2. Crear Plantas de Reciclaje 
            // Usamos URLs ficticias o localhost para probar la conexión
            PlantaDeReciclaje plas = new PlantaDeReciclaje("PlasSB Ltd", "http://localhost:8081", 48007, 5000.0, "REST");
            PlantaDeReciclaje cont = new PlantaDeReciclaje("ContSocket Ltd", "127.0.0.1:9090", 48009, 3000.0, "SOCKET");
            
            plantaService.createPlanta(plas); 
            plantaService.createPlanta(cont);
            
            logger.info("Plantas de Reciclaje guardadas!");

            // 3. Crear Contenedores
            Container c1_Euskadi = new Container("Plaza Euskadi", 48007, 100.0);
            Container c2_GranVia = new Container("Gran Vía 50", 48007, 100.0);
            Container c3_Parque = new Container("Parque Etxebarria", 48009, 120.0);
            
            // Los servicios (ahora usando repositorios JPA) guardarán y asignarán IDs
            containerService.createContainer(c1_Euskadi);
            containerService.createContainer(c2_GranVia);
            containerService.createContainer(c3_Parque);
            
            logger.info("Contenedores guardados!");

            // 4. Crear Historial de Niveles
            LocalDate hoy = LocalDate.now();
            
            // --- Historial para Contenedores ---
            // IMPORTANTE: Al ser Entidades JPA, debemos asegurarnos de que c1, c2, etc. tienen IDs asignados (lo hace el createContainer anterior)
            
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy.minusDays(5), 10.0, c1_Euskadi.getId(), TipoID.CONTAINER));
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy.minusDays(3), 15.0, c1_Euskadi.getId(), TipoID.CONTAINER));
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy, 20.0, c1_Euskadi.getId(), TipoID.CONTAINER)); // Nivel actual

            nivelLlenadoService.addRegistro(new NivelLlenado(hoy.minusDays(2), 50.0, c2_GranVia.getId(), TipoID.CONTAINER));
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy, 95.0, c2_GranVia.getId(), TipoID.CONTAINER)); // Nivel actual > 90%

            nivelLlenadoService.addRegistro(new NivelLlenado(hoy, 60.0, c3_Parque.getId(), TipoID.CONTAINER)); // Nivel actual > 50%

            // --- Historial para Plantas ---
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy, 1500.0, plas.getId(), TipoID.PLANTA_DE_RECICLAJE));
            nivelLlenadoService.addRegistro(new NivelLlenado(hoy, 500.0, cont.getId(), TipoID.PLANTA_DE_RECICLAJE));
            
            logger.info("Historial de niveles guardado!");
        };
    }
}