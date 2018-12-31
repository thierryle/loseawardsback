package fr.loseawards.archiveuser.dto;

import java.io.Serializable;

public class ArchiveUserDTO implements Serializable {
	private static final long serialVersionUID = -1432946777415634708L;
	
	private Long id;
	private String firstName;
	private String lastName;
	private Integer firstYear;
	private Integer lastYear;
	
	public ArchiveUserDTO() {
	}
	
	public ArchiveUserDTO(final Long id, final String firstName, final String lastName, final Integer firstYear, final Integer lastYear) {
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
}
