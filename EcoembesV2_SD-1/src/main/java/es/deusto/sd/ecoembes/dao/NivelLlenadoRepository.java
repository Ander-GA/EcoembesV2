package es.deusto.sd.ecoembes.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.deusto.sd.ecoembes.entity.NivelLlenado;
import es.deusto.sd.ecoembes.entity.NivelLlenado.TipoID;

public interface NivelLlenadoRepository extends JpaRepository<NivelLlenado, Long>{
	// "Busca por IdObjetoAsociado Y por TipoId, Ordenando por FechaRegistro Ascendente"
    List<NivelLlenado> findByIdObjetoAsociadoAndTipoIdOrderByFechaRegistroAsc(long idObjetoAsociado, TipoID tipoId);
    List<NivelLlenado> findByIdObjetoAsociadoAndTipoIdAndFechaRegistroBetweenOrderByFechaRegistroAsc(
            long idObjetoAsociado, 
            TipoID tipoId, 
            LocalDate fechaInicio, 
            LocalDate fechaFin
        );
    NivelLlenado findTop1ByIdObjetoAsociadoAndTipoIdAndFechaRegistroLessThanEqualOrderByFechaRegistroDesc(
    	    long idObjetoAsociado, 
    	    TipoID tipoId, 
    	    LocalDate fechaLimite
    	);
}
