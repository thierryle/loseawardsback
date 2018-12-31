
/**
 * 
 */
package fr.loseawards.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant une nomination.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Nomination implements Serializable {
	private static final long serialVersionUID = 7246648769595703830L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Long categoryId;
	
	@Persistent
	protected Long[] usersIds;
	
	@Persistent
	protected String reason;
	
	@Persistent
	protected Date date;
	
	@Persistent
	protected Long imageId;
	
	public Nomination() {
	}
	
	public Nomination(Long id, List<Long> usersIds, Long categoryId, String reason, Date date, Long imageId) {
		this.id = id;
		if (usersIds != null) {
			this.usersIds = (Long[]) usersIds.toArray(new Long[0]);
		}		
		this.categoryId = categoryId;
		this.reason = reason;
		this.date = date;
		this.imageId = imageId;
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

	public Long[] getUsersIds() {
//		List<Long> ids = new ArrayList<Long>();
//		if (usersIds != null) {
//			for(Long id: usersIds) {
//				ids.add(id);
//			}
//		}
//		
//		return ids.toArray(new Long[ids.size()]);
		return this.usersIds;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}
}
