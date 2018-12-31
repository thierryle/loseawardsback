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
public class Archive implements Serializable {
	private static final long serialVersionUID = 2738917976123802531L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Integer year;
	
	@Persistent
	protected Long[] categoriesIds;
	
	public Archive() {
	}
	
	public Archive(Long id, Integer year, List<Long> categoriesIds) {
		this.id = id;
		if (categoriesIds != null) {
			this.categoriesIds = (Long[]) categoriesIds.toArray(new Long[categoriesIds.size()]);
		}
		this.year = year;
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

	public Long[] getCategoriesIds() {
		return categoriesIds;
	}

	public void setCategoriesIds(Long[] categoriesIds) {
		this.categoriesIds = categoriesIds;
	}
}
