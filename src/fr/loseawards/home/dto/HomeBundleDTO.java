/**
 * 
 */
package fr.loseawards.home.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.loseawards.category.dto.CategoryDTO;
import fr.loseawards.global.dto.GlobalDTO;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.user.dto.UserDTO;

/**
 * Classe contenant les données nécessaires à l'écran d'accueil.
 */
public class HomeBundleDTO implements Serializable {
	private static final long serialVersionUID = -6291046700879602800L;
	
	private List<UserDTO> users;
	private List<CategoryDTO> categories;
	private List<NominationDTO> nominations;
	private Map<Long, Integer> statistiques;
	private List<GlobalDTO> globals;
	private Integer totalNominations;
	
	public List<UserDTO> getUsers() {
		return users;
	}
	
	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

	public List<NominationDTO> getNominations() {
		return nominations;
	}

	public void setNominations(List<NominationDTO> nominations) {
		this.nominations = nominations;
	}

	public Map<Long, Integer> getStatistiques() {
		return statistiques;
	}

	public void setStatistiques(Map<Long, Integer> statistiques) {
		this.statistiques = statistiques;
	}

	public List<GlobalDTO> getGlobals() {
		return globals;
	}

	public void setGlobals(List<GlobalDTO> globals) {
		this.globals = globals;
	}

	public Integer getTotalNominations() {
		return totalNominations;
	}

	public void setTotalNominations(Integer totalNominations) {
		this.totalNominations = totalNominations;
	}
}
