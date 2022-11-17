package courtcases.data;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CaseRepo extends CrudRepository<ACase, Integer> {
	List<ACase> findAll();
}
