package mainapp.data;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface CaseRepo extends CrudRepository<ACase, Integer> {
	List<ACase> findAll();
	
	List<ACase> findByRepr(Representative repr);
	
	List<ACase> findByRelation(Relation relation);
	
	@SuppressWarnings("unchecked")
	@Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
	ACase save (ACase entity);
}
