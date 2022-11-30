package ma.awb.poc.core.dao.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {

	@Id
	@Column(name = "ID")
	private String id;


	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "USER_NAME")
	private String userName;
}
