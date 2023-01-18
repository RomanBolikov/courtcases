package mainapp.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Representative {

	@Column(name = "repr_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Integer id;

	@NonNull
	private String name;

	private String password;
	
	@Column(name = "isadmin")
	private Boolean isAdmin = false;
	
	@Version
	private Integer version;

	public String toString() {
		return name;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
}
