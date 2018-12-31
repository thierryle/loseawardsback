package fr.loseawards.archiveaward.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.archiveaward.dto.ArchiveAwardsAndReportDTO;
import fr.loseawards.archiveaward.dto.ArchiveAwardDTO;
import fr.loseawards.archiveaward.dto.GraphicsDatumDTO;
import fr.loseawards.archiveaward.dto.ProgressionGraphicsDataDTO;
import fr.loseawards.archiveaward.dto.StatCategoryDTO;
import fr.loseawards.archiveaward.dto.StatUserDTO;
import fr.loseawards.archiveaward.dto.UserAndRecordDTO;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveReport;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;

@Path("/archiveawards")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveAwardApi extends AbstractServiceApi {
	/**
	 * Retourne une récompense.
	 * GET http://localhost:8888/api/archiveawards/5387606976102400
	 * @param archiveAwardId
	 * @return
	 */
	@GET
	@Path("/{archiveAwardId}")
	public ArchiveAwardDTO getArchiveAward(@PathParam("archiveAwardId") final Long archiveAwardId) {
		ArchiveAward archiveAward = getPersistenceService().getArchiveAward(archiveAwardId);
		if (archiveAward == null) {
			return null;
		}
		return Converter.toDTO(archiveAward);
	}
	
	/**
	 * Retourne les récompenses par année.
	 * GET http://localhost:8888/api/archiveawards?year=2009
	 * @param year
	 * @return
	 */
	@GET
	public List<ArchiveAwardDTO> getArchiveAwards(@QueryParam("year") final Integer year) {
		List<ArchiveAward> archiveAwards;
		if (year == null) {
			archiveAwards = getPersistenceService().getArchiveAwards();
		} else {
			archiveAwards = getPersistenceService().getArchiveAwardsByYear(year);
		}
		
		// Conversion en DTO
		return Converter.toArchiveAwardsDTO(archiveAwards);
	}
	
	/**
	 * Retourne les récompenses et le rapport pour une année.
	 * GET http://localhost:8888/api/archiveawards/report?year=2009
	 * @param year
	 * @return
	 */
	@GET
	@Path("/report")
	public ArchiveAwardsAndReportDTO getArchiveAwardsAndReport(@QueryParam("year") final Integer year) {
		ArchiveAwardsAndReportDTO bundle = new ArchiveAwardsAndReportDTO();
		
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwardsByYear(year);
		bundle.setArchiveAwards(Converter.toArchiveAwardsDTO(archiveAwards));
		
		ArchiveReport archiveReport = getPersistenceService().getArchiveReportOfYear(year);
		if (archiveReport != null) {
			bundle.setArchiveReport(Converter.toDTO(archiveReport));
		}		

		return bundle;
	}
	
	/**
	 * Supprime une récompense.
	 * DELETE http://localhost:8888/api/archiveawards/5387606976102400
	 * @param archiveAwardId
	 * @return
	 */
	@DELETE
	@Path("/{archiveAwardId}")
	public void deleteArchiveAward(@PathParam("archiveAwardId") final Long archiveAwardId) {
		getPersistenceService().deleteArchiveAward(archiveAwardId);
	}
	
	/**
	 * Supprime toutes les récompenses.
	 * DELETE http://localhost:8888/api/archiveawards
	 * @param year
	 * @return
	 */
	@DELETE
	public void deleteArchiveAwards() {
		getPersistenceService().deleteArchiveAwards();
	}
	
	/**
	 * Crée une liste de récompenses (pour une même année) en supprimant les récompenses précédentes de la même année.
	 * POST http://localhost:8888/api/archiveawards/bulk
	 * @param archiveAwards
	 */
	@POST
	@Path("/bulk")
	public void createArchiveAwards(final List<ArchiveAwardDTO> archiveAwards) {
		if (archiveAwards != null && !archiveAwards.isEmpty()) {
			// Suppression des récompenses précédentes de cette année
			getPersistenceService().deleteArchiveAwardsOfYear(archiveAwards.get(0).getYear());
			
			for (ArchiveAwardDTO archiveAwardDTO : archiveAwards) {
				getPersistenceService().addArchiveAward(Converter.fromDTO(archiveAwardDTO));
			}
		}
	}
	
	/**
	 * Récupère les stats pour une catégorie.
	 * GET http://localhost:8888/api/archiveawards/statcategory?categoryId=5704266324901888
	 * @param categoryId
	 * @return
	 */
	@GET
	@Path("/statcategory")
	public StatCategoryDTO getStatCategory(@QueryParam("categoryId") final Long categoryId) {
		StatCategoryDTO statCategoryDTO = new StatCategoryDTO();
		List<Integer> appearingYears = new ArrayList<Integer>();
		
		int total = 0;
		final Map<Long, List<Integer>> awardsByUser = new HashMap<Long, List<Integer>>(); // Années de victoire par utilisateur
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwardsByCategory(categoryId);
		for (ArchiveAward archiveAward : archiveAwards) {
			if (archiveAward.getUsersIds() != null) {
				for (Long userId: archiveAward.getUsersIds()) {
					List<Integer> years = awardsByUser.get(userId);
					if (years == null) {
						years = new ArrayList<Integer>();
						awardsByUser.put(userId, years);
					}
					years.add(archiveAward.getYear());
					total++;
				}
				if (!appearingYears.contains(archiveAward.getYear())) {
					appearingYears.add(archiveAward.getYear());
				}				
			}
		}
		
		// Ordonnancement des années
		Comparator<Integer> comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}				
		};
		
		Collections.sort(appearingYears, comparator);
		statCategoryDTO.setAppearingYears(appearingYears);
		
		for (Long userId: awardsByUser.keySet()) {
			List<Integer> years = awardsByUser.get(userId);
			Collections.sort(years, comparator);
		}
		
		// Ordonnancement des utilisateurs
		List<Long> sortedUsers = new ArrayList<Long>(awardsByUser.keySet());
		Collections.sort(sortedUsers, new Comparator<Long>() {
			@Override
			public int compare(Long u1, Long u2) {
				int nb1 = awardsByUser.get(u2).size();
				int nb2 = awardsByUser.get(u1).size();
				return (nb1 < nb2) ? -1 : ((nb1 == nb2) ? 0 : 1);
			}
		});
		Map<Long, List<Integer>> sortedAwardsByUser = new LinkedHashMap<Long, List<Integer>>();
		for (Long userId : sortedUsers) {
			sortedAwardsByUser.put(userId, awardsByUser.get(userId));
		}
		statCategoryDTO.setAwardsByUser(sortedAwardsByUser);
		
		// Statistiques
		List<ArchiveUser> users = getPersistenceService().getArchiveUsers();
		List<GraphicsDatumDTO> graphicsData = new ArrayList<GraphicsDatumDTO>();
		for (Long userId: sortedAwardsByUser.keySet()) {
			graphicsData.add(new GraphicsDatumDTO(Util.getObjectById(users, userId).getDisplayName(), ((double) awardsByUser.get(userId).size()) / ((double) total) * 100D));
		}
		statCategoryDTO.setGraphicsData(graphicsData);
		
		return statCategoryDTO;
	}
	
	/**
	 * Récupère les records par catégorie.
	 * GET http://localhost:8888/api/archiveawards/statrecords
	 * @return
	 */
	@GET
	@Path("/statrecords")
	public Map<Long, List<UserAndRecordDTO>> getStatRecords() {
		// Pour chaque catégorie, nombre de victoires par utilisateur
		Map<Long, Map<Long, Integer>> recordsByCategory = new HashMap<Long, Map<Long,Integer>>();
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwards();
		for (ArchiveAward archiveAward : archiveAwards) {
			Long categoryId = archiveAward.getCategoryId();
			if (categoryId == null) {
				categoryId = -1L;
			}
			Map<Long, Integer> recordsOfOneCategory = recordsByCategory.get(categoryId);
			if (recordsOfOneCategory == null) {
				recordsOfOneCategory = new HashMap<Long, Integer>();
				recordsByCategory.put(categoryId, recordsOfOneCategory);
			}
			if (archiveAward.getUsersIds() != null) {
				for (Long userId: archiveAward.getUsersIds()) {
					Integer nbAwardsOfOneUser = recordsOfOneCategory.get(userId);
					if (nbAwardsOfOneUser == null) {
						recordsOfOneCategory.put(userId, new Integer(1));
					} else {
						recordsOfOneCategory.put(userId, new Integer(nbAwardsOfOneUser.intValue() + 1));
					}
				}
			}			
		}
		
		// Records pour chaque catégorie
		Map<Long, List<UserAndRecordDTO>> usersRecordsByCategory = new HashMap<Long, List<UserAndRecordDTO>>();
		for (Long categoryId : recordsByCategory.keySet()) {
			Map<Long, Integer> recordsOfOneCategory = recordsByCategory.get(categoryId);
			
			// On cherche le record
			int record = 0;
			for (Long userId : recordsOfOneCategory.keySet()) {
				Integer nbAwards = recordsOfOneCategory.get(userId);
				if (nbAwards.intValue() > record) {
					record = nbAwards.intValue();
				}
			}
			
			// On cherche les utilisateurs qui ont ce record
			for (Long userId : recordsOfOneCategory.keySet()) {
				Integer nbAwards = recordsOfOneCategory.get(userId);
				if (nbAwards.intValue() == record) {
					List<UserAndRecordDTO> usersAndRecords = usersRecordsByCategory.get(categoryId);
					if (usersAndRecords == null) {
						usersAndRecords = new ArrayList<UserAndRecordDTO>();
						usersRecordsByCategory.put(categoryId, usersAndRecords);
					}
					usersAndRecords.add(new UserAndRecordDTO(userId, nbAwards));
				}
			}
		}
		
		// Ordonnancement des utilisateurs
		final List<ArchiveCategory> categories = getPersistenceService().getArchiveCategories();
		List<Long> sortedCategories = new ArrayList<Long>(usersRecordsByCategory.keySet());
		Collections.sort(sortedCategories, new Comparator<Long>() {
			@Override
			public int compare(Long id1, Long id2) {
				if (id1.equals(-1L)) {
					return -1;
				}
				if (id2.equals(-1L)) {
					return 1;
				}
				ArchiveCategory category1 = Util.getObjectById(categories, id1);
				ArchiveCategory category2 = Util.getObjectById(categories, id2);
				return (category1.getName().compareTo(category2.getName()));
			}
		});
		Map<Long, List<UserAndRecordDTO>> sortedUsersRecordsByCategory = new LinkedHashMap<Long, List<UserAndRecordDTO>>();
		for (Long categoryId : sortedCategories) {
			sortedUsersRecordsByCategory.put(categoryId, usersRecordsByCategory.get(categoryId));
		}
		
		return sortedUsersRecordsByCategory;
	}
	
	/**
	 * Récupère les stats pour un utilisateur
	 * GET http://localhost:8888/api/archiveawards/statuser?userId=6619059999211520
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/statuser")
	public StatUserDTO getStatUser(@QueryParam("userId") final Long userId) {
		StatUserDTO statUserDTO = new StatUserDTO();
		
		int total = 0;
		final Map<Long, List<Integer>> awardsByCategory = new HashMap<Long, List<Integer>>(); // Listes des années de récompense par catégorie
		final Map<Integer, Integer> nbAwardsByYear = new HashMap<Integer, Integer>(); // Nombre de récompenses par année
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwardsByUser(userId);
		for (ArchiveAward archiveAward : archiveAwards) {
			Long categoryId = archiveAward.getCategoryId();
			if (categoryId == null) {
				categoryId = -1L;
			} else {
				total++;
			}
			// Liste des années pour la catégorie courante
			List<Integer> years = awardsByCategory.get(categoryId);
			if (years == null) {
				years = new ArrayList<Integer>();
				awardsByCategory.put(categoryId, years);
			}
			years.add(archiveAward.getYear());
			
			// Nombre de récompenses pour l'année courante
			if (categoryId != -1L) {
				Integer nbAwards = nbAwardsByYear.get(archiveAward.getYear());
				if (nbAwards == null) {
					nbAwardsByYear.put(archiveAward.getYear(), new Integer(1));
				} else {
					nbAwardsByYear.put(archiveAward.getYear(), new Integer(nbAwards.intValue() + 1));
				}
			}			
		}
		
		// Ordonnancement des années
		for (Long categoryId : awardsByCategory.keySet()) {
			List<Integer> years = awardsByCategory.get(categoryId);
			Collections.sort(years, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}				
			});
		}
		
		// Ordonnancement des catégories
		List<Long> sortedCategories = new ArrayList<Long>(awardsByCategory.keySet());
		Collections.sort(sortedCategories, new Comparator<Long>() {
			@Override
			public int compare(Long u1, Long u2) {
				int nb1 = awardsByCategory.get(u2).size();
				int nb2 = awardsByCategory.get(u1).size();
				return (nb1 < nb2) ? -1 : ((nb1 == nb2) ? 0 : 1);
			}
		});
		Map<Long, List<Integer>> sortedAwardsByCategory = new LinkedHashMap<Long, List<Integer>>();
		for (Long categoryId : sortedCategories) {
			sortedAwardsByCategory.put(categoryId, awardsByCategory.get(categoryId));
		}
		statUserDTO.setAwardsByCategory(sortedAwardsByCategory);		
		
		// Statistiques
		List<ArchiveCategory> categories = getPersistenceService().getArchiveCategories();
		List<GraphicsDatumDTO> graphicsData = new ArrayList<GraphicsDatumDTO>();
		for (Long categoryId: sortedAwardsByCategory.keySet()) {
			if (categoryId != -1) {
				graphicsData.add(new GraphicsDatumDTO(Util.getObjectById(categories, categoryId).getName(), ((double) awardsByCategory.get(categoryId).size()) / ((double) total) * 100D));
			}			
		}
		statUserDTO.setGraphicsData(graphicsData);
		
		// Statistiques de progression
		ArchiveUser archiveUser = getPersistenceService().getArchiveUser(userId);
		ProgressionGraphicsDataDTO progression = new ProgressionGraphicsDataDTO();
		List<Integer> nbAwards = new ArrayList<Integer>();
		List<Integer> years = new ArrayList<Integer>();
		List<Integer> ranks = new ArrayList<Integer>();
		List<ArchiveRank> archiveRanks = getPersistenceService().getArchiveRanksByUser(userId);
		
		for (ArchiveRank archiveRank : archiveRanks) {
			Integer year = archiveRank.getYear();
			if ((archiveUser.getFirstYear() == null || year.intValue() >= archiveUser.getFirstYear()) && (archiveUser.getLastYear() == null || year.intValue() <= archiveUser.getLastYear().intValue())) {
				years.add(year);
				
				// Nombre de récompenses
				Integer nbAwardsOfCurrentYear = nbAwardsByYear.get(year);
				if (nbAwardsOfCurrentYear == null) {
					nbAwards.add(new Integer(0));
				} else {
					nbAwards.add(nbAwardsOfCurrentYear);
				}
				
				// Classement
				ranks.add(getRankByYear(year, archiveRanks));
			}
		}
		progression.setYears(years);
		progression.setNbAwards(nbAwards);
		progression.setRanks(ranks);
		statUserDTO.setProgressionGraphicsData(progression);		
		
		return statUserDTO;
	}
	
	/**
	 * Retourne le classement pour une année.
	 * @param year
	 * @param archiveRanks
	 * @return
	 */
	protected Integer getRankByYear(final Integer year, final List<ArchiveRank> archiveRanks) {
		for (ArchiveRank archiveRank : archiveRanks) {
			if (archiveRank.getYear().equals(year)) {
				return archiveRank.getPosition();
			}
		}
		return null;
	}
}
