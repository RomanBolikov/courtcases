package mainapp.helpers;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mainapp.repositories.CaseRepo;
import mainapp.repositories.CaseTypeRepo;
import mainapp.repositories.CourtRepo;
import mainapp.repositories.RelationRepo;
import mainapp.repositories.RepresentativeRepo;
import mainapp.repositories.StageRepo;

@Component
@AllArgsConstructor
@Getter
public class DataModel {
	
	private final CaseRepo caseRepo;
	private final CaseTypeRepo caseTypeRepo;
	private final CourtRepo courtRepo;
	private final RelationRepo relationRepo;
	private final RepresentativeRepo reprRepo;
	private final StageRepo stageRepo;
	
}
