package fr.loseawards.archivecategory.dto;

import java.io.Serializable;

public class ArchiveCategoryDTO implements Serializable {
	private static final long serialVersionUID = -23549859398623950L;
	
	private Long id;
	private String name;

	public ArchiveCategoryDTO() {
	}

	public ArchiveCategoryDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
