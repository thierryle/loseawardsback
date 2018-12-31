package fr.loseawards.vote.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.decision.api.DecisionApi;
import fr.loseawards.decision.dto.DecisionDTO;
import fr.loseawards.model.Category;
import fr.loseawards.model.Global;
import fr.loseawards.model.GlobalKey;
import fr.loseawards.model.User;
import fr.loseawards.model.Vote;
import fr.loseawards.nomination.api.NominationApi;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;
import fr.loseawards.vote.dto.VoteDTO;
import fr.loseawards.vote.dto.VoteResultDTO;

@Path("/votes")
@Produces(MediaType.APPLICATION_JSON)
public class VoteApi extends AbstractServiceApi {
	private final Logger log = Logger.getLogger(VoteApi.class.getName());
	protected Cache cachedVotes = null;
	
	protected DecisionApi decisionApi;
	protected NominationApi nominationApi;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public VoteApi() {
		// Initialisation du cache
		Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, 60);

        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cachedVotes = cacheFactory.createCache(props);
        } catch (CacheException e) {
        	// Pas bloquant
        	log.log(Level.WARNING, "Problème dans la création des caches", e);
        }
	}
	
	protected Cache getCachedVotes() {
		return cachedVotes;
	}
	
	/**
	 * Retourne tous les votes.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<VoteDTO> getVotes() {
		List<VoteDTO> votesDTO = new ArrayList<VoteDTO>();
		
		List<User> users = getPersistenceService().getUsers();
		List<Vote> votes = getPersistenceService().getVotes();
		
		for (User user : users) {
			// On prend en priorité les votes dans le cache
			List<VoteDTO> votesOfOneUser = (List<VoteDTO>) getCachedVotes().get(user.getId());
			if (votesOfOneUser != null) {
				votesDTO.addAll((List<VoteDTO>) votesOfOneUser);
			} else {
				// Sinon on prend dans la base
				for (Vote vote : votes) {
					if (vote.getVoterId().equals(user.getId())) {
						votesDTO.add(Converter.toDTO(vote));
					}					
				}
			}
		}
		
		return votesDTO;
	}
	
	/**
	 * Retourne un vote.
	 * GET http://localhost:8888/api/votes/5886785255112704
	 * @param voteId
	 * @return
	 */
	@GET
	@Path("/{voteId}")
	public VoteDTO getVote(@PathParam("voteId") final Long voteId) {
		Vote vote = getPersistenceService().getVote(voteId);
		if (vote == null) {
			return null;
		}
		return Converter.toDTO(vote);
	}
	
	/**
	 * Retourne tous les votes ou les votes d'un utilisateur.
	 * GET http://localhost:8888/api/votes
	 * GET http://localhost:8888/api/votes?userId=5726256557457408
	 * @param userId
	 * @return
	 */
	@GET
	public List<VoteDTO> getVotes(@QueryParam("userId") final Long userId) {
		if (userId == null) {
			return getVotes();
		}
		
		List<Vote> votes = getPersistenceService().getVotesByVoter(userId);
		
		// Conversion en DTO
		return Converter.toVotesDTO(votes);
	}
	
	/**
	 * Retourne les résultats des votes.
	 * GET http://localhost:8888/api/votes/result
	 * @return
	 */
	@GET
	@Path("/result")
	public VoteResultDTO getVoteResult() {
		VoteResultDTO voteResultDTO = new VoteResultDTO();
		
		Map<Long, List<Long>> winnersByCategory = new HashMap<Long, List<Long>>();
		voteResultDTO.setWinnersByCategory(winnersByCategory);
		
		Map<Long, List<Long>>secondsByCategory = new HashMap<Long, List<Long>>();
		voteResultDTO.setSecondsByCategory(secondsByCategory);
		
		// Récupération de tous les votes et classement par catégorie
		List<VoteDTO> votesDTO = getVotes();
		if (votesDTO == null || votesDTO.isEmpty()) {
			return null;
		}
		Map<Long, List<VoteDTO>> votesByCategory = Util.groupByProperty(votesDTO, "categoryId");
		voteResultDTO.setVotesByCategory(votesByCategory);
		
		// Récupération des catégories
		List<Category> categories = getPersistenceService().getCategories();
		
		// Récupération des nominations et classement par ID
		List<NominationDTO> nominationsDTO = getNominationApi().getNominations();
		Map<Long, NominationDTO> nominationsById = Util.sortByProperty(nominationsDTO, "id");
		voteResultDTO.setNominationsById(nominationsById);
		
		// Récupération des décisions et classement par catégorie
		List<DecisionDTO> decisionsDTO = getDecisionApi().getDecisions();
		Map<Long, Long> decisionsByCategory = new HashMap<Long, Long>();
		for (DecisionDTO decisionDTO : decisionsDTO) {
			decisionsByCategory.put(decisionDTO.getCategoryId(), decisionDTO.getNominatedId());
		}
		voteResultDTO.setDecisionsByCategory(decisionsByCategory);

		// Users ayant reçu des votes, toutes catégories confondues (pour départager les vainqueurs)
		List<Long> allCategoriesNominatedIds = new ArrayList<Long>();
		
		// Tous les vainqueurs des différentes catégories (pour trouver ensuite le ou les grands vainqueurs)
		List<Long> allWinners = new ArrayList<Long>();
		
		// Parcours des catégories
		for (Category category: categories) {
			List<VoteDTO> votesOfOneCategory = votesByCategory.get(category.getId());
			if (votesOfOneCategory != null) {
				// Users ayant reçu des votes dans la catégorie courante
				List<Long> nominatedIds = new ArrayList<Long>();
				
				// Recherche des votes de la catégorie courante
				for (VoteDTO vote : votesOfOneCategory) {
					if (vote.getNominationId() != null) {
						// On récupère la nomination correspondant au vote pour récupérer les users
						NominationDTO nominationDTO = nominationsById.get(vote.getNominationId());
						for (Long userId: nominationDTO.getUsersIds()) {
							nominatedIds.add(userId);
							allCategoriesNominatedIds.add(userId);
						}
					}
				}
				
				// Vainqueurs de la catégorie courante
				List<Long> winners = Util.getMostRecurrentElements(nominatedIds, null);
				if (winners.size() == 1) {
					allWinners.addAll(winners);
				}
				winnersByCategory.put(category.getId(), winners);
				
				// Recherche des deuxièmes
				nominatedIds.removeAll(winners);
				if (!nominatedIds.isEmpty()) {
					List<Long> seconds = Util.getMostRecurrentElements(nominatedIds, null);
					secondsByCategory.put(category.getId(), seconds);
				}
				
				// Décision du président
				if (winners.size() > 1 && decisionsDTO != null) {
					Long nominatedId = decisionsByCategory.get(category.getId());
					if (nominatedId != null && nominatedId != -1L) {
						if (nominatedId == -2L) {
							// Tous
							allWinners.addAll(winners);
						} else {
							allWinners.add(nominatedId);
						}
					}
				}
			}
		}
		
		// Losers de l'année
		List<Long> losers = Util.getMostRecurrentElements(allWinners, null);
		voteResultDTO.setLosers(losers);
		if (losers.size() > 1) {
			// Des ex aequo dans les losers, on cherche les Grands Losers
			List<Long> grandLosers = Util.getMostRecurrentElements(allCategoriesNominatedIds, losers);
			voteResultDTO.setGrandLosers(grandLosers);
			voteResultDTO.setGrandLosersNbVotes(Collections.frequency(allCategoriesNominatedIds, grandLosers.get(0)));
		}

		// Liste des utilisateurs avec leur nombre de récompenses
		final Map<Long, Integer> nbAwardsByUser = new HashMap<Long, Integer>();
		List<User> users = getPersistenceService().getUsers();
		for (User user : users) {
			nbAwardsByUser.put(user.getId(), Collections.frequency(allWinners, user.getId()));				
		}
		voteResultDTO.setNbAwardsByUser(nbAwardsByUser);
		
		// Ranking
		Map<Integer, List<Long>> ranking = new LinkedHashMap<Integer, List<Long>>();
		Integer rank = new Integer(1);
		List<Long> finalWinners;
		
		if (voteResultDTO.getGrandLosers() != null) {
			finalWinners = voteResultDTO.getGrandLosers();
		} else {
			finalWinners = voteResultDTO.getLosers();
		}
		ranking.put(rank, finalWinners);
		rank = new Integer(rank.intValue() + finalWinners.size());
		Integer maxNbAwards = nbAwardsByUser.get(finalWinners.get(0));
		
		for (int nbAwards = maxNbAwards.intValue(); nbAwards >= 0; nbAwards--) {
			// Qui parmi les compétiteurs a le nombre courant de récompenses, à part les Grands Champions ?
			List<Long> currentRankUsers = null;
			for (User user : users) {
				Integer nbAwardsOfOneUser = nbAwardsByUser.get(user.getId());
				if (nbAwardsOfOneUser.intValue() == nbAwards && !finalWinners.contains(user.getId())) {
					currentRankUsers = ranking.get(rank);
					if (currentRankUsers == null) {
						currentRankUsers = new ArrayList<Long>();
						ranking.put(rank, currentRankUsers);
					}
					currentRankUsers.add(user.getId());
				}
			}
			// A-t-on trouvé des compétiteurs ?
			if (currentRankUsers != null) {
				rank = new Integer(rank.intValue() + currentRankUsers.size());
			}
		}
		voteResultDTO.setRanking(ranking);
		
		return voteResultDTO;
	}
	
	/**
	 * Création des votes d'un utilisateur.
	 * POST http://localhost:8888/api/votes/bulk
	 * @param votes
	 */
	@POST
	@Path("/bulk")
	public void createVotes(final List<VoteDTO> votes) {
		if (votes != null && !votes.isEmpty()) {
			// Suppression des votes précédents de ce votant
			getPersistenceService().deleteVotesOfVoter(votes.get(0).getVoterId());
			
			// Suppression des décisions du président
			getPersistenceService().deleteDecisions();
			
			for (VoteDTO voteDTO : votes) {
				getPersistenceService().addVote(Converter.fromDTO(voteDTO));
			}
			sendNotification(votes.get(0).getVoterId());
			
			// Modification de la variable globale
			Global global = getPersistenceService().getGlobalByKey(GlobalKey.VOTERS_IDS.getKey());
			if (global != null) {
				// Le votant est-il déjà dans la liste des votants ?
				if (!Util.contains(global.getValuesIds(), votes.get(0).getVoterId())) {
					global.addValueId(votes.get(0).getVoterId());
					getPersistenceService().updateGlobal(global);
				}
			} else {
				List<Long> valuesIds = new ArrayList<Long>();
				valuesIds.add(votes.get(0).getVoterId());
				global = new Global(null, GlobalKey.VOTERS_IDS.getKey(), null, valuesIds);
				getPersistenceService().addGlobal(global);
			}
			
			// Mise en cache
			getCachedVotes().put(votes.get(0).getVoterId(), votes);
		}
	}
	
	/**
	 * Suppression de tous les votes.
	 * DELETE http://localhost:8888/api/votes
	 */
	@DELETE
	public void deleteVotes() {
		getPersistenceService().deleteVotes();
		
		// Suppression des décisions du président
		getPersistenceService().deleteDecisions();
		
		// Mise à jour de la variable globale
		Global global = getPersistenceService().getGlobalByKey(GlobalKey.VOTERS_IDS.getKey());
		if (global != null) {
			global.setValuesIds(null);
			getPersistenceService().updateGlobal(global);
		}
	}
	
	/**
	 * Envoi d'une notification pour un nouveau vote.
	 * @param voterId
	 */
	protected String sendNotification(final Long voterId) {
		List<User> users = getPersistenceService().getUsers();
		User voter = Util.getObjectById(users, voterId);
		StringBuilder builder = new StringBuilder("<p>");
		builder.append(voter.getDisplayName());
		builder.append(" a voté.</p>\n<a href=\"http://loseawards.appspot.com\">http://loseawards.appspot.com</a>");
		String message = builder.toString();
		
		if (voter != null) {
			for (User user : users) {
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					sendMail(user.getEmail(), user.getDisplayName(), "Nouveau vote", message, true);
				}
			}
		}
		return message.toString();
	}
	
	protected DecisionApi getDecisionApi() {
		if (decisionApi == null) {
			decisionApi = resourceContext.getResource(DecisionApi.class);
		}
		return decisionApi;
	}
	
	protected NominationApi getNominationApi() {
		if (nominationApi == null) {
			nominationApi = resourceContext.getResource(NominationApi.class);
		}
		return nominationApi;
	}
}
