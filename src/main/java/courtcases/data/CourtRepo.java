package courtcases.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface CourtRepo extends CrudRepository<Court, Integer> {
	List<Court> findAll();
	Optional<Court> findByName(String name);
}
