package mainapp.data;

import java.time.LocalDate;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component
public class CaseFilter implements Predicate<ACase> {

	private boolean acceptByType;
	private boolean acceptByRelation;
	private boolean acceptByRepr;
	private boolean acceptByCourt;
	private boolean acceptByCurrentDate;
	private boolean acceptByPlaintiff;
	private boolean acceptByDefendant;

	private Relation relation = null;
	private CaseType type = null;
	private Representative repr = null;
	private Court court = null;
	private LocalDate currentDate = null;
	private String plaintiff = null;
	private String defendant = null;

	public Relation getRelation() {
		return relation;
	}

	public CaseType getType() {
		return type;
	}

	public Representative getRepr() {
		return repr;
	}

	public Court getCourt() {
		return court;
	}

	public LocalDate getCurrentDate() {
		return currentDate;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public void setType(CaseType type) {
		this.type = type;
	}

	public void setRepr(Representative repr) {
		this.repr = repr;
	}

	public void setCourt(Court court) {
		this.court = court;
	}

	public void setCurrentDate(LocalDate currentDate) {
		this.currentDate = currentDate;
	}

	public void setPlaintiff(String plaintiff) {
		this.plaintiff = plaintiff;
	}

	public void setDefendant(String defendant) {
		this.defendant = defendant;
	}

	@Override
	public boolean test(ACase acase) {
		acceptByType = type == null ? true : acase.getCaseType().toString().equals(type.toString());
		acceptByRelation = relation == null ? true : acase.getRelation().toString().equals(relation.toString());
		acceptByRepr = repr == null ? true : acase.getRepr().toString().equals(repr.toString());
		acceptByCourt = court == null ? true : acase.getCourt().toString().equals(court.toString());
		acceptByCurrentDate = currentDate == null ? true
				: DatePickerConverter.extractLocalDate(acase.getCurrentDate()).equals(currentDate);
		acceptByPlaintiff = plaintiff == null ? true
				: acase.getPlaintiff().toLowerCase().contains(plaintiff.toLowerCase());
		acceptByDefendant = defendant == null ? true
				: acase.getDefendant().toLowerCase().contains(defendant.toLowerCase());

		return acceptByType && acceptByRelation && acceptByRepr && acceptByCourt && acceptByCurrentDate
				&& acceptByPlaintiff && acceptByDefendant;
	}

}
