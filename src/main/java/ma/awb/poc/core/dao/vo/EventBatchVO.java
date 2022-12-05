package ma.awb.poc.core.dao.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "EVENT_BATCH")
@Entity
public class EventBatchVO {

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "KEY")
	private String key;

	@Column(name = "VALUE")
	private String value;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "TREATED")
	private Boolean treated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getTreated() {
		return treated;
	}

	public void setTreated(Boolean treated) {
		this.treated = treated;
	}

}
