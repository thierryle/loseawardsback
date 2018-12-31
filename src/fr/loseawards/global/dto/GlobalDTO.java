/**
 * 
 */
package fr.loseawards.global.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant une variable globale à l'application.
 */
public class GlobalDTO implements Serializable {
	private static final long serialVersionUID = -106627311473466785L;
	
	protected Long id;
	protected String key;
	protected String value;
	protected List<Long> valuesIds;
	
	public GlobalDTO() {
	}
	
	public GlobalDTO(Long id, String key, String value, Long[] valuesIds) {
		this.id = id;
		this.key = key;
		this.value = value;
		
		if (valuesIds != null) {
			this.valuesIds = Arrays.asList(valuesIds);
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public List<Long> getValuesIds() {
		return valuesIds;
	}

	public void setValuesIds(List<Long> valuesIds) {
		this.valuesIds = valuesIds;
	}
}
