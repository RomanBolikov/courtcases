package mainapp.data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
@Table(name = "acase")
public class ACase {

	@Column(name = "case_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer id;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "relation", nullable = false)
	private Relation relation;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "case_type", nullable = false)
	private CaseType case_type;
	
	@NonNull
	@Column(name = "case_title", nullable = false)
	private String title;
	
	@NonNull
	@ManyToOne
	@JoinColumn(name = "court", nullable = false)
	private Court court;

	private String case_no;
	
	@NonNull
	@Column(nullable = false)
	private String plaintiff;
	
	@NonNull
	@Column(nullable = false)
	private String defendant;

	@ManyToOne
	@JoinColumn(name = "repr")
	private Representative repr;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "stage", nullable = false)
	private Stage stage;

	private Timestamp curr_date;
	
	@NonNull
	@Column(nullable = false)
	private String curr_state;
	
	@Column(name = "isarchive", nullable = false)	
	private Boolean isArchive = false;

	@Version
	private Integer version;
	
	public String toString() {
		return title;
	}
	
	public boolean isArchive() {
		return isArchive;
	}
}
