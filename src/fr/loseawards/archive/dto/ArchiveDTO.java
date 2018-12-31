/**
 * 
 */
package fr.loseawards.archive.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Classe représentant une archive.
 */
public class ArchiveDTO implements Serializable {
	private static final long serialVersionUID = -2721602377984019751L;
	
	private Long id;
	private List<Long> categoriesIds;
	private Integer year;
	private Map<Integer, List<Long>> ranking;
	
	public ArchiveDTO() {
	}
	
	public ArchiveDTO(Long id, Integer year, Long[] categoriesIds, Map<Integer, List<Long>> ranking) {
		this.id = id;
		this.year = year;
		if (categoriesIds != null) {
			this.categoriesIds = Arrays.asList(categoriesIds);
		}
		this.ranking = ranking;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getCategoriesIds() {
		return categoriesIds;
	}

	public void setCategoriesIds(List<Long> categoriesIds) {
		this.categoriesIds = categoriesIds;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Map<Integer, List<Long>> getRanking() {
		return ranking;
	}

	public void setRanking(Map<Integer, List<Long>> ranking) {
		this.ranking = ranking;
	}
}
