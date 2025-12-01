package es.deusto.sd.ecoembes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.deusto.sd.ecoembes.entity.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long>{

}
