package courtcases.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
@Table(name = "court")
public class Court {
	@Column(name = "court_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer id;

	@NonNull
	@Column(name = "court_name", unique = true)
	private String name;

	public String toString() {
		return name;
	}
}
