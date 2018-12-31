package fr.loseawards.decision.dto;

import java.io.Serializable;

/**
 * Classe représentant la décision du président dans une égalité pour une catégorie.
 */
public class DecisionDTO implements Serializable {
	private static final long serialVersionUID = 68516224795361534L;
	
	protected Long id;
	protected Long categoryId;
	protected Long nominatedId;
	
	public DecisionDTO() {
	}
	
	public DecisionDTO(Long id, Long categoryId, Long nominatedId) {
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