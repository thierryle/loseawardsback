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
 * Classe représentant un utilisateur dans les archives.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchiveUser implements Serializable {
	private static final long serialVersionUID = 3844788924555881857L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String firstName;
	
	@Persistent
	private String lastName;
	
	@Persistent
	private Integer firstYear;
	
	@Persistent
	private Integer lastYear;
	
	public ArchiveUser() {
	}
	
	public ArchiveUser(final Long id, final String firstName, final String lastName, final Integer firstYear, final Integer lastYear) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.firstYear = firstYear;
		this.lastYear = lastYear;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Integer getFirstYear() {
		return firstYear;
	}

	public void setFirstYear(Integer firstYear) {
		this.firstYear = firstYear;
	}

	public Integer getLastYear() {
		return lastYear;
	}

	public void setLastYear(Integer lastYear) {
		this.lastYear = lastYear;
	}

	public String getDisplayName() {
		StringBuilder name = new StringBuilder(firstName);
		if (lastName != null && !lastName.isEmpty()) {
			name.append(" ");
			name.append(lastName);
		}
		return name.toString();
	}
}
