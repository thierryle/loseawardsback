/**
 * 
 */
package fr.loseawards.archivereport.dto;

import java.io.Serializable;

import com.google.appengine.api.datastore.Blob;

import fr.loseawards.util.Converter;

/**
 * Classe représentant un compte-rendu dans les archives.
 */
public class ArchiveReportDTO implements Serializable {
	private static final long serialVersionUID = 3189385711534646322L;
	
	private Long id;
	private Integer year;
	private String report;
	
	public ArchiveReportDTO() {
	}
	
	public ArchiveReportDTO(Long id, Integer year, Blob report) {
		this.id = id;
		this.year = year;
		if (report != null) {
			this.report = Converter.blobToString(report);
		}		
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

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
}
