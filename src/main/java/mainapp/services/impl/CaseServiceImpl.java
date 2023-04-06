package mainapp.services.impl;

import java.util.List;

import javax.persistence.OptimisticLockException;

import org.springframework.stereotype.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mainapp.data.ACase;
import mainapp.data.Representative;
import mainapp.helpers.SaveEntityException;
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
	public List<ACase> getCasesByRepr(Representative repr) {
		return caseRepo.findByRepr(repr);
	}

	@Override
	public List<ACase> getCasesByCaseNo(String caseNo) {
		return caseRepo.findByCaseNo(caseNo);
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
	public ACase saveCase(ACase acase) throws SaveEntityException {
		try {
			return caseRepo.save(acase);
		} catch (OptimisticLockException ole) {
			throw new SaveEntityException("An OptimisticLockException has occurred");
		}
	}

	
}
