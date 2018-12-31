package fr.loseawards.archive.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.text.similarity.LevenshteinDistance;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.archive.dto.ArchiveBundleDTO;
import fr.loseawards.archive.dto.ArchiveDTO;
import fr.loseawards.archive.dto.ArchiveWithAwardsAndReportDTO;
import fr.loseawards.archive.dto.CategoriesLinksDTO;
import fr.loseawards.archiveaward.api.ArchiveAwardApi;
import fr.loseawards.archiveaward.dto.ArchiveAwardDTO;
import fr.loseawards.archivecategory.api.ArchiveCategoryApi;
import fr.loseawards.archivereport.api.ArchiveReportApi;
import fr.loseawards.archivereport.dto.ArchiveReportDTO;
import fr.loseawards.archiveuser.api.ArchiveUserApi;
import fr.loseawards.model.Archive;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.model.Category;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;
import fr.loseawards.vote.api.VoteApi;
import fr.loseawards.vote.dto.VoteDTO;
import fr.loseawards.vote.dto.VoteResultDTO;

@Path("/archives")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveApi extends AbstractServiceApi {
	protected ArchiveUserApi archiveUserApi = null;
	protected ArchiveCategoryApi archiveCategoryApi = null;
	protected ArchiveAwardApi archiveAwardApi = null;
	protected ArchiveReportApi archiveReportApi = null;
	protected VoteApi voteApi = null;
	
	/**
	 * Retourne la liste des archives.
	 * GET http://localhost:8888/api/archives
	 * @return
	 */
	@GET
	public List<ArchiveDTO> getArchives() {
		List<Archive> archives = getPersistenceService().getArchives();
		List<ArchiveRank> archiveRanks = getPersistenceService().getArchiveRanks();
		
		// Conversion en DTO
		List<ArchiveDTO> archivesDTO = Converter.toArchivesDTO(archives, archiveRanks);
		return archivesDTO;
	}
	
	/**
	 * Retourne une archive.
	 * GET http://localhost:8888/api/archives/4822457999425536
	 * @param archiveId
	 * @return
	 */
	@GET
	@Path("/{archiveId}")
	public ArchiveDTO getArchive(@PathParam("archiveId") final Long archiveId) {
		Archive archive = getPersistenceService().getArchive(archiveId);
		if (archive == null) {
			return null;
		}
		List<ArchiveRank> archiveRanks = getPersistenceService().getArchiveRanksByYear(archive.getYear());		
		return Converter.toDTO(archive, archiveRanks);
	}
	
	/**
	 * Retourne le bundle des archives.
	 * GET http://localhost:8888/api/archives/bundle
	 * @return
	 */
	@GET
	@Path("/bundle")
	public ArchiveBundleDTO getArchiveBundle() {
		ArchiveBundleDTO archiveBundleDTO = new ArchiveBundleDTO();
		
		List<Archive> archives = getPersistenceService().getArchives();
		List<ArchiveRank> archiveRanks = getPersistenceService().getArchiveRanks();
		
		// Conversion en DTO
		List<ArchiveDTO> archivesDTO = Converter.toArchivesDTO(archives, archiveRanks);
		archiveBundleDTO.setArchives(archivesDTO);
		
		archiveBundleDTO.setArchiveUsers(getArchiveUserApi().getArchiveUsers());
		archiveBundleDTO.setArchiveCategories(getArchiveCategoryApi().getArchiveCategories());
		
		// Grands Losers par année
		Map<Integer, List<Long>> losersByYear = new HashMap<Integer, List<Long>>();
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwardsByCategory(null);
		for (ArchiveAward archiveAward : archiveAwards) {
			losersByYear.put(archiveAward.getYear(), archiveAward.getUsersIds() != null ? Arrays.asList(archiveAward.getUsersIds()) : null);
		}
		archiveBundleDTO.setLosersByYear(losersByYear);
		
		return archiveBundleDTO;
	}
	
	/**
	 * Crée une archive.
	 * POST http://localhost:8888/api/archives
	 * @param archiveDTO
	 * @return
	 */
	@POST
	public ArchiveDTO createArchive(final ArchiveDTO archiveDTO) {
		Archive archive = Converter.fromDTO(archiveDTO);
		List<ArchiveRank> archiveRanks = Converter.fromDTO(archiveDTO.getRanking(), archiveDTO.getYear());
		getPersistenceService().addArchive(archive);
		
		// Ranking
		//getPersistenceService().deleteArchiveRanksOfYear(archiveDTO.getYear());
		if (archiveRanks != null) {
			for (ArchiveRank archiveRank: archiveRanks) {
				getPersistenceService().addArchiveRank(archiveRank);
			}
		}
		
		archiveDTO.setId(archive.getId());
		return archiveDTO;
	}
	
	/**
	 * Fait le lien entre les catégories actuelles et les catégories archive.
	 * GET http://localhost:8888/api/archives/categoriesLinks
	 * @return
	 */
	@GET
	@Path("/categoriesLinks")
	public CategoriesLinksDTO linkCategories() {
		CategoriesLinksDTO categoriesLinksDTO = new CategoriesLinksDTO();
		categoriesLinksDTO.setArchiveCategories(getArchiveCategoryApi().getArchiveCategories());		
		
		Map<Long, Long> links = new HashMap<Long, Long>();
		List<Category> categories = getPersistenceService().getCategories();
		List<ArchiveCategory> archiveCategories = getPersistenceService().getArchiveCategories();
		
		// On tente de faire un rapprochement entre les catégories actuelles et les catégories archive
		for (Category category : categories) {
			ArchiveCategory archiveCategory = findArchiveCategory(category, archiveCategories);
			if (archiveCategory != null) {
				links.put(category.getId(), archiveCategory.getId());
			} else {
				links.put(category.getId(), -1L);
			}
		}
		categoriesLinksDTO.setLinks(links);
		
		return categoriesLinksDTO;
	}
	
	/**
	 * Crée une archive à partir des résultats de vote et des liens entre les catégories précisés par l'utilisateur.
	 * @param categoriesLinks
	 */
	@POST
	@Path("/fromVoteResult")	
	public void createArchiveFromVoteResult(final CategoriesLinksDTO categoriesLinksDTO) {
		ArchiveWithAwardsAndReportDTO archiveWithAwardsAndReportDTO = new ArchiveWithAwardsAndReportDTO();
		
		// De même qu'on a liens entre les catégories, il faut faire les liens entre les utilisateurs courants et les utilisateurs archive
		Map<Long, Long> usersLinks = new HashMap<Long, Long>();
		List<User> users = getPersistenceService().getUsers();
		List<ArchiveUser> archiveUsers = getPersistenceService().getArchiveUsers();
		for (User user : users) {
			boolean found = false;
			// On fait d'abord un rapprochement avec le displayName
			for (ArchiveUser archiveUser : archiveUsers) {
				if (archiveUser.getDisplayName().equals(user.getDisplayName())) {
					usersLinks.put(user.getId(), archiveUser.getId());
					found = true;
					break;
				}
			}
			if (!found) {
				// Sinon, on fait le rapprochement sur le firstName
				for (ArchiveUser archiveUser : archiveUsers) {
					if (archiveUser.getFirstName().equals(user.getFirstName())) {
						usersLinks.put(user.getId(), archiveUser.getId());
						found = true;
						break;
					}
				}
			}
			if (!found) {
				// Problème
				throw new IllegalArgumentException("Utilisateur " + user.getDisplayName() + " non-trouvé");
			}
		}
		
		// Récupération des résultats de vote
		VoteResultDTO voteResultDTO = getVoteApi().getVoteResult();
		
		// ===== Création de l'archive =====
		
		ArchiveDTO archiveDTO = new ArchiveDTO();
		archiveDTO.setYear(categoriesLinksDTO.getYear());
		
		// Conversion des catégories en catégories archive 
		final Map<Long, ArchiveCategory> archiveCategoriesById = new HashMap<Long, ArchiveCategory>();
		for (Long categoryId : categoriesLinksDTO.getLinks().keySet()) {
			Long linkedArchiveCategoryId = categoriesLinksDTO.getLinks().get(categoryId);
			if (linkedArchiveCategoryId == -1L) {
				// L'utilisateur n'a fait aucun lien : il s'agit d'une nouvelle catégorie à créer
				Category category = getPersistenceService().getCategory(categoryId);
				ArchiveCategory newArchiveCategory = new ArchiveCategory(null, category.getName());
				getPersistenceService().addArchiveCategory(newArchiveCategory);
				
				// On range la nouvelle catégorie archive dans les liens et dans la map de classement
				linkedArchiveCategoryId = newArchiveCategory.getId();
				categoriesLinksDTO.getLinks().put(categoryId, linkedArchiveCategoryId);
				archiveCategoriesById.put(linkedArchiveCategoryId, newArchiveCategory);
			} else {
				ArchiveCategory archiveCategory = getPersistenceService().getArchiveCategory(linkedArchiveCategoryId);
				archiveCategoriesById.put(linkedArchiveCategoryId, archiveCategory);
			}
		}
		
		// Tri par ordre alphabétique
		List<Long> sortedArchiveCategoriesIds = new ArrayList<Long>(archiveCategoriesById.keySet());
		Collections.sort(sortedArchiveCategoriesIds, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				ArchiveCategory archiveCategory1 = archiveCategoriesById.get(o1);
				ArchiveCategory archiveCategory2 = archiveCategoriesById.get(o2);
				return archiveCategory1.getName().compareTo(archiveCategory2.getName());
			}
		});
		
		archiveDTO.setCategoriesIds(sortedArchiveCategoriesIds);
		
		// Ranking
		Map<Integer, List<Long>> archiveRanking = new HashMap<Integer, List<Long>>();
		for (Integer rank : voteResultDTO.getRanking().keySet()) {
			// Conversion des ID des utilisateurs en ID des utilisateurs archive
			List<Long> usersIds = voteResultDTO.getRanking().get(rank);
			archiveRanking.put(rank, usersIdsToArchiveUsersIds(usersIds, usersLinks));
		}
		archiveDTO.setRanking(archiveRanking);
		
		archiveWithAwardsAndReportDTO.setArchive(archiveDTO);
		
		// ===== Récompenses =====
		
		List<Nomination> nominations = getPersistenceService().getNominations();
		List<ArchiveAwardDTO> archiveAwardsDTO = new ArrayList<ArchiveAwardDTO>();
		
		for (Long categoryId : voteResultDTO.getWinnersByCategory().keySet()) {
			Long archiveCategoryId = categoriesLinksDTO.getLinks().get(categoryId);
			List<Long> winners = voteResultDTO.getWinnersByCategory().get(categoryId);
			List<Long> archiveUsersIds = null;
			String reason = null;
			
			if (winners.size() == 1) {
				// Un seul vainqueur
				archiveUsersIds = usersIdsToArchiveUsersIds(winners, usersLinks);
				reason = getReason(winners, voteResultDTO.getVotesByCategory().get(categoryId), nominations);
			} else if (winners.size() > 1) {
				// Plusieurs vainqueurs : y a-t-il une décision du président ?
				Long decision = voteResultDTO.getDecisionsByCategory().get(categoryId);
				if (decision == null || decision == -2L) {
					// Pas de décision, ou alors tous : dans les deux cas, on les prend tous
					archiveUsersIds = usersIdsToArchiveUsersIds(winners, usersLinks);
					reason = getReason(winners, voteResultDTO.getVotesByCategory().get(categoryId), nominations);
				} else {
					// Un seul vainqueur
					List<Long> oneWinner = Arrays.asList(decision);
					archiveUsersIds = usersIdsToArchiveUsersIds(oneWinner, usersLinks);
					reason = getReason(oneWinner, voteResultDTO.getVotesByCategory().get(categoryId), nominations);
				}
			}			
			ArchiveAwardDTO archiveAwardDTO = new ArchiveAwardDTO(null, categoriesLinksDTO.getYear(), archiveCategoryId,  (Long[]) archiveUsersIds.toArray(new Long[archiveUsersIds.size()]), reason);
			archiveAwardsDTO.add(archiveAwardDTO);
		}
		
		// Lose award
		List<Long> losers = archiveRanking.get(1);
		archiveAwardsDTO.add(new ArchiveAwardDTO(null, categoriesLinksDTO.getYear(), null, (Long[]) losers.toArray(new Long[losers.size()]), null));
		
		archiveWithAwardsAndReportDTO.setArchiveAwards(archiveAwardsDTO);
		
		// ===== Rapport =====
		
		ArchiveReportDTO archiveReportDTO = new ArchiveReportDTO(null, categoriesLinksDTO.getYear(), Converter.stringToBlob(""));
		archiveWithAwardsAndReportDTO.setArchiveReport(archiveReportDTO);
		
		createArchiveWithAwardsAndReport(archiveWithAwardsAndReportDTO);
	}
	
	/**
	 * On cherche la raison pour laquelle, dans une catégorie données, les vainqueurs ont gagné.
	 * Il s'agit donc de rechercher les nominations qui les concerne et pour lesquelles il y a eu des votes, puis regarder quelle nomination est dominante.
	 * @param winners
	 * @param votesDTO
	 * @param nominations
	 * @return
	 */
	protected String getReason(final List<Long> winners, final List<VoteDTO> votesDTO, final List<Nomination> nominations) {
		List<Long> nominationsIds = new ArrayList<Long>();
		
		// On recherche toutes les nominations liées aux votes, concernant les vainqueurs (utilisateurs en paramètre)
		for (VoteDTO voteDTO : votesDTO) {
			if (voteDTO.getNominationId() != null) {
				// On récupère la nomination concernée pour avoir les utilisateurs
				Nomination nomination = Util.getObjectById(nominations, voteDTO.getNominationId());
				
				// Les nominés doivent contenir tous les vainqueurs
				boolean containsAll = true;
				for (Long userId : winners) {
					if (!Util.contains(nomination.getUsersIds(), userId)) {
						containsAll = false;
						break;
					}
				}
				if (containsAll) {
					nominationsIds.add(nomination.getId());
				}
			}
		}
		
		// S'il y a plusieurs nominations qui ressortent, on prend la première
		Long nominationId = Util.getMostRecurrentElements(nominationsIds, null).get(0);
		return Util.getObjectById(nominations, nominationId).getReason();
	}
	
	protected List<Long> usersIdsToArchiveUsersIds(final List<Long> usersIds, final Map<Long, Long> links) {
		List<Long> archiveUsersIds = new ArrayList<Long>();
		for (Long userId : usersIds) {
			archiveUsersIds.add(links.get(userId));
		}
		return archiveUsersIds;
	}
	
	protected ArchiveCategory findArchiveCategory(final Category category, final List<ArchiveCategory> archiveCategories) {
		for (ArchiveCategory archiveCategory : archiveCategories) {
			int distance = LevenshteinDistance.getDefaultInstance().apply(category.getName(), archiveCategory.getName());
			if (distance <= 2) {
				return archiveCategory;
			}
		}
		return null;
	}
	
	
	/**
	 * Supprime une archive.
	 * DELETE http://localhost:8888/api/archives/4822457999425536
	 * @param idArchive
	 */
	@DELETE
	@Path("/{idArchive}")
	public void deleteArchive(@PathParam("idArchive") final Long idArchive) {
		getPersistenceService().deleteArchive(idArchive);
	}
	
	/**
	 * Crée une archive avec ses récompenses.
	 * POST http://localhost:8888/api/archives/awards
	 * @param archiveWithAwardsDTO
	 */
	@POST
	@Path("/awards")
	public void createArchiveWithAwardsAndReport(final ArchiveWithAwardsAndReportDTO archiveWithAwardsDTO) {
		createArchive(archiveWithAwardsDTO.getArchive());
		getArchiveAwardApi().createArchiveAwards(archiveWithAwardsDTO.getArchiveAwards());
		getArchiveReportApi().createArchiveReport(archiveWithAwardsDTO.getArchiveReport());
	}
	
	/**
	 * Met à jour une archive avec ses récompenses.
	 * PUT http://localhost:8888/api/archives/awards/4822457999425536
	 * @param archiveId
	 * @param archiveWithAwardsDTO
	 */
	@PUT
	@Path("/awards/{archiveId}")
	public void updateArchiveWithAwardsAndReport(@PathParam("archiveId") final Long archiveId, final ArchiveWithAwardsAndReportDTO archiveWithAwardsDTO) {
		Archive archive = Converter.fromDTO(archiveWithAwardsDTO.getArchive());
		getPersistenceService().updateArchive(archive);
		
		// Suppression des récompenses, rangs et rapport précédents
		getPersistenceService().deleteArchiveAwardsOfYear(archive.getYear());
		getPersistenceService().deleteArchiveRanksOfYear(archive.getYear());
		getPersistenceService().deleteArchiveReportOfYear(archive.getYear());
		
		// Ajout des récompenses, rangs et rapport
		getArchiveAwardApi().createArchiveAwards(archiveWithAwardsDTO.getArchiveAwards());
		List<ArchiveRank> archiveRanks = Converter.fromDTO(archiveWithAwardsDTO.getArchive().getRanking(), archive.getYear());
		if (archiveRanks != null) {
			for (ArchiveRank archiveRank: archiveRanks) {
				getPersistenceService().addArchiveRank(archiveRank);
			}
		}
		getArchiveReportApi().createArchiveReport(archiveWithAwardsDTO.getArchiveReport());
	}
		
	protected ArchiveUserApi getArchiveUserApi() {
		if (archiveUserApi == null) {
			archiveUserApi = resourceContext.getResource(ArchiveUserApi.class);
		}
		return archiveUserApi;
	}
	
	protected ArchiveCategoryApi getArchiveCategoryApi() {
		if (archiveCategoryApi == null) {
			archiveCategoryApi = resourceContext.getResource(ArchiveCategoryApi.class);
		}
		return archiveCategoryApi;
	}
	
	protected ArchiveAwardApi getArchiveAwardApi() {
		if (archiveAwardApi == null) {
			archiveAwardApi = resourceContext.getResource(ArchiveAwardApi.class);
		}
		return archiveAwardApi;
	}
	
	protected ArchiveReportApi getArchiveReportApi() {
		if (archiveReportApi == null) {
			archiveReportApi = resourceContext.getResource(ArchiveReportApi.class);
		}
		return archiveReportApi;
	}

	protected VoteApi getVoteApi() {
		if (voteApi == null) {
			voteApi = resourceContext.getResource(VoteApi.class);
		}
		return voteApi;
	}

}
