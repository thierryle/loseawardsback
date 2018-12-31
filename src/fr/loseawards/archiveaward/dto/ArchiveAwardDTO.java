/**
 * 
 */
package fr.loseawards.archiveaward.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant une récompense dans les archives.
 */
public class ArchiveAwardDTO implements Serializable {
	private static final long serialVersionUID = 4597861373021375896L;
	
	private Long id;
	private Long categoryId;
	private List<Long> usersIds;
	private Integer year;
	private String reason;
	
	public ArchiveAwardDTO() {
	}
	
	public ArchiveAwardDTO(Long id, Integer year, Long categoryId, Long[] usersIds, String reason) {
		this.id = id;
		this.year = year;
		this.categoryId = categoryId;
		if (usersIds != null) {
			this.usersIds = Arrays.asList(usersIds);
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

	public List<Long> getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(List<Long> usersIds) {
		this.usersIds = usersIds;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
