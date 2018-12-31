package fr.loseawards.archiveaward.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StatUserDTO implements Serializable {
	private static final long serialVersionUID = 8415928138751435577L;
	
	private Map<Long, List<Integer>> awardsByCategory;
	private List<GraphicsDatumDTO> graphicsData;
	private ProgressionGraphicsDataDTO progressionGraphicsData;

	public Map<Long, List<Integer>> getAwardsByCategory() {
		return awardsByCategory;
	}

	public void setAwardsByCategory(Map<Long, List<Integer>> awardsByCategory) {
		this.awardsByCategory = awardsByCategory;
	}

	public List<GraphicsDatumDTO> getGraphicsData() {
		return graphicsData;
	}

	public void setGraphicsData(List<GraphicsDatumDTO> graphicsData) {
		this.graphicsData = graphicsData;
	}

	public ProgressionGraphicsDataDTO getProgressionGraphicsData() {
		return progressionGraphicsData;
	}

	public void setProgressionGraphicsData(
			ProgressionGraphicsDataDTO progressionGraphicsData) {
		this.progressionGraphicsData = progressionGraphicsData;
	}
}
