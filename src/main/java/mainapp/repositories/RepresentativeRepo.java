package mainapp.repositories;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import mainapp.data.Representative;

public interface RepresentativeRepo extends CrudRepository<Representative, Integer> {

	List<Representative> findAll();

	Representative findByName(String name);
	
	@SuppressWarnings("unchecked")
	@Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
	Representative save(Representative entity);
	
	@Lock(value = LockModeType.OPTIMISTIC)
	void delete(Representative entity);
}
