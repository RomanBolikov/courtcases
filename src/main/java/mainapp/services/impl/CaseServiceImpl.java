package mainapp.services.impl;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.SaveCaseException;
import mainapp.repositories.CaseRepo;
import mainapp.services.CaseService;

@Service
public class CaseServiceImpl implements CaseService {

	private final CaseRepo caseRepo;
	
	public CaseServiceImpl(CaseRepo caseRepo) {
		this.caseRepo = caseRepo;
	}

	@Override
	public ObservableList<ACase> getAllCases() {
		return FXCollections.observableArrayList(caseRepo.findAll());
	}

	@Override
	public ObservableList<ACase> getCasesByRepr(Representative repr) {
		return FXCollections.observableArrayList(caseRepo.findByRepr(repr));
	}

	@Override
	public ObservableList<ACase> getCasesByRelation(String relation) {
		return FXCollections.observableArrayList(caseRepo.findByRelation(relation));
	}

	@Override
	public ACase getCaseById(int id) {
		return caseRepo.findById(id).orElseThrow();
	}
	
	@Override
	public ACase addCase(ACase acase) throws SaveCaseException {
		try {
			return caseRepo.save(acase);
		} catch (OptimisticLockException ole) {
			throw new SaveCaseException("An OptimisticLockException has occurred");
		}
	}

	@Override
	public ACase updateCase(int id, ACase updatedCase) throws SaveCaseException {
		ACase acase = caseRepo.findById(id).orElseThrow(() -> new SaveCaseException("Case not found in database"));
		acase.setRelation(updatedCase.getRelation());
		acase.setCaseType(updatedCase.getCaseType());
		acase.setTitle(updatedCase.getTitle());
		acase.setCourt(updatedCase.getCourt());
		acase.setCaseNo(updatedCase.getCaseNo());
		acase.setPlaintiff(updatedCase.getPlaintiff());
		acase.setDefendant(updatedCase.getDefendant());
		acase.setRepr(updatedCase.getRepr());
		acase.setStage(updatedCase.getStage());
		acase.setCurrentDate(updatedCase.getCurrentDate());
		acase.setCurrentState(updatedCase.getCurrentState());
		try {
			return caseRepo.save(acase);
		} catch (OptimisticLockException ole) {
			throw new SaveCaseException("An OptimisticLockException has occurred");
		}
	}
}
