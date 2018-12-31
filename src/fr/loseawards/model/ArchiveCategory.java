/**
 * 
 */
package fr.loseawards.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant une catégorie dans les archives.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchiveCategory implements Serializable {
	private static final long serialVersionUID = 751560734487744011L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected String name;
	
	public ArchiveCategory() {
	}
	
	public ArchiveCategory(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
