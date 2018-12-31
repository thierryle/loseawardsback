/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Classe représentant un backup d'archive.
 */
public class BackupArchiveDTO implements Serializable {
	private static final long serialVersionUID = 5043111882744926797L;
	
	private List<String> categoriesNames;
	private Integer year;
	private Map<Integer, List<String>> ranking;
	
	public BackupArchiveDTO() {
	}
	
	public BackupArchiveDTO(Integer year, String[] categoriesNames) {
		this.year = year;
		if (categoriesNames != null) {
			this.categoriesNames = Arrays.asList(categoriesNames);
		}		
	}

	public List<String> getCategoriesNames() {
		return categoriesNames;
	}

	public void setCategoriesNames(List<String> categoriesNames) {
		this.categoriesNames = categoriesNames;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Map<Integer, List<String>> getRanking() {
		return ranking;
	}

	public void setRanking(Map<Integer, List<String>> ranking) {
		this.ranking = ranking;
	}
}
