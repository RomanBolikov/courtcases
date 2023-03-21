package mainapp.services.impl;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainapp.data.Court;
import mainapp.helpers.SaveEntityException;
import mainapp.repositories.CourtRepo;
import mainapp.services.CourtService;

@Service
public class CourtServiceImpl implements CourtService {

	private final CourtRepo courtRepo;

	public CourtServiceImpl(CourtRepo courtRepo) {
		this.courtRepo = courtRepo;
	}

	@Override
	public ObservableList<Court> getAllCourts() {
		return FXCollections.observableArrayList(courtRepo.findAll());
	}

	@Override
	public boolean existsInDB(Court court) {
		return courtRepo.existsByName(court.getName());
	}
	
	@Override
	public Court findCourtByName(String courtName) {
		return courtRepo.findByName(courtName).orElse(null);
	}
	
	@Override
	public Court findCourtByEntity(Court court) {
		return findCourtByName(court.getName());
	}

	@Override
	public Court addCourt(Court newCourt) throws SaveEntityException {
		try {
			return courtRepo.save(newCourt);
		} catch (OptimisticLockException ole) {
			throw new SaveEntityException("An OptimisticLockException has occurred");
		}
	}



	
}
