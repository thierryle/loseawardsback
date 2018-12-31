/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant un backup de nomination.
 */
public class BackupNominationDTO implements Serializable {
	private static final long serialVersionUID = -3077819925988704522L;
	
	private List<String> usersNames;
	private String categoryName;
	private String reason;
	private Date date;
	
	public BackupNominationDTO() {
	}
	
	public BackupNominationDTO(String[] usersNames, String categoryName, String reason, Date date) {
		if (usersNames != null) {
			this.usersNames = Arrays.asList(usersNames);
		}
		this.categoryName = categoryName;
		this.reason = reason;
		this.date = date;
	}

	public List<String> getUsersNames() {
		return usersNames;
	}

	public void setUsersNames(List<String> usersNames) {
		this.usersNames = usersNames;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
}
