/**
 * 
 */
package fr.loseawards.image.dto;

import java.io.Serializable;
import java.util.List;

import fr.loseawards.nomination.dto.NominationDTO;

/**
 * Classe contenant les données nécessaires à l'écran des images.
 */
public class ImageBundleDTO implements Serializable {
	private static final long serialVersionUID = 913521960565066507L;
	
	private List<NominationDTO> nominations;

	public List<NominationDTO> getNominations() {
		return nominations;
	}

	public void setNominations(List<NominationDTO> nominations) {
		this.nominations = nominations;
	}
}
