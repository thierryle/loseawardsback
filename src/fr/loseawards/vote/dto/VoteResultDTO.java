/**
 * 
 */
package fr.loseawards.vote.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.loseawards.nomination.dto.NominationDTO;

public class VoteResultDTO implements Serializable {
	private static final long serialVersionUID = 917429137518805529L;
	
	private Map<Long, List<Long>> winnersByCategory; // ID des vainqueurs par catégorie
	private Map<Long, List<Long>> secondsByCategory; // ID des deuxièmes par catégorie
	private Map<Long, Long> decisionsByCategory; // Pour les catégories avec ex aequo, ID du vainqueur par décision
	private Map<Long, NominationDTO> nominationsById; // Nominations classées par ID
	private Map<Long, List<VoteDTO>> votesByCategory; // Votes par catégorie
	private List<Long> losers; // ID des losers de l'année
	private List<Long> grandLosers; // En cas d'ex aequo parmi les losers de l'année, ID du Grand Loser au nombre de votes 
	private Integer grandLosersNbVotes; // Nombre de votes recueillis par le Grand Loser
	private Map<Long, Integer> nbAwardsByUser; // Nombre de récompenses par compétiteur
	private Map<Integer, List<Long>> ranking; // Compétiteurs classés par rang
	
	public Map<Long, List<Long>> getWinnersByCategory() {
		return winnersByCategory;
	}
	
	public void setWinnersByCategory(Map<Long, List<Long>> winnersByCategory) {
		this.winnersByCategory = winnersByCategory;
	}
	
	public Map<Long, List<Long>> getSecondsByCategory() {
		return secondsByCategory;
	}

	public void setSecondsByCategory(Map<Long, List<Long>> secondsByCategory) {
		this.secondsByCategory = secondsByCategory;
	}

	public Map<Long, Long> getDecisionsByCategory() {
		return decisionsByCategory;
	}
	
	public void setDecisionsByCategory(Map<Long, Long> decisionsByCategory) {
		this.decisionsByCategory = decisionsByCategory;
	}

	public Map<Long, NominationDTO> getNominationsById() {
		return nominationsById;
	}

	public void setNominationsById(Map<Long, NominationDTO> nominationsById) {
		this.nominationsById = nominationsById;
	}

	public Map<Long, List<VoteDTO>> getVotesByCategory() {
		return votesByCategory;
	}

	public void setVotesByCategory(Map<Long, List<VoteDTO>> votesByCategory) {
		this.votesByCategory = votesByCategory;
	}

	public List<Long> getLosers() {
		return losers;
	}

	public void setLosers(List<Long> losers) {
		this.losers = losers;
	}

	public List<Long> getGrandLosers() {
		return grandLosers;
	}

	public void setGrandLosers(List<Long> grandLosers) {
		this.grandLosers = grandLosers;
	}
	
	public Integer getGrandLosersNbVotes() {
		return grandLosersNbVotes;
	}

	public void setGrandLosersNbVotes(Integer grandLosersNbVotes) {
		this.grandLosersNbVotes = grandLosersNbVotes;
	}

	public Map<Long, Integer> getNbAwardsByUser() {
		return nbAwardsByUser;
	}

	public void setNbAwardsByUser(Map<Long, Integer> nbAwardsByUser) {
		this.nbAwardsByUser = nbAwardsByUser;
	}

	public Map<Integer, List<Long>> getRanking() {
		return ranking;
	}

	public void setRanking(Map<Integer, List<Long>> ranking) {
		this.ranking = ranking;
	}
}
