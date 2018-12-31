/**
 * 
 */
package fr.loseawards.archiveaward.dto;

import java.io.Serializable;
import java.util.List;

import fr.loseawards.archivereport.dto.ArchiveReportDTO;

/**
 * Classe représentant un bundle pour afficher une archive avec ses résultats et son rapport.
 */
public class ArchiveAwardsAndReportDTO implements Serializable {
	private static final long serialVersionUID = 4168750802787546964L;
	
	private List<ArchiveAwardDTO> archiveAwards;
	private ArchiveReportDTO archiveReport;
	
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
