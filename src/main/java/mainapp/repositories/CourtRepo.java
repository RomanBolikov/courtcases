package mainapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mainapp.data.Court;

public interface CourtRepo extends CrudRepository<Court, Integer> {
	List<Court> findAll();
	Optional<Court> findByName(String name);
	boolean existsByName(String name);
}
