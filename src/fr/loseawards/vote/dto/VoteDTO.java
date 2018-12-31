/**
 * 
 */
package fr.loseawards.vote.dto;

import java.io.Serializable;

public class VoteDTO implements Serializable {
	private static final long serialVersionUID = 7060829044690837938L;
	
	protected Long id;
	protected Long categoryId;
	protected Long voterId;
	protected Long nominationId;
	protected String reason;
	
	public VoteDTO() {
	}
	
	public VoteDTO(Long id, Long categoryId, Long voterId, Long nominationId, String reason) {
		this.id = id;
		this.categoryId = categoryId;
		this.voterId = voterId;
		this.nominationId = nominationId;
		this.reason = reason;
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
	
	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	public Long getNominationId() {
		return nominationId;
	}

	public void setNominationId(Long nominationId) {
		this.nominationId = nominationId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
