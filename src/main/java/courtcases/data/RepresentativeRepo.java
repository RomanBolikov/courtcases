package courtcases.data;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RepresentativeRepo extends CrudRepository<Representative, Integer> {

	List<Representative> findAll();

	Representative findByName(String name);
}
