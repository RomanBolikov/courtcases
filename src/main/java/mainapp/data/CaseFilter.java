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

	private String relation = null;
	private String type = null;
	private String repr = null;
	private String court = null;
	private LocalDate currentDate = null;
	private String plaintiff = null;
	private String defendant = null;

	public String getRelation() {
		return relation;
	}

	public String getType() {
		return type;
	}

	public String getRepr() {
		return repr;
	}

	public String getCourt() {
		return court;
	}

	public LocalDate getCurrentDate() {
		return currentDate;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRepr(String repr) {
		this.repr = repr;
	}

	public void setCourt(String court) {
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
		acceptByType = type == null ? true : acase.getCaseType().toString().equals(type);
		acceptByRelation = relation == null ? true : acase.getRelation().toString().equals(relation);
		acceptByRepr = repr == null ? true : acase.getRepr() == null ? false : acase.getRepr().toString().equals(repr);
		acceptByCourt = court == null ? true : acase.getCourt().toString().equals(court);
		acceptByCurrentDate = currentDate == null ? true : acase.getCurrentDate() == null ? false
				: DatePickerConverter.extractLocalDate(acase.getCurrentDate()).equals(currentDate);
		acceptByPlaintiff = plaintiff == null ? true
				: acase.getPlaintiff().toLowerCase().contains(plaintiff.toLowerCase());
		acceptByDefendant = defendant == null ? true
				: acase.getDefendant().toLowerCase().contains(defendant.toLowerCase());

		return acceptByType && acceptByRelation && acceptByRepr && acceptByCourt && acceptByCurrentDate
				&& acceptByPlaintiff && acceptByDefendant;
	}

}
