/**
 * 
 */
package fr.loseawards.nomination.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe contenant les données nécessaires à l'écran des nominations.
 */
public class NominationBundleDTO implements Serializable {
	private static final long serialVersionUID = -7391599356901180085L;
	
	// Nominations regroupées par catégorie
	private Map<Long, List<NominationDTO>> nominations;
	
	public Map<Long, List<NominationDTO>> getNominations() {
		return nominations;
	}
	
	public void setNominations(Map<Long, List<NominationDTO>> nominations) {
		this.nominations = nominations;
	}
	
	public List<NominationDTO> getNominationsAsList() {
		List<NominationDTO> nominationsAsList = new ArrayList<NominationDTO>();
		for (Long categoryId : nominations.keySet()) {
			List<NominationDTO> nominationsOfOneCategory = nominations.get(categoryId);
			if (nominationsOfOneCategory != null) {
				nominationsAsList.addAll(nominationsOfOneCategory);
			}
		}
		return nominationsAsList;
	}
}
