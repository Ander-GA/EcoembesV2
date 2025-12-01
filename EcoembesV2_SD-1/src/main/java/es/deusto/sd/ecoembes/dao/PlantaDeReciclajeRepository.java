package es.deusto.sd.ecoembes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.deusto.sd.ecoembes.entity.PlantaDeReciclaje;

@Repository
public interface PlantaDeReciclajeRepository extends JpaRepository<PlantaDeReciclaje, Long>{

}
