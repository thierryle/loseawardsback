package fr.loseawards.archiveaward.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StatCategoryDTO implements Serializable {
	private static final long serialVersionUID = -7148218155583770358L;
	
	private Map<Long, List<Integer>> awardsByUser;
	private List<GraphicsDatumDTO> graphicsData;
	private List<Integer> appearingYears;

	public Map<Long, List<Integer>> getAwardsByUser() {
		return awardsByUser;
	}

	public void setAwardsByUser(Map<Long, List<Integer>> awardsByUser) {
		this.awardsByUser = awardsByUser;
	}

	public List<GraphicsDatumDTO> getGraphicsData() {
		return graphicsData;
	}

	public void setGraphicsData(List<GraphicsDatumDTO> graphicsData) {
		this.graphicsData = graphicsData;
	}

	public List<Integer> getAppearingYears() {
		return appearingYears;
	}

	public void setAppearingYears(List<Integer> appearingYears) {
		this.appearingYears = appearingYears;
	}
}
