package es.deusto.sd.ecoembes.dao;

import org.springframework.stereotype.Repository;
import es.deusto.sd.ecoembes.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RepositorioAsignaciones extends JpaRepository<Asignacion, Long> {
}
