package fr.loseawards.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant la décision du président dans une égalité pour une catégorie.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Decision implements Serializable {
	private static final long serialVersionUID = 5735245875868068019L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Long categoryId;
	
	@Persistent
	protected Long nominatedId;
	
	public Decision() {
	}
	
	public Decision(Long id, Long categoryId, Long nominatedId) {
		this.id = id;
		this.categoryId = categoryId;
		this.nominatedId = nominatedId;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public Long getNominatedId() {
		return nominatedId;
	}

	public void setNominatedId(Long nominatedId) {
		this.nominatedId = nominatedId;
	}
}