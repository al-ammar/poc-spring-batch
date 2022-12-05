package ma.awb.poc.core.model;

import com.googlecode.jmapper.annotations.JMap;

public class EventBatchDTO {

	@JMap
	private String id;

	@JMap
	private String key;

	@JMap
	private String value;

	@JMap
	private String type;

	@JMap
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
