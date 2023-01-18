package mainapp.data;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CaseTypeRepo extends CrudRepository<CaseType, Integer> {
	List<CaseType> findAll();
}
