package courtcases.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Representative {

	@Column(name = "repr_id")
	@Id
	private Integer id;

	private String name;

	private String password;
	
	@Column(name = "isadmin")
	private Boolean isAdmin;

	public String toString() {
		return name;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
}