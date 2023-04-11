package mainapp.repositories;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import mainapp.data.ACase;
import mainapp.data.Representative;

public interface CaseRepo extends CrudRepository<ACase, Integer> {
	
	List<ACase> findAll();
	
	List<ACase> findByRepr(Representative repr);
	
	List<ACase> findByCaseNo(String caseNo);
	
	List<ACase> findByRelation(String relation);
	
	@SuppressWarnings("unchecked")
	@Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
	ACase save (ACase entity);
	
	@Lock(value = LockModeType.OPTIMISTIC)
	void delete(ACase entity);
	
}
