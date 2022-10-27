package courtcases.data;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table("representative")
public class Representative {
	@Column("repr_id")
	private int id;
	
	private String name;
}
