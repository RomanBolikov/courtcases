package mainapp.data;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
