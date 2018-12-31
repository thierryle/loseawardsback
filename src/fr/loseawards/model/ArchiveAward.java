package fr.loseawards.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant une archive.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchiveAward implements Serializable {
	private static final long serialVersionUID = 2738917976123802531L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Integer year;
	
	@Persistent
	protected Long categoryId;
	
	@Persistent
	protected Long[] usersIds;
	
	@Persistent
	protected String reason;	
	
	public ArchiveAward() {
	}
	
	public ArchiveAward(Long id, Integer year, Long categoryId, List<Long> usersIds, String reason) {
		this.id = id;
		this.year = year;
		this.categoryId = categoryId;
		if (usersIds != null) {
			this.usersIds = (Long[]) usersIds.toArray(new Long[usersIds.size()]);
		}
		this.reason = reason;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long[] getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(Long[] usersIds) {
		this.usersIds = usersIds;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
