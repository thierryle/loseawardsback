/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Classe représentant une archive avec les récompenses.
 */
public class BackupArchiveWithAwardsDTO implements Serializable {
	private static final long serialVersionUID = -6355195754265747812L;
	
	private BackupArchiveDTO archive;
	private List<BackupArchiveAwardDTO> archiveAwards;
	
	public BackupArchiveDTO getArchive() {
		return archive;
	}
	
	public void setArchive(BackupArchiveDTO archive) {
		this.archive = archive;
	}
	
	public List<BackupArchiveAwardDTO> getArchiveAwards() {
		return archiveAwards;
	}
	
	public void setArchiveAwards(List<BackupArchiveAwardDTO> archiveAwards) {
		this.archiveAwards = archiveAwards;
	}
}
