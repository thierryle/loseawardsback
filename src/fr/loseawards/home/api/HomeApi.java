package fr.loseawards.home.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.category.api.CategoryApi;
import fr.loseawards.global.api.GlobalApi;
import fr.loseawards.home.dto.HomeBundleDTO;
import fr.loseawards.model.Category;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.nomination.api.NominationApi;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.user.api.UserApi;
import fr.loseawards.util.Converter;
import fr.loseawards.vote.api.VoteApi;

@Path("/home")
@Produces(MediaType.APPLICATION_JSON)
public class HomeApi extends AbstractServiceApi {
	private static final int NOMINATIONS_NUMBER = 5;
	
	protected UserApi userApi = null;
	protected CategoryApi categoryApi = null;
	protected GlobalApi globalApi = null;
	protected VoteApi voteApi = null;
	protected NominationApi nominationApi = null;
	
	/**
	 * Retourne le bundle nécessaire à la page d'accueil.
	 * http://localhost:8888/api/home/bundle
	 * @return
	 */
	@GET
	@Path("/bundle")
	public HomeBundleDTO getHomeBundle() {
		HomeBundleDTO homeBundleDTO = new HomeBundleDTO();
		
		homeBundleDTO.setUsers(getUserApi().getUsers());
		homeBundleDTO.setCategories(getCategoryApi().getCategories());
		
		// Récupération des nominations
		List<Nomination> nominations = getPersistenceService().getOrderedNominations();
		homeBundleDTO.setTotalNominations(new Integer(nominations.size()));
		
		// Conversion en DTO des 5 dernières nominations
		List<NominationDTO> nominationsDTO = new ArrayList<NominationDTO>();
		for (int i = 0; i < nominations.size() && i < NOMINATIONS_NUMBER; i++) {
			Nomination nomination = nominations.get(i);
			nominationsDTO.add(Converter.toDTO(nomination));
		}
		homeBundleDTO.setNominations(nominationsDTO);
		
		// Statistiques (nombre de nominations par utilisateur)
		final Map<Long, Integer> stats = new HashMap<Long, Integer>();
		for (Nomination nomination: nominations) {
			for (Long userId : nomination.getUsersIds()) {
				Integer count = stats.get(userId);
				if (count == null) {
					count = new Integer(1);
				} else {
					count = new Integer(count.intValue() + 1);
				}
				stats.put(userId, count);
			}
		}
		
		// Tri par nombre de nominations
		List<Long> sortedUsers = new ArrayList<Long>(stats.keySet());
		Collections.sort(sortedUsers, new Comparator<Long>() {
			@Override
			public int compare(Long u1, Long u2) {
				return stats.get(u2).compareTo(stats.get(u1));				
			}
		});
		Map<Long, Integer> sortedStats = new LinkedHashMap<Long, Integer>();
		for (Long userId : sortedUsers) {
			sortedStats.put(userId, stats.get(userId));
		}		
		homeBundleDTO.setStatistiques(sortedStats);
		
		// Variables globales
		homeBundleDTO.setGlobals(getGlobalApi().getGlobals());
		
		return homeBundleDTO;
	}
	
	/**
	 * Réinitialise le site.
	 * http://localhost:8888/api/home/reset
	 */
	@DELETE
	@Path("/reset")
	public void reset() {
		getVoteApi().deleteVotes();
		getNominationApi().deleteNominations();
		getCategoryApi().deleteCategories();
	}
	
	/**
	 * Nettoie la base de données.
	 * http://localhost:8888/api/home/clean
	 */
	@GET
	@Path("/clean")
	public void clean() {
		// Récupération des catégories et extraction des ID
		List<Category> categories = getPersistenceService().getCategories();
		List<Long> categoriesIds = new ArrayList<Long>();
		for (Category category: categories) {
			categoriesIds.add(category.getId());
		}
		
		// Récupération des utilisateurs et extraction des ID
		List<User> users = getPersistenceService().getUsers();
		List<Long> usersIds = new ArrayList<Long>();
		for (User user: users) {
			usersIds.add(user.getId());
		}
		
		// Récupération des nominations et vérification que la catégorie et les utilisateurs existent toujours
		List<Nomination> nominations = getPersistenceService().getNominations();
		List<Long> nominationsIds = new ArrayList<Long>();
		for (Nomination nomination : nominations) {
			if (nomination.getCategoryId() == null || nomination.getUsersIds() == null || !categoriesIds.contains(nomination.getCategoryId())) {
				getPersistenceService().deleteNomination(nomination.getId());
			} else {
				boolean deleted = false;
				for (Long userId : nomination.getUsersIds()) {
					if (!usersIds.contains(userId)) {
						getPersistenceService().deleteNomination(nomination.getId());
						deleted = true;
						break;
					}
				}
				if (!deleted) {
					nominationsIds.add(nomination.getId());
				}
			}
		}
		
		// Récupération des commentaires et vérification que la nomination et l'utilisateur existent toujours
		List<Comment> comments = getPersistenceService().getComments();
		for (Comment comment : comments) {
			if (comment.getAuthorId() == null || comment.getNominationId() == null || !usersIds.contains(comment.getAuthorId()) || !nominationsIds.contains(comment.getNominationId())) {
				getPersistenceService().deleteComment(comment.getId());
			}
		}
	}
		
	protected UserApi getUserApi() {
		if (userApi == null) {
			userApi = resourceContext.getResource(UserApi.class);
		}
		return userApi;
	}
	
	protected CategoryApi getCategoryApi() {
		if (categoryApi == null) {
			categoryApi = resourceContext.getResource(CategoryApi.class);
		}
		return categoryApi;
	}	
	
	protected GlobalApi getGlobalApi() {
		if (globalApi == null) {
			globalApi = resourceContext.getResource(GlobalApi.class);
		}
		return globalApi;
	}
	
	protected VoteApi getVoteApi() {
		if (voteApi == null) {
			voteApi = resourceContext.getResource(VoteApi.class);
		}
		return voteApi;
	}
	
	protected NominationApi getNominationApi() {
		if (nominationApi == null) {
			nominationApi = resourceContext.getResource(NominationApi.class);
		}
		return nominationApi;
	}
}
