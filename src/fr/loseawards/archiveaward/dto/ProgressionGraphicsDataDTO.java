package fr.loseawards.archiveaward.dto;

import java.io.Serializable;
import java.util.List;

public class ProgressionGraphicsDataDTO implements Serializable {
	private static final long serialVersionUID = 863246022701766176L;
	
	private List<Integer> years;
	private List<Integer> nbAwards;
	private List<Integer> ranks;
	
	public List<Integer> getYears() {
		return years;
	}
	
	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public List<Integer> getNbAwards() {
		return nbAwards;
	}

	public void setNbAwards(List<Integer> nbAwards) {
		this.nbAwards = nbAwards;
	}

	public List<Integer> getRanks() {
		return ranks;
	}
	
	public void setRanks(List<Integer> ranks) {
		this.ranks = ranks;
	}	
}
