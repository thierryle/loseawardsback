/**
 * 
 */
package fr.loseawards.archive.dto;

import java.io.Serializable;
import java.util.List;

import fr.loseawards.archiveaward.dto.ArchiveAwardDTO;
import fr.loseawards.archivereport.dto.ArchiveReportDTO;

/**
 * Classe représentant une archive avec les récompenses.
 */
public class ArchiveWithAwardsAndReportDTO implements Serializable {
	private static final long serialVersionUID = 8217639253251052763L;
	
	private ArchiveDTO archive;
	private List<ArchiveAwardDTO> archiveAwards;
	private ArchiveReportDTO archiveReport;
	
	public ArchiveDTO getArchive() {
		return archive;
	}
	
	public void setArchive(ArchiveDTO archive) {
		this.archive = archive;
	}
	
	public List<ArchiveAwardDTO> getArchiveAwards() {
		return archiveAwards;
	}
	
	public void setArchiveAwards(List<ArchiveAwardDTO> archiveAwards) {
		this.archiveAwards = archiveAwards;
	}

	public ArchiveReportDTO getArchiveReport() {
		return archiveReport;
	}

	public void setArchiveReport(ArchiveReportDTO archiveReport) {
		this.archiveReport = archiveReport;
	}
}
