package fr.loseawards.backup.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.backup.dto.BackupArchiveAwardDTO;
import fr.loseawards.backup.dto.BackupArchiveDTO;
import fr.loseawards.backup.dto.BackupArchiveWithAwardsDTO;
import fr.loseawards.backup.dto.BackupCategoryWithNominationsDTO;
import fr.loseawards.backup.dto.BackupCommentDTO;
import fr.loseawards.backup.dto.BackupNominationDTO;
import fr.loseawards.model.Archive;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.model.Avatar;
import fr.loseawards.model.Category;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.nomination.api.NominationApi;
import fr.loseawards.nomination.dto.NominationBundleDTO;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.user.api.UserApi;
import fr.loseawards.user.dto.UserDTO;
import fr.loseawards.util.Util;

@Path("/backup")
@Produces(MediaType.APPLICATION_JSON)
public class BackupApi extends AbstractServiceApi {
	protected UserApi userApi = null;
	protected NominationApi nominationApi = null;
	
	/**
	 * Permet de générer un backup de l'archive (sans les ID) pour pouvoir faire un restore.
	 * GET http://localhost:8888/api/backup/archives
	 * @param year
	 * @return
	 */
	@GET
	@Path("/archives")
	public BackupArchiveWithAwardsDTO backupArchiveWithAwards(@QueryParam("year") final Integer year) {
		BackupArchiveWithAwardsDTO backup = new BackupArchiveWithAwardsDTO();
		
		List<ArchiveCategory> categories = getPersistenceService().getArchiveCategories();
		List<ArchiveUser> users = getPersistenceService().getArchiveUsers();
		
		// Partie archive
		Archive archive = getPersistenceService().getArchiveByYear(year);
		if (archive == null) {
			return backup;
		}		
		
		BackupArchiveDTO backupArchiveDTO = new BackupArchiveDTO();
		backupArchiveDTO.setYear(year);
		
		List<String> categoriesNames = new ArrayList<String>();
		for (Long categoryId: archive.getCategoriesIds()) {
			ArchiveCategory category = Util.getObjectById(categories, categoryId);
			categoriesNames.add(category.getName());
		}
		backupArchiveDTO.setCategoriesNames(categoriesNames);
		
		// Partie ranking
		Map<Integer, List<String>> ranking = new HashMap<Integer, List<String>>();
		List<ArchiveRank> archiveRanks = getPersistenceService().getArchiveRanksByYear(year);
		for (ArchiveRank archiveRank : archiveRanks) {
			List<String> usersNames = new ArrayList<String>();
			for (Long userId : archiveRank.getUsersIds()) {
				usersNames.add(Util.getObjectById(users, userId).getDisplayName());
			}
			ranking.put(archiveRank.getPosition(), usersNames);
		}
		backupArchiveDTO.setRanking(ranking);
		
		backup.setArchive(backupArchiveDTO);
		
		// Partie awards
		List<ArchiveAward> archiveAwards = getPersistenceService().getArchiveAwardsByYear(year);
		
		List<BackupArchiveAwardDTO> backupArchiveAwards = new ArrayList<BackupArchiveAwardDTO>();
		for (ArchiveAward archiveAward : archiveAwards) {
			BackupArchiveAwardDTO backupArchiveAwardDTO = new BackupArchiveAwardDTO();
			if (archiveAward.getCategoryId() == null) {
				// Lose awards
				backupArchiveAwardDTO.setCategoryName("");
			} else {
				backupArchiveAwardDTO.setCategoryName(Util.getObjectById(categories, archiveAward.getCategoryId()).getName());
			}
			backupArchiveAwardDTO.setReason(archiveAward.getReason());
			backupArchiveAwardDTO.setYear(archiveAward.getYear());
			
			// Utilisateurs
			List<String> usersNames = new ArrayList<String>();
			for (Long userId : archiveAward.getUsersIds()) {
				usersNames.add(Util.getObjectById(users, userId).getDisplayName());
			}
			backupArchiveAwardDTO.setUsersNames(usersNames);
			
			backupArchiveAwards.add(backupArchiveAwardDTO);
		}
		
		backup.setArchiveAwards(backupArchiveAwards);
		return backup;
	}
	
	/**
	 * Permet de générer un backup des utilisateurs (sans les ID).
	 * GET http://localhost:8888/api/backup/users
	 * @return
	 */
	@GET
	@Path("/users")
	public List<UserDTO> backupUsersWithAvatars() {
		// Récupération des utilisateurs
		List<UserDTO> usersDTO = getUserApi().getUsers();
		
		// Récupération des avatars et mise à null des ID
		for (UserDTO userDTO : usersDTO) {
			userDTO.setId(null);
			if (userDTO.getAvatarId() != null) {
				Avatar avatar = getPersistenceService().getAvatar(userDTO.getAvatarId());
				userDTO.setAvatar(avatar.getImage().getBytes());
				userDTO.setAvatarId(null);
			}			
		}
		
		return usersDTO;
	}
	
	/**
	 * Permet de générer un backup des catégories avec les nominations (sans les ID).
	 * GET http://localhost:8888/api/backup/categories
	 * @return
	 */
	@GET
	@Path("/categories")
	public List<BackupCategoryWithNominationsDTO> backupCategoriesWithNominations() {
		 List<BackupCategoryWithNominationsDTO> backup = new ArrayList<BackupCategoryWithNominationsDTO>();
		 
		 List<Category> categories = getPersistenceService().getCategories();
		 List<User> users = getPersistenceService().getUsers();
		 
		 NominationBundleDTO bundle = getNominationApi().getNominationBundle();
		 for (Long categoryId : bundle.getNominations().keySet()) {
			 BackupCategoryWithNominationsDTO backupCategory = new BackupCategoryWithNominationsDTO();
			 backupCategory.setName(Util.getObjectById(categories, categoryId).getName());
			 
			 // Nominations
			 List<BackupNominationDTO> backupNominations = new ArrayList<BackupNominationDTO>();
			 List<NominationDTO> nominationsDTO = bundle.getNominations().get(categoryId);
			 
			 for (NominationDTO nominationDTO : nominationsDTO) {
				 BackupNominationDTO backupNomination = new BackupNominationDTO();
				 backupNomination.setDate(nominationDTO.getDate());
				 backupNomination.setReason(nominationDTO.getReason());
				 
				// Utilisateurs
				List<String> usersNames = new ArrayList<String>();
				for (Long userId : nominationDTO.getUsersIds()) {
					usersNames.add(Util.getObjectById(users, userId).getDisplayName());
				}
				backupNomination.setUsersNames(usersNames);
				backupNominations.add(backupNomination);
			 }			 
			 backupCategory.setNominations(backupNominations);
			 backup.add(backupCategory);
		 }
		
		return backup;
	}
	
	/**
	 * Permet de générer un backup des commentaires (sans les ID).
	 * GET http://localhost:8888/api/backup/comments
	 * @return
	 */
	@GET
	@Path("/comments")
	public List<BackupCommentDTO> backupComments() {
		List<BackupCommentDTO> backup = new ArrayList<BackupCommentDTO>();
		
		List<Category> categories = getPersistenceService().getCategories();
		List<User> users = getPersistenceService().getUsers();
		
		List<Comment> comments = getPersistenceService().getComments();
		for (Comment comment : comments) {
			BackupCommentDTO backupComment = new BackupCommentDTO();
			backupComment.setAuthorName(Util.getObjectById(users, comment.getAuthorId()).getDisplayName());
			backupComment.setContent(comment.getContent());
			backupComment.setDate(comment.getDate());
			
			Nomination nomination = getPersistenceService().getNomination(comment.getNominationId());
			backupComment.setNominationCategoryName(Util.getObjectById(categories, nomination.getCategoryId()).getName());
			backupComment.setNominationReason(nomination.getReason());
			
			backup.add(backupComment);
		}
		
		return backup;
	}
	
	protected UserApi getUserApi() {
		if (userApi == null) {
			userApi = resourceContext.getResource(UserApi.class);
		}
		return userApi;
	}
	
	protected NominationApi getNominationApi() {
		if (nominationApi == null) {
			nominationApi = resourceContext.getResource(NominationApi.class);
		}
		return nominationApi;
	}
}
