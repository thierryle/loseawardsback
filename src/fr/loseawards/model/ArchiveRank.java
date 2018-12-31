package fr.loseawards.model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant un classement.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchiveRank implements Serializable {
	private static final long serialVersionUID = -4337679270887672372L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Integer year;
	
	@Persistent
	protected Integer position;
	
	@Persistent
	protected Long[] usersIds;
	
	public ArchiveRank() {
	}
	
	public ArchiveRank(Long id, Integer year, Integer position, List<Long> usersIds) {
		this.id = id;
		this.year = year;
		this.position = position;
		if (usersIds != null) {
			this.usersIds = (Long[]) usersIds.toArray(new Long[usersIds.size()]);
		}
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long[] getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(Long[] usersIds) {
		this.usersIds = usersIds;
	}
}
