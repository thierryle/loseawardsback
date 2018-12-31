/**
 * 
 */
package fr.loseawards.archive.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.loseawards.archivecategory.dto.ArchiveCategoryDTO;
import fr.loseawards.archiveuser.dto.ArchiveUserDTO;

/**
 * Classe contenant les données nécessaires à l'écran d'archive.
 */
public class ArchiveBundleDTO implements Serializable {
	private static final long serialVersionUID = 7042602674460073902L;
	
	private List<ArchiveDTO> archives;
	private List<ArchiveUserDTO> archiveUsers;
	private List<ArchiveCategoryDTO> archiveCategories;
	private Map<Integer, List<Long>> losersByYear;
	
	public List<ArchiveDTO> getArchives() {
		return archives;
	}

	public void setArchives(List<ArchiveDTO> archives) {
		this.archives = archives;
	}

	public List<ArchiveUserDTO> getArchiveUsers() {
		return archiveUsers;
	}
	
	public void setArchiveUsers(List<ArchiveUserDTO> archiveUsers) {
		this.archiveUsers = archiveUsers;
	}
	
	public List<ArchiveCategoryDTO> getArchiveCategories() {
		return archiveCategories;
	}
	
	public void setArchiveCategories(List<ArchiveCategoryDTO> archiveCategories) {
		this.archiveCategories = archiveCategories;
	}

	public Map<Integer, List<Long>> getLosersByYear() {
		return losersByYear;
	}

	public void setLosersByYear(Map<Integer, List<Long>> losersByYear) {
		this.losersByYear = losersByYear;
	}
}
