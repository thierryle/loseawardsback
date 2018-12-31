/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;

/**
 * Classe représentant les données nécessaires à une restauration par URL.
 */
public class RestoreURLDTO implements Serializable {
	private static final long serialVersionUID = 1968071687329459552L;
	
	private String url;
	private Boolean restoreUsers;
	private Boolean restoreCategoriesAndNominations;
	private Boolean restoreUsersAndCategoriesArchive;
	private Boolean restoreArchives;
	
	public RestoreURLDTO() {
	}
	
	public RestoreURLDTO(final String url, final Boolean restoreUsers, final Boolean restoreCategoriesAndNominations, final Boolean restoreUsersAndCategoriesArchive, final Boolean restoreArchives) {
		this.url = url;
		this.restoreUsers = restoreUsers;
		this.restoreCategoriesAndNominations = restoreCategoriesAndNominations;
		this.restoreUsersAndCategoriesArchive = restoreUsersAndCategoriesArchive;
		this.restoreArchives = restoreArchives;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Boolean getRestoreUsers() {
		return restoreUsers;
	}
	
	public void setRestoreUsers(Boolean restoreUsers) {
		this.restoreUsers = restoreUsers;
	}
	
	public Boolean getRestoreCategoriesAndNominations() {
		return restoreCategoriesAndNominations;
	}
	
	public void setRestoreCategoriesAndNominations(
			Boolean restoreCategoriesAndNominations) {
		this.restoreCategoriesAndNominations = restoreCategoriesAndNominations;
	}
	
	public Boolean getRestoreUsersAndCategoriesArchive() {
		return restoreUsersAndCategoriesArchive;
	}
	
	public void setRestoreUsersAndCategoriesArchive(
			Boolean restoreUsersAndCategoriesArchive) {
		this.restoreUsersAndCategoriesArchive = restoreUsersAndCategoriesArchive;
	}
	
	public Boolean getRestoreArchives() {
		return restoreArchives;
	}
	
	public void setRestoreArchives(Boolean restoreArchives) {
		this.restoreArchives = restoreArchives;
	}
}
