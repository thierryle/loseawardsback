package fr.loseawards.category.dto;

import java.io.Serializable;

public class CategoryDTO implements Serializable {
	private static final long serialVersionUID = -6737965223583402732L;
	
	private Long id;
	private String name;

	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String name) {
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
