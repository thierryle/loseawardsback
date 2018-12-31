/**
 * 
 */
package fr.loseawards.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant une variable globale à l'application.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Global implements Serializable {
	private static final long serialVersionUID = 1410925640268372275L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected String key;
	
	@Persistent
	protected String value;
	
	@Persistent
	protected Long[] valuesIds;
	
	public Global() {
	}
	
	public Global(Long id, String key, String value, List<Long> valuesIds) {
		this.id = id;
		this.key = key;
		this.value = value;
		
		if (valuesIds != null && !valuesIds.isEmpty()) {
			this.valuesIds = (Long[]) valuesIds.toArray(new Long[0]);
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

	public Long[] getValuesIds() {
		return valuesIds;
	}

	public void setValuesIds(Long[] valuesIds) {
		this.valuesIds = valuesIds;
	}
	
	public void addValueId(Long valueId) {
		int oldSize = valuesIds != null ? valuesIds.length : 0;
		Long[] newValuesIds = new Long[oldSize + 1];
		if (valuesIds != null) {
			System.arraycopy(valuesIds, 0, newValuesIds, 0, oldSize);
		}
		newValuesIds[newValuesIds.length - 1] = valueId;
		
		valuesIds = newValuesIds;
	}
}
