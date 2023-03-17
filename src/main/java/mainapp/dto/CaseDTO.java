package mainapp.dto;

import java.sql.Timestamp;

import lombok.Data;
import mainapp.data.Court;
import mainapp.data.Representative;

@Data
public class CaseDTO {
	
	private int id;
	
	private String relation;

	private String caseType;
	
	private String title;

	private Court court;

	private String caseNo;
	
	private String plaintiff;
	
	private String defendant;

	private Representative repr;

	private String stage;

	private Timestamp currentDate;
	
	private String currentState;
	
	private Boolean isArchive;

}
