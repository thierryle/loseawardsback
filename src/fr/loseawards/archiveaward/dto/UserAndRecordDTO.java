package fr.loseawards.archiveaward.dto;

import java.io.Serializable;

public class UserAndRecordDTO implements Serializable {
	private static final long serialVersionUID = 4594386642610862760L;
	
	private Long userId;
	private Integer record;
	
	public UserAndRecordDTO(final Long userId, final Integer record) {
		this.userId = userId;
		this.record = record;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Integer getRecord() {
		return record;
	}
	
	public void setRecord(Integer record) {
		this.record = record;
	}	
}
