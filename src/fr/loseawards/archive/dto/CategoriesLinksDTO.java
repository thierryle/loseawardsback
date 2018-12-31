/**
 * 
 */
package fr.loseawards.archive.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.loseawards.archivecategory.dto.ArchiveCategoryDTO;

/**
 * Classe représentant une archive avec les récompenses.
 */
public class CategoriesLinksDTO implements Serializable {
	private static final long serialVersionUID = -5224621285010668163L;
	
	private Integer year;
	private Map<Long, Long> links;
	private List<ArchiveCategoryDTO> archiveCategories;
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Map<Long, Long> getLinks() {
		return links;
	}
	
	public void setLinks(Map<Long, Long> links) {
		this.links = links;
	}
	
	public List<ArchiveCategoryDTO> getArchiveCategories() {
		
		return archiveCategories;
	}
	
	public void setArchiveCategories(List<ArchiveCategoryDTO> archiveCategories) {
		this.archiveCategories = archiveCategories;
	}
}
