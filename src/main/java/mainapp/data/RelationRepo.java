package mainapp.data;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepo extends CrudRepository<Relation, Integer> {
	List<Relation> findAll();
	Relation findByRelation(String relation);
}
