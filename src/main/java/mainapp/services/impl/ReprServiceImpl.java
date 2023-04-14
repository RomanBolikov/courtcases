package mainapp.services.impl;


import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainapp.data.Representative;
import mainapp.helpers.SaveEntityException;
import mainapp.repositories.RepresentativeRepo;
import mainapp.services.ReprService;

@Service
public class ReprServiceImpl implements ReprService {

	private final RepresentativeRepo reprRepo;

	public ReprServiceImpl(RepresentativeRepo reprRepo) {
		this.reprRepo = reprRepo;
	}

	@Override
	public ObservableList<Representative> getAllReprs() {
		return FXCollections.observableArrayList(reprRepo.findAll());
	}
	
	@Override
	public Representative getRepr(Representative repr) {
		return reprRepo.findByName(repr.getName());
	}

	@Override
	public Representative addNewRepr(Representative newRepr) throws SaveEntityException {
		try {
			return reprRepo.save(newRepr);
		} catch (ObjectOptimisticLockingFailureException ole) {
			throw new SaveEntityException("An OptimisticLockException has occurred");
		}
	}

	@Override
	public Representative updateRepr(int id, Representative updatedRepr) throws SaveEntityException {
		Representative repr = reprRepo.findById(id)
				.orElseThrow(() -> new SaveEntityException("Representative not found in database"));
		repr.setName(updatedRepr.getName());
		repr.setPassword(updatedRepr.getPassword());
		repr.setIsAdmin(updatedRepr.getIsAdmin());
		try {
			return reprRepo.save(repr);
		} catch (ObjectOptimisticLockingFailureException ole) {
			throw new SaveEntityException("An OptimisticLockException has occurred");
		}
	}

	@Override
	public void deleteRepr(int id) throws SaveEntityException {
		try {
			Representative repr = reprRepo.findById(id)
					.orElseThrow(() -> new SaveEntityException("Representative not found in database"));
			reprRepo.delete(repr);
		} catch (ObjectOptimisticLockingFailureException ole) {
			throw new SaveEntityException("An OptimisticLockException has occurred");
		}
	}
}
