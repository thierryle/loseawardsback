package fr.loseawards.backup.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.archive.dto.ArchiveDTO;
import fr.loseawards.archivecategory.dto.ArchiveCategoryDTO;
import fr.loseawards.archivereport.dto.ArchiveReportDTO;
import fr.loseawards.archiveuser.dto.ArchiveUserDTO;
import fr.loseawards.backup.dto.BackupArchiveAwardDTO;
import fr.loseawards.backup.dto.BackupArchiveWithAwardsDTO;
import fr.loseawards.backup.dto.BackupCategoryWithNominationsDTO;
import fr.loseawards.backup.dto.BackupCommentDTO;
import fr.loseawards.backup.dto.BackupNominationDTO;
import fr.loseawards.backup.dto.RestoreURLDTO;
import fr.loseawards.category.api.CategoryApi;
import fr.loseawards.comment.api.CommentApi;
import fr.loseawards.model.Archive;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveReport;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.model.Category;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.user.api.UserApi;
import fr.loseawards.user.dto.UserDTO;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;

@Path("/restore")
@Produces(MediaType.APPLICATION_JSON)
public class RestoreApi extends AbstractServiceApi {
	protected CategoryApi categoryApi = null;
	protected CommentApi commentApi = null;
	protected UserApi userApi = null;
	protected Client client = null;
	
	/**
	 * Restauration de l'archive d'une année.
	 * POST http://localhost:8888/api/restore/archives?year=2009
	 * @param year Année dans laquelle recopier l'archive (optionnel)
	 * @param backup
	 */
	@POST
	@Path("/archives")
	public void restoreArchiveWithAwards(@QueryParam("year") final Integer pYear, final BackupArchiveWithAwardsDTO backup) {
		Integer year;
		if (pYear == null) {
			year = backup.getArchive().getYear();
		} else {
			year = pYear;
		}
		
		// Suppression de l'archive précédente
		getPersistenceService().deleteArchiveOfYear(year);
		
		List<ArchiveCategory> categories = getPersistenceService().getArchiveCategories();
		List<ArchiveUser> users = getPersistenceService().getArchiveUsers();
		
		// Partie archive
		Archive archive = new Archive();
		archive.setYear(year);
		
		List<Long> categoriesIds = new ArrayList<Long>();
		for (String categoryName : backup.getArchive().getCategoriesNames()) {
			ArchiveCategory category = Util.getObjectByProperty(categories, "name", categoryName);
			if (category == null) {
				throw new IllegalArgumentException("Catégorie " + categoryName + " inexistante");
			}
			categoriesIds.add(category.getId());
		}
		archive.setCategoriesIds((Long[]) categoriesIds.toArray(new Long[categoriesIds.size()]));
		
		// Partie ranking
		Map<Integer, List<String>> ranking = backup.getArchive().getRanking();
		if (ranking != null) {
			for (Integer rank : ranking.keySet()) {
				// Utilisateurs
				List<Long> usersIds = new ArrayList<Long>();
				for (String userName : ranking.get(rank)) {
					ArchiveUser user =  Util.getObjectByProperty(users, "displayName", userName);
					if (user == null) {
						throw new IllegalArgumentException("Utilisateur " + userName + " inexistant");
					}
					usersIds.add(user.getId());
				}
				ArchiveRank archiveRank = new ArchiveRank(null, year, rank, usersIds);
				getPersistenceService().addArchiveRank(archiveRank);;
			}
		}
		
		getPersistenceService().addArchive(archive);
		
		// Partie awards
		for (BackupArchiveAwardDTO backupArchiveAwardDTO : backup.getArchiveAwards()) {
			ArchiveAward archiveAward = new ArchiveAward();
			
			if (backupArchiveAwardDTO.getCategoryName() == null || backupArchiveAwardDTO.getCategoryName().isEmpty()) {
				// Lose award
				archiveAward.setCategoryId(null);
			} else {
				ArchiveCategory category = Util.getObjectByProperty(categories, "name", backupArchiveAwardDTO.getCategoryName());
				archiveAward.setCategoryId(category.getId());
			}			
			archiveAward.setReason(backupArchiveAwardDTO.getReason());
			archiveAward.setYear(year);
			
			// Utilisateurs
			List<Long> usersIds = new ArrayList<Long>();
			for (String userName : backupArchiveAwardDTO.getUsersNames()) {
				ArchiveUser user =  Util.getObjectByProperty(users, "displayName", userName);
				if (user == null) {
					throw new IllegalArgumentException("Utilisateur " + userName + " inexistant");
				}
				usersIds.add(user.getId());
			}
			archiveAward.setUsersIds((Long[]) usersIds.toArray(new Long[usersIds.size()]));
			
			getPersistenceService().addArchiveAward(archiveAward);
		}
	}
	
	/**
	 * Restauration des utilisateurs d'archive.
	 * POST http://localhost:8888/api/restore/archiveusers
	 * @param archiveUsers
	 */
	@POST
	@Path("/archiveusers")
	public void restoreArchiveUsers(final List<ArchiveUserDTO> archiveUsers) {
		getPersistenceService().deleteArchiveUsers();
		
		for (ArchiveUserDTO archiveUserDTO : archiveUsers) {
			getPersistenceService().addArchiveUser(new ArchiveUser(null, archiveUserDTO.getFirstName(), archiveUserDTO.getLastName(), archiveUserDTO.getFirstYear(), archiveUserDTO.getLastYear()));
		}
	}
	
	/**
	 * Restauration des catégories d'archive.
	 * POST http://localhost:8888/api/restore/archivecategories
	 * @param archiveCategories
	 */
	@POST
	@Path("/archivecategories")
	public void restoreArchiveCategories(final List<ArchiveCategoryDTO> archiveCategories) {
		getPersistenceService().deleteArchiveCategories();
		
		for (ArchiveCategoryDTO archiveCategoryDTO : archiveCategories) {
			getPersistenceService().addArchiveCategory(new ArchiveCategory(null, archiveCategoryDTO.getName()));
		}
	}
	
	/**
	 * Restauration des utilisateurs.
	 * POST http://localhost:8888/api/restore/users
	 * @param users
	 */
	@POST
	@Path("/users")
	public void restoreUsers(final List<UserDTO> users) {
		getPersistenceService().deleteUsers();
		
		for (UserDTO userDTO : users) {
			//getPersistenceService().addUser(new User(null, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), null));
			getUserApi().createUser(userDTO);
		}
	}
	
	/**
	 * Restauration des catégories.
	 * POST http://localhost:8888/api/restore/categories
	 */
	@POST
	@Path("/categories")
	public void restoreCategoriesWithNominations(final List<BackupCategoryWithNominationsDTO> backupCategories) {
		getCategoryApi().deleteCategories();
		
		List<User> users = getPersistenceService().getUsers();
		
		for (BackupCategoryWithNominationsDTO backupCategory : backupCategories) {
			Category category = new Category();
			category.setName(backupCategory.getName());
			getPersistenceService().addCategory(category);
			
			// Nominations
			for (BackupNominationDTO backupNomination : backupCategory.getNominations()) {
				Nomination nomination = new Nomination();
				nomination.setCategoryId(category.getId());
				nomination.setDate(backupNomination.getDate());
				nomination.setReason(backupNomination.getReason());
				
				// Utilisateurs
				List<Long> usersIds = new ArrayList<Long>();
				for (String userName : backupNomination.getUsersNames()) {
					User user =  Util.getObjectByProperty(users, "displayName", userName);
					if (user == null) {
						throw new IllegalArgumentException("Utilisateur " + userName + " inexistant");
					}
					usersIds.add(user.getId());
				}
				nomination.setUsersIds((Long[]) usersIds.toArray(new Long[usersIds.size()]));
				
				getPersistenceService().addNomination(nomination);
			}
		}
	}
	
	/**
	 * Restauration des commentaires.
	 * POST http://localhost:8888/api/restore/comments
	 * @param backupComments
	 */
	@POST
	@Path("/comments")
	public void restoreComments(final List<BackupCommentDTO> backupComments) {
		getCommentApi().deleteComments();
		
		List<User> users = getPersistenceService().getUsers();
		List<Nomination> nominations = getPersistenceService().getNominations();
		List<Category> categories = getPersistenceService().getCategories();
		
		for (BackupCommentDTO backupComment : backupComments) {
			Comment comment = new Comment();
			
			// Utilisateur
			User user =  Util.getObjectByProperty(users, "displayName", backupComment.getAuthorName());
			if (user == null) {
				throw new IllegalArgumentException("Utilisateur " + backupComment.getAuthorName() + " inexistant");
			}
			comment.setAuthorId(user.getId());
			
			// Contenu et date
			comment.setContent(backupComment.getContent());
			comment.setDate(backupComment.getDate());
			
			// Nomination
			Nomination nomination = findNominationByCategoryNameAndReason(backupComment.getNominationCategoryName(), backupComment.getNominationReason(), nominations, categories);
			comment.setNominationId(nomination.getId());
			
			getPersistenceService().addComment(comment);
		}
	}
	
	/**
	 * Restauration des rapports.
	 * POST http://localhost:8888/api/restore/archivereports
	 * @param archiveReports
	 */
	@POST
	@Path("/archivereports")
	public void restoreArchiveReports(final List<ArchiveReportDTO> archiveReports) {
		// Suppression des comptes-rendus précédents
		getPersistenceService().deleteArchiveReports();
		
		for (ArchiveReportDTO archiveReportDTO : archiveReports) {
			getPersistenceService().addArchiveReport(new ArchiveReport(null, archiveReportDTO.getYear(), Converter.stringToBlob(archiveReportDTO.getReport())));
		}
	}
	
	/**
	 * Restauration du site par l'URL d'un site miroir.
	 * POST http://localhost:8888/api/restore/url
	 * @param restoreURLDTO
	 */
	@POST
	@Path("/url")
	public void restoreByURL(final RestoreURLDTO restoreURLDTO) {
		// Utilisateurs
		if (Boolean.TRUE.equals(restoreURLDTO.getRestoreUsers())) {
			List<UserDTO> users = getResource(restoreURLDTO.getUrl() + "/api/backup/users", new GenericType<List<UserDTO>>(){});
			restoreUsers(users);
		}
		
		// Catégories et nominations
		if (Boolean.TRUE.equals(restoreURLDTO.getRestoreCategoriesAndNominations())) {
			List<BackupCategoryWithNominationsDTO> categoriesWithNominations = getResource(restoreURLDTO.getUrl() + "/api/backup/categories", new GenericType<List<BackupCategoryWithNominationsDTO>>(){});
			restoreCategoriesWithNominations(categoriesWithNominations);
			
			// Commentaires
			List<BackupCommentDTO> comments = getResource(restoreURLDTO.getUrl() + "/api/backup/comments", new GenericType<List<BackupCommentDTO>>(){});
			restoreComments(comments);
		}
		
		// Utilisateurs et catégories d'archive
		if (Boolean.TRUE.equals(restoreURLDTO.getRestoreUsersAndCategoriesArchive())) {
			// Utilisateurs
			List<ArchiveUserDTO> archiveUsers = getResource(restoreURLDTO.getUrl() + "/api/archiveusers", new GenericType<List<ArchiveUserDTO>>(){});
			restoreArchiveUsers(archiveUsers);
			
			// Catégories
			List<ArchiveCategoryDTO> archiveCategories = getResource(restoreURLDTO.getUrl() + "/api/archivecategories", new GenericType<List<ArchiveCategoryDTO>>(){});
			restoreArchiveCategories(archiveCategories);
		}
		
		// Archive
		if (Boolean.TRUE.equals(restoreURLDTO.getRestoreArchives())) {
			List<ArchiveDTO> archives = getResource(restoreURLDTO.getUrl() + "/api/archives", new GenericType<List<ArchiveDTO>>(){});
			
			// Parcours des années d'archive
			for (ArchiveDTO archive : archives) {
				Integer year = archive.getYear();
				BackupArchiveWithAwardsDTO backupArchive = getResource(restoreURLDTO.getUrl() + "/api/backup/archives?year=" + year, BackupArchiveWithAwardsDTO.class);
				restoreArchiveWithAwards(year, backupArchive);
			}
			
			// Rapports
			List<ArchiveReportDTO> archiveReports = getResource(restoreURLDTO.getUrl() + "/api/archivereports", new GenericType<List<ArchiveReportDTO>>(){});
			restoreArchiveReports(archiveReports);			
		}
	}
	
	/**
	 * Cherche une nomination par le nom de la catégorie et la raison.
	 * @param categoryName
	 * @param reason
	 * @param nominations
	 * @param categories
	 * @return
	 */
	protected Nomination findNominationByCategoryNameAndReason(final String categoryName, final String reason, final List<Nomination> nominations, final List<Category> categories) {
		for (Nomination nomination : nominations) {
			if (nomination.getReason().equals(reason)) {
				Category category = Util.getObjectById(categories, nomination.getCategoryId());
				if (category.getName().equals(categoryName)) {
					return nomination;
				}
			}			
		}
		return null;
	}
	
	protected Client getClient() {
		if (client == null) {
			ClientConfig clientConfig = new DefaultClientConfig();
	        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			client = Client.create();
		}
		return client;
	}
	
	protected <T> T getResource(final String url, final GenericType<T> genericType) {
		WebResource webResource = getClient().resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
		   throw new IllegalArgumentException("Erreur dans l'appel de l'URL " + url + " : " + response.getStatus());
		}
		
		return response.getEntity(genericType);
	}
	
	protected <T> T getResource(final String url, final Class<T> c) {
		WebResource webResource = getClient().resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
		   throw new IllegalArgumentException("Erreur dans l'appel de l'URL " + url + " : " + response.getStatus());
		}
		
		return response.getEntity(c);
	}
	
	protected CategoryApi getCategoryApi() {
		if (categoryApi == null) {
			categoryApi = resourceContext.getResource(CategoryApi.class);
		}
		return categoryApi;
	}
	
	protected CommentApi getCommentApi() {
		if (commentApi == null) {
			commentApi = resourceContext.getResource(CommentApi.class);
		}
		return commentApi;
	}
	
	protected UserApi getUserApi() {
		if (userApi == null) {
			userApi = resourceContext.getResource(UserApi.class);
		}
		return userApi;
	}
}

