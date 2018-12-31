/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Classe représentant une catégorie avec les nominations.
 */
public class BackupCategoryWithNominationsDTO implements Serializable {
	private static final long serialVersionUID = 7253021603177351425L;
	
	private String name;
	private List<BackupNominationDTO> nominations;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<BackupNominationDTO> getNominations() {
		return nominations;
	}
	
	public void setNominations(List<BackupNominationDTO> nominations) {
		this.nominations = nominations;
	}
}
