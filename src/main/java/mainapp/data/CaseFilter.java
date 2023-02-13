package mainapp.data;

import java.sql.Timestamp;
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
	private boolean acceptByArchive = false;

	private Relation relation = null;
	private CaseType type = null;
	private Representative repr = null;
	private Court court = null;
	private Timestamp currentDate = null;
	private String plaintiff = null;
	private String defendant = null;

	public void setAcceptByArchive(boolean acceptByArchive) {
		this.acceptByArchive = acceptByArchive;
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

	public void setCurrentDate(Timestamp currentDate) {
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
		acceptByType = type == null ? true : acase.getCase_type() == type;
		acceptByRelation = relation == null ? true : acase.getRelation() == relation;
		acceptByRepr = repr == null ? true : acase.getRepr() == repr;
		acceptByCourt = court == null ? true : acase.getCourt() == court;
		acceptByCurrentDate = currentDate == null ? true : acase.getCurr_date() == currentDate;
		acceptByPlaintiff = plaintiff == null ? true
				: acase.getPlaintiff().toLowerCase().contains(plaintiff.toLowerCase());
		acceptByDefendant = defendant == null ? true
				: acase.getDefendant().toLowerCase().contains(defendant.toLowerCase());

		return acceptByType && acceptByRelation && acceptByRepr && acceptByCourt && acceptByCurrentDate
				&& acceptByPlaintiff && acceptByDefendant && acceptByArchive;
	}

}
