/**
 * 
 */
package fr.loseawards.user.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.loseawards.nomination.dto.NominationDTO;

/**
 * Classe contenant les données nécessaires à l'écran des utilisateurs.
 */
public class UserBundleDTO implements Serializable {
	private static final long serialVersionUID = -2802999600734835381L;
	
	// Nominations regroupées par utilisateurs
	private Map<Long, List<NominationDTO>> nominations;
	
	public Map<Long, List<NominationDTO>> getNominations() {
		return nominations;
	}
	
	public void setNominations(Map<Long, List<NominationDTO>> nominations) {
		this.nominations = nominations;
	}
}
