/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Classe représentant un backup de récompense dans les archives.
 */
public class BackupArchiveAwardDTO implements Serializable {
	private static final long serialVersionUID = -5090433463886169869L;
	
	private String categoryName;
	private List<String> usersNames;
	private Integer year;
	private String reason;
	
	public BackupArchiveAwardDTO() {
	}
	
	public BackupArchiveAwardDTO(Integer year, String categoryName, String[] usersNames, String reason) {
		this.year = year;
		this.categoryName = categoryName;
		if (usersNames != null) {
			this.usersNames = Arrays.asList(usersNames);
		}
		this.reason = reason;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<String> getUsersNames() {
		return usersNames;
	}

	public void setUsersNames(List<String> usersNames) {
		this.usersNames = usersNames;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
