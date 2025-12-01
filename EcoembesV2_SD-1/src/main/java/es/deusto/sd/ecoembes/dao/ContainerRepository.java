package es.deusto.sd.ecoembes.dao;
import es.deusto.sd.ecoembes.entity.Container;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
	List<Container> findByCodigoPostal(int codigoPostal);
}
