package es.deusto.sd.ecoembes.dao;
import es.deusto.sd.ecoembes.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
}
