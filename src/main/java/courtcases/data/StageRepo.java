package courtcases.data;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StageRepo  extends CrudRepository<Stage, Integer> {
	List<Stage> findAll();
}