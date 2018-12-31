package fr.loseawards.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import fr.loseawards.model.Archive;
import fr.loseawards.model.ArchiveAward;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.model.ArchiveRank;
import fr.loseawards.model.ArchiveReport;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.model.Avatar;
import fr.loseawards.model.Category;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Decision;
import fr.loseawards.model.Global;
import fr.loseawards.model.Image;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.model.Vote;
import fr.loseawards.util.Util;

public class LoseAwardsPersistenceService {
	private static final LoseAwardsPersistenceService instance = new LoseAwardsPersistenceService();
	
	private final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private final Logger log = Logger.getLogger(LoseAwardsPersistenceService.class.getName());
	
	protected LoseAwardsPersistenceService() {
	}
	
	public static LoseAwardsPersistenceService getInstance() {
		return instance;
	}
	
	public PersistenceManager getPersistenceManager() {
		return pmf.getPersistenceManager();
	}
	
	// ========== Gestion de la classe User ==========
	
	/**
	 * Retourne un utilisateur.
	 * @param id
	 * @return
	 */
	public User getUser(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(User.class, id);
		} finally {
			pm.close();
		}
	}

	/**
	 * Ajoute un utilisateur.
	 * @param user
	 */	
	public void addUser(User user) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(user);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Met à jour un utilisateur.
	 * @param user
	 */
	public void updateUser(User user) {
		PersistenceManager pm = getPersistenceManager();
		User oldUser = null;
		try {
			oldUser = pm.getObjectById(User.class, user.getId());
			oldUser.setFirstName(user.getFirstName());
			oldUser.setLastName(user.getLastName());
			oldUser.setEmail(user.getEmail());
			oldUser.setPassword(user.getPassword());
			if (oldUser.getAvatarId() != null && (user.getAvatarId() == null || !user.getAvatarId().equals(oldUser.getAvatarId()))) {
				// L'avatar a été supprimé ou modifié 
				deleteAvatar(oldUser.getAvatarId());
			}
			oldUser.setAvatarId(user.getAvatarId());
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime un utilisateur.
	 * @param id
	 */
	public void deleteUser(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			User user = pm.getObjectById(User.class, id);
			
			// Suppression de l'avatar
			if (user.getAvatarId() != null) {
				deleteAvatar(user.getAvatarId());
			}					
			
			pm.deletePersistent(user);
			
			// Suppression des nominations et votes associés
			deleteNominationsOfUser(id);
			deleteVotesOfVoter(id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Suppression de tous les utilisateurs
	 */
	public void deleteUsers() {
		List<User> users = getUsers();
		for (User user : users) {
			deleteUser(user.getId());
		}
	}
	
	/**
	 * Récupère la liste des utilisateurs.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		PersistenceManager pm = getPersistenceManager();
		List<User> result = null;
		try {
			Query query = pm.newQuery(User.class);
			query.setOrdering("firstName");
			result = (List<User>) query.execute();
			log.log(Level.INFO, "getUsers a ramené " + result.size() + " utilisateurs.");
		} finally {
			pm.close();
		}
		return result;
	}
	
	// ========== Gestion de la classe Category ==========

	/**
	 * Retourne une catégorie.
	 * @param id
	 * @return
	 */
	public Category getCategory(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Category.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Ajoute une catégorie.
	 * @param category
	 */
	public void addCategory(Category category) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(category);
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime une catégorie.
	 * @param id
	 */
	public void deleteCategory(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Category category = pm.getObjectById(Category.class, id);
			pm.deletePersistent(category);
			
			// Suppression des nominations et votes associées
			deleteNominationsOfCategory(id);
			deleteVotesOfCategory(id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime toutes les catégories.
	 */
	public void deleteCategories() {
		List<Category> categories = getCategories();
		for (Category category: categories) {
			deleteCategory(category.getId());
		}		
	}

	/**
	 * Retourne la liste des catégories.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Category> getCategories() {
		PersistenceManager pm = getPersistenceManager();
		List<Category> result = null;
		try {
			Query query = pm.newQuery(Category.class);
			query.setOrdering("name");
			result = (List<Category>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Met à jour une catégorie.
	 * @param category
	 */
	public void updateCategory(Category category) {
		PersistenceManager pm = getPersistenceManager();
		Category oldCategory = null;
		try {
			oldCategory = pm.getObjectById(Category.class, category.getId());
			oldCategory.setName(category.getName());
		} finally {
			pm.close();
		}
	}

	// ========== Gestion de la classe Nomination ==========

	public Nomination getNomination(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Nomination.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Ajoute une nomination.
	 * @param nomination
	 */
	public void addNomination(Nomination nomination) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(nomination);
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime une nomination.
	 * @param id
	 */
	public void deleteNomination(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Nomination nomination = pm.getObjectById(Nomination.class, id);
			
			// Suppression des commentaires associés
			deleteCommentsOfNomination(id);
			
			if (nomination.getImageId() != null) {
				// Suppression de l'image associée
				deleteImage(nomination.getImageId());
			}

			pm.deletePersistent(nomination);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime toutes les nominations.
	 */
	public void deleteNominations() {
		List<Nomination> nominations = getNominations();
		if (nominations != null) {
			for (Nomination nomination : nominations) {
				deleteNomination(nomination.getId());
			}
		}
	}
	
	/**
	 * Suppression les nominations d'un utilisateur.
	 * @param userId
	 */
	public void deleteNominationsOfUser(Long userId) {
		List<Nomination> nominations = getNominationsByUser(userId);
		if (nominations != null) {
			for (Nomination nomination : nominations) {
				deleteNomination(nomination.getId());
			}
		}
	}
	
	/**
	 * Suppression les nominations d'une catégorie.
	 * @param userId
	 */
	public void deleteNominationsOfCategory(Long categoryId) {
		List<Nomination> nominations = getNominationsByCategory(categoryId);
		if (nominations != null) {
			for (Nomination nomination : nominations) {
				deleteNomination(nomination.getId());
			}
		}
	}

	/**
	 * Retourne la liste des nominations.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Nomination> getNominations() {
		PersistenceManager pm = getPersistenceManager();
		List<Nomination> result = null;
		try {
			Query query = pm.newQuery(Nomination.class);
			result = (List<Nomination>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Met à jour une nomination.
	 * @param nomination
	 */
	public void updateNomination(Nomination nomination) {
		PersistenceManager pm = getPersistenceManager();
		Nomination oldNomination = null;
		try {
			oldNomination = pm.getObjectById(Nomination.class, nomination.getId());
			if (oldNomination.getImageId() != null && (nomination.getImageId() == null || !nomination.getImageId().equals(oldNomination.getImageId()))) {
				// L'image a changé ou a été supprimée
				deleteImage(oldNomination.getImageId());
			}
			
			oldNomination.setCategoryId(nomination.getCategoryId());
			oldNomination.setUsersIds(nomination.getUsersIds());
			oldNomination.setReason(nomination.getReason());
			oldNomination.setDate(nomination.getDate());
			oldNomination.setImageId(nomination.getImageId());
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne les nominations par ordre décroissant de date.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Nomination> getOrderedNominations() {
		PersistenceManager pm = getPersistenceManager();
		List<Nomination> result = null;
		try {
			Query query = pm.newQuery(Nomination.class);
			query.setOrdering("date desc");
			result = (List<Nomination>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les nominations d'un utilisateur.
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Nomination> getNominationsByUser(Long userId) {
		PersistenceManager pm = getPersistenceManager();
		List<Nomination> result = null;
		try {
			Query query = pm.newQuery(Nomination.class, "usersIds == userIdParam");
			query.declareParameters("Long userIdParam");
			result = (List<Nomination>) query.execute(userId);
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les nominations d'une catégorie.
	 * @param categoryId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Nomination> getNominationsByCategory(Long categoryId) {
		PersistenceManager pm = getPersistenceManager();
		List<Nomination> result = null;
		try {
			Query query = pm.newQuery(Nomination.class, "categoryId == categoryIdParam");
			query.declareParameters("Long categoryIdParam");
			result = (List<Nomination>) query.execute(categoryId);
		} finally {
			pm.close();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Nomination> getNominationsWithImage() {
		PersistenceManager pm = getPersistenceManager();
		List<Nomination> result = null;
		try {
			Query query = pm.newQuery(Nomination.class, "imageId > -1");
			result = (List<Nomination>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Nomination> getLastNominations() {
//		PersistenceManager pm = getPersistenceManager();
//		List<Nomination> result = null;
//		try {
//			Query query = pm.newQuery(Nomination.class);
//			query.setOrdering("date desc");
//			query.setRange(0, NOMINATIONS_NUMBER);
//			result = (List<Nomination>) query.execute();
//			log.log(Level.INFO, "getLastNominations a ramené " + result.size() + " nominations.");
//		} finally {
//			pm.close();
//		}
//		
//		return result;
//	}
	
	// ========== Gestion de la classe Comment ==========
	
	/**
	 * Retourne la liste des commentaires.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Comment> getComments() {
		PersistenceManager pm = getPersistenceManager();
		List<Comment> result = null;
		try {
			Query query = pm.newQuery(Comment.class);
			query.setOrdering("date");
			result = (List<Comment>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Ajoute un commentaire.
	 * @param comment
	 */
	public void addComment(Comment comment) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(comment);
		} finally {
			pm.close();
		}
	}

	/**
	 * Retourne un commentaire.
	 * @param id
	 * @return
	 */
	public Comment getComment(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Comment.class, id);
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime un commentaire.
	 * @param id
	 */
	public void deleteComment(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			 Comment comment = pm.getObjectById(Comment.class, id);
			 pm.deletePersistent(comment);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime tous les commentaires.
	 */
	public void deleteComments() {
		List<Comment> comments = getComments();		
		for (Comment comment : comments) {
			deleteComment(comment.getId());
		}		
	}
	
	/**
	 * Supprime les commentaires d'une nomination.
	 * @param nominationId
	 */
	public void deleteCommentsOfNomination(Long nominationId) {
		List<Comment> comments = getCommentsByNomination(nominationId);
		if (comments != null) {
			for (Comment comment : comments) {
				deleteComment(comment.getId());
			}
		}
	}
	
	/**
	 * Met à jour un commentaire.
	 * @param comment
	 */
	public void updateComment(Comment comment) {
		PersistenceManager pm = getPersistenceManager();
		Comment oldComment = null;
		try {
			oldComment = pm.getObjectById(Comment.class, comment.getId());
			oldComment.setAuthorId(comment.getAuthorId());
			oldComment.setContent(comment.getContent());
			oldComment.setDate(comment.getDate());
			oldComment.setNominationId(comment.getNominationId());
		} finally {
			pm.close();
		}
	}

	/**
	 * Retourne les commentaires d'une nominations.
	 * @param nominationId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Comment> getCommentsByNomination(Long nominationId) {
		PersistenceManager pm = getPersistenceManager();
		List<Comment> result = null;
		try {
			Query query = pm.newQuery(Comment.class, "nominationId == nominationIdParam");
			query.declareParameters("Long nominationIdParam");
			result = (List<Comment>) query.execute(nominationId);
		} finally {
			pm.close();
		}
		return result;
	}
	
	// ===== Gestion de la classe Vote =====
	
	/**
	 * Retourne un vote.
	 * @param id
	 * @return
	 */
	public Vote getVote(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Vote.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Ajoute un vote.
	 * @param vote
	 */
	public void addVote(Vote vote) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(vote);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime les votes d'un utilisateur.
	 * @param voterId
	 */
	@SuppressWarnings("unchecked")
	public void deleteVotesOfVoter(Long voterId) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Query query = pm.newQuery(Vote.class, "voterId == voterIdParam");
			query.declareParameters("Long voterIdParam");
			List<Vote> result = (List<Vote>) query.execute(voterId);
			for (Vote vote: result) {
				pm.deletePersistent(vote);
			}
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime les votes liés à une catégorie.
	 * @param categoryId
	 */
	@SuppressWarnings("unchecked")
	public void deleteVotesOfCategory(Long categoryId) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Query query = pm.newQuery(Vote.class, "categoryId == categoryIdParam");
			query.declareParameters("Long categoryIdParam");
			List<Vote> result = (List<Vote>) query.execute(categoryId);
			for (Vote vote: result) {
				pm.deletePersistent(vote);
			}
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime tous les votes.
	 */
	public void deleteVotes() {
		List<Vote> votes = getVotes();		
		for (Vote vote : votes) {
			deleteVote(vote.getId());
		}
	}
	
	/**
	 * Supprime un vote.
	 * @param id
	 */
	public void deleteVote(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Vote vote = pm.getObjectById(Vote.class, id);
			pm.deletePersistent(vote);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne tous les votes.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Vote> getVotes() {
		PersistenceManager pm = getPersistenceManager();
		List<Vote> result = null;
		try {
			Query query = pm.newQuery(Vote.class);
			result = (List<Vote>) query.execute();
		} finally {
			pm.close();
		}
	
		return result;
	}
	
	/**
	 * Retourne les votes d'un utilisateur.
	 * @param voterId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Vote> getVotesByVoter(Long voterId) {
		PersistenceManager pm = getPersistenceManager();
		List<Vote> result = null;
		try {
			Query query = pm.newQuery(Vote.class, "voterId == voterIdParam");
			query.declareParameters("Long voterIdParam");
			result = (List<Vote>) query.execute(voterId);
		} finally {
			pm.close();
		}
	
		return result;
	}
	
	// ===== Gestion de la classe Decision =====
	
	/**
	 * Ajoute une décision.
	 * @param decision
	 */
	public void addDecision(Decision decision) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(decision);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime toutes les décisions.
	 */
	@SuppressWarnings("unchecked")
	public void deleteDecisions() {
		PersistenceManager pm = getPersistenceManager();
		try {
			Query query = pm.newQuery(Decision.class);
			List<Decision> result = (List<Decision>) query.execute();
			for (Decision decision: result) {
				pm.deletePersistent(decision);
			}
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne toutes les décisions.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Decision> getDecisions() {
		PersistenceManager pm = getPersistenceManager();
		List<Decision> result = null;
		try {
			Query query = pm.newQuery(Decision.class);
			result = (List<Decision>) query.execute();
			log.log(Level.INFO, "getDecisions a ramené " + result.size() + " decisions.");
		} finally {
			pm.close();
		}
	
		return result;
	}
	
	/**
	 * Retourne une décision.
	 * @param id
	 * @return
	 */
	public Decision getDecision(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Decision.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime une décision.
	 * @param id
	 */
	public void deleteDecision(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Decision avatar = pm.getObjectById(Decision.class, id);
			pm.deletePersistent(avatar);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Decision " + id + " non-trouvée.");
		} finally {
			pm.close();
		}
	}
	
	// ===== Gestion de la classe Global =====
	
	/**
	 * Récupère la liste des variables globales.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Global> getGlobals() {
		PersistenceManager pm = getPersistenceManager();
		List<Global> result = null;
		try {
			Query query = pm.newQuery(Global.class);
			result = (List<Global>) query.execute();
			log.log(Level.INFO, "getGlobals a ramené " + result.size() + " variables globales.");
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Retourne une variable globale.
	 * @param id
	 * @return
	 */
	public Global getGlobal(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Global.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Ajoute une variable globale.
	 * @param global
	 */
	public void addGlobal(Global global) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(global);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime une variable globale.
	 * @param id
	 */
	public void deleteGlobal(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Global global = pm.getObjectById(Global.class, id);
			pm.deletePersistent(global);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime toutes les variables globales.
	 * @param id
	 */
	public void deleteGlobals() {
		List<Global> globals = getGlobals();
		for (Global global : globals) {
			deleteGlobal(global.getId());
		}
	}
	
	/**
	 * Met à jour une variable globale.
	 * @param global
	 */
	public void updateGlobal(Global global) {
		PersistenceManager pm = getPersistenceManager();
		Global oldGlobal = null;
		try {
			oldGlobal = pm.getObjectById(Global.class, global.getId());
			oldGlobal.setKey(global.getKey());
			oldGlobal.setValue(global.getValue());
			oldGlobal.setValuesIds(global.getValuesIds());
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne une variable globale par son identifiant fonctionnel.
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Global getGlobalByKey(String key) {
		PersistenceManager pm = getPersistenceManager();
		List<Global> result = null;
		try {
			Query query = pm.newQuery(Global.class, "key == keyParam");
			query.declareParameters("String keyParam");
			result = (List<Global>) query.execute(key);
		} finally {
			pm.close();
		}
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	// ========== Gestion de la classe Avatar ==========

	/**
	 * Retourne un avatar.
	 * @param id
	 * @return
	 */
	public Avatar getAvatar(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Avatar.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne tous les avatars.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Avatar> getAvatars() {
		PersistenceManager pm = getPersistenceManager();
		List<Avatar> result = null;
		try {
			Query query = pm.newQuery(Avatar.class);
			result = (List<Avatar>) query.execute();
			log.log(Level.INFO, "getAvatars a ramené " + result.size() + " avatars.");
		} finally {
			pm.close();
		}
	
		return result;
	}
	
	
	/**
	 * Ajoute un avatar.
	 * @param avatar
	 */
	public void addAvatar(Avatar avatar) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(avatar);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime un avatar.
	 * @param id
	 */
	public void deleteAvatar(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Avatar avatar = pm.getObjectById(Avatar.class, id);
			pm.deletePersistent(avatar);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Avatar " + id + " non-trouvé.");
		} finally {
			pm.close();
		}
	}
	
	// ========== Gestion de la classe Image ==========

	/**
	 * Retourne une image.
	 * @param id
	 * @return
	 */
	public Image getImage(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Image.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne toutes les images.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Image> getImages() {
		PersistenceManager pm = getPersistenceManager();
		List<Image> result = null;
		try {
			Query query = pm.newQuery(Image.class);
			result = (List<Image>) query.execute();
		} finally {
			pm.close();
		}
	
		return result;
	}
	
	/**
	 * Ajoute une image.
	 * @param image
	 */
	public void addImage(Image image) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(image);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime une image.
	 * @param id
	 */
	public void deleteImage(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Image image = pm.getObjectById(Image.class, id);
			pm.deletePersistent(image);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Image " + id + " non-trouvé.");
		} finally {
			pm.close();
		}
	}
	
	// ===== Gestion de la classe ArchiveUser =====
	
	/**
	 * Ajoute un utilisateur.
	 * @param archiveUser
	 */	
	public void addArchiveUser(ArchiveUser archiveUser) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archiveUser);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Met à jour un utilisateur.
	 * @param archiveUser
	 */
	public void updateArchiveUser(ArchiveUser archiveUser) {
		PersistenceManager pm = getPersistenceManager();
		ArchiveUser oldArchiveUser = null;
		try {
			oldArchiveUser = pm.getObjectById(ArchiveUser.class, archiveUser.getId());
			oldArchiveUser.setFirstName(archiveUser.getFirstName());
			oldArchiveUser.setLastName(archiveUser.getLastName());
			oldArchiveUser.setFirstYear(archiveUser.getFirstYear());
			oldArchiveUser.setLastYear(archiveUser.getLastYear());
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime un utilisateur.
	 * @param id
	 */
	public void deleteArchiveUser(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			// Suppression des récompenses de l'utilisateur
			deleteArchiveAwardsOfUser(id);
			
			ArchiveUser archiveUser = pm.getObjectById(ArchiveUser.class, id);
			pm.deletePersistent(archiveUser);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Utilisateur d'archive " + id + " non-trouvé.");
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime tous les utilisateurs.
	 * @param id
	 */
	public void deleteArchiveUsers() {
		List<ArchiveUser> users = getArchiveUsers();
		for (ArchiveUser user : users) {
			deleteArchiveUser(user.getId());
		}
	}
	
	/**
	 * Récupère la liste des utilisateurs.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveUser> getArchiveUsers() {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveUser> result = null;
		try {
			Query query = pm.newQuery(ArchiveUser.class);
			query.setOrdering("firstName");
			result = (List<ArchiveUser>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne un utilisateur.
	 * @param id
	 * @return
	 */
	public ArchiveUser getArchiveUser(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(ArchiveUser.class, id);
		} finally {
			pm.close();
		}
	}
	
	// ===== Gestion de la classe ArchiveReport =====
	
	/**
	 * Ajoute un compte-rendu.
	 * @param archiveReport
	 */	
	public void addArchiveReport(ArchiveReport archiveReport) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archiveReport);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Met à jour un compte-rendu.
	 * @param archiveReport
	 */
	public void updateArchiveReport(ArchiveReport archiveReport) {
		PersistenceManager pm = getPersistenceManager();
		ArchiveReport oldArchiveReport = null;
		try {
			oldArchiveReport = pm.getObjectById(ArchiveReport.class, archiveReport.getId());
			oldArchiveReport.setYear(archiveReport.getYear());
			oldArchiveReport.setReport(archiveReport.getReport());
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime un compte-rendu.
	 * @param id
	 */
	public void deleteArchiveReport(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			ArchiveReport archiveReport = pm.getObjectById(ArchiveReport.class, id);
			pm.deletePersistent(archiveReport);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime tous les comptes-rendus.
	 * @param id
	 */
	public void deleteArchiveReports() {
		List<ArchiveReport> reports = getArchiveReports();
		for (ArchiveReport report : reports) {
			deleteArchiveReport(report.getId());
		}
	}
	
	/**
	 * Récupère la liste des compte-rendus (par ordre décroissant d'année).
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveReport> getArchiveReports() {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveReport> result = null;
		try {
			Query query = pm.newQuery(ArchiveReport.class);
			query.setOrdering("year desc");
			result = (List<ArchiveReport>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne un compte-rendu
	 * @param id
	 * @return
	 */
	public ArchiveReport getArchiveReport(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(ArchiveReport.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Récupère le compte-rendu d'une année.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArchiveReport getArchiveReportOfYear(Integer year) {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveReport> result = null;
		try {
			Query query = pm.newQuery(ArchiveReport.class, "year == yearParam");
			query.declareParameters("Integer yearParam");
			result = (List<ArchiveReport>) query.execute(year);
		} finally {
			pm.close();
		}
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	/**
	 * Supprime un compte-rendu.
	 * @param year
	 */
	public void deleteArchiveReportOfYear(Integer year) {
		ArchiveReport archiveReport = getArchiveReportOfYear(year);
		if (archiveReport != null) {
			deleteArchiveReport(archiveReport.getId());
		}
	}
	
	// ===== Gestion de la classe ArchiveCategory =====
	
	/**
	 * Retourne une catégorie.
	 * @param id
	 * @return
	 */
	public ArchiveCategory getArchiveCategory(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(ArchiveCategory.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Ajoute une catégorie.
	 * @param archiveCategory
	 */
	public void addArchiveCategory(ArchiveCategory archiveCategory) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archiveCategory);
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime une catégorie.
	 * @param id
	 */
	public void deleteArchiveCategory(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			// Suppression des archives contenant cette catégorie
			deleteArchivesOfCategory(id);
			
			ArchiveCategory archiveCategory = pm.getObjectById(ArchiveCategory.class, id);
			pm.deletePersistent(archiveCategory);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime toutes les catégories.
	 * @param id
	 */
	public void deleteArchiveCategories() {
		List<ArchiveCategory> archiveCategories = getArchiveCategories();
		for (ArchiveCategory archiveCategory : archiveCategories) {
			deleteArchiveCategory(archiveCategory.getId());
		}
	}

	/**
	 * Retourne la liste des catégories.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveCategory> getArchiveCategories() {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveCategory> result = null;
		try {
			Query query = pm.newQuery(ArchiveCategory.class);
			query.setOrdering("name");
			result = (List<ArchiveCategory>) query.execute();
			log.log(Level.INFO, "getArchiveCategories a ramené " + result.size() + " catégories.");
		} finally {
			pm.close();
		}
		return result;
	}

	/**
	 * Met à jour une catégorie.
	 * @param archiveCategory
	 */
	public void updateArchiveCategory(ArchiveCategory archiveCategory) {
		PersistenceManager pm = getPersistenceManager();
		ArchiveCategory oldArchiveCategory = null;
		try {
			oldArchiveCategory = pm.getObjectById(ArchiveCategory.class, archiveCategory.getId());
			oldArchiveCategory.setName(archiveCategory.getName());
		} finally {
			pm.close();
		}
	}
	
	// ===== Gestion de la classe Archive
	
	/**
	 * Retourne une archive.
	 * @param id
	 * @return
	 */
	public Archive getArchive(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(Archive.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne l'archive d'une année.
	 * @param year
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Archive getArchiveByYear(Integer year) {
		PersistenceManager pm = getPersistenceManager();
		List<Archive> result = null;
		try {
			Query query = pm.newQuery(Archive.class, "year == yearParam");
			query.declareParameters("Integer yearParam");
			result = (List<Archive>) query.execute(year);
		} finally {
			pm.close();
		}
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	/**
	 * Ajoute une archive.
	 * @param archive
	 */	
	public void addArchive(Archive archive) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archive);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Met à jour une archive.
	 * @param archiveUser
	 */
	public void updateArchive(Archive archive) {
		PersistenceManager pm = getPersistenceManager();
		Archive oldArchive = null;
		try {
			oldArchive = pm.getObjectById(Archive.class, archive.getId());
			oldArchive.setYear(archive.getYear());
			oldArchive.setCategoriesIds(archive.getCategoriesIds());
		} finally {
			pm.close();
		}
	}

	/**
	 * Supprime une archive.
	 * @param id
	 */
	public void deleteArchive(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			Archive archive = pm.getObjectById(Archive.class, id);
			
			// Suppression des récompenses, rangs et compte-rendu de cette année
			deleteArchiveAwardsOfYear(archive.getYear());
			deleteArchiveRanksOfYear(archive.getYear());
			deleteArchiveReportOfYear(archive.getYear());
			
			pm.deletePersistent(archive);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Archive " + id + " non-trouvée.");
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime l'archive d'une année.
	 * @param year
	 */
	public void deleteArchiveOfYear(Integer year) {
		Archive archive = getArchiveByYear(year);
		if (archive != null) {
			deleteArchive(archive.getId());
		}		
	}
	
	/**
	 * Supprime les archives d'une catégorie.
	 * @param categoryId
	 */
	public void deleteArchivesOfCategory(Long categoryId) {
		List<Archive> archives = getArchives();
		for (Archive archive: archives) {
			if (Util.contains(archive.getCategoriesIds(), categoryId)) {
				deleteArchive(archive.getId());
			}
		}
	}
	
	/**
	 * Récupère la liste des archive.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Archive> getArchives() {
		PersistenceManager pm = getPersistenceManager();
		List<Archive> result = null;
		try {
			Query query = pm.newQuery(Archive.class);
			query.setOrdering("year desc");
			result = (List<Archive>) query.execute();
			log.log(Level.INFO, "getArchives a ramené " + result.size() + " archives.");
		} finally {
			pm.close();
		}
		return result;
	}
	
	// ===== Gestion de la classe ArchiveAward =====
	
	/**
	 * Ajoute une récompense.
	 * @param archive
	 */	
	public void addArchiveAward(ArchiveAward archiveAward) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archiveAward);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Retourne une récompense.
	 * @param id
	 * @return
	 */
	public ArchiveAward getArchiveAward(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(ArchiveAward.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne toutes les récompenses.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveAward> getArchiveAwards() {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveAward> result = null;
		try {
			Query query = pm.newQuery(ArchiveAward.class);
			result = (List<ArchiveAward>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les récompenses d'une année.
	 * @param year
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveAward> getArchiveAwardsByYear(Integer year) {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveAward> result = null;
		try {
			Query query = pm.newQuery(ArchiveAward.class, "year == yearParam");
			query.declareParameters("Integer yearParam");
			result = (List<ArchiveAward>) query.execute(year);
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les récompenses d'une catégorie.
	 * @param categoryId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveAward> getArchiveAwardsByCategory(Long categoryId) {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveAward> result = null;
		try {
			Query query = pm.newQuery(ArchiveAward.class, "categoryId == categoryIdParam");
			query.declareParameters("Long categoryIdParam");
			result = (List<ArchiveAward>) query.execute(categoryId);
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les récompenses d'un utilisateur.
	 * @param userId
	 * @return
	 */
	public List<ArchiveAward> getArchiveAwardsByUser(Long userId) {
		List<ArchiveAward> archiveAwards = new ArrayList<ArchiveAward>();
		
		List<ArchiveAward> result = getArchiveAwards();
		for (ArchiveAward archiveAward : result) {
			if (archiveAward.getUsersIds() != null && Util.contains(archiveAward.getUsersIds(), userId)) {
				archiveAwards.add(archiveAward);
			}
		}
		return archiveAwards;
	}
	
	/**
	 * Supprime une récompense.
	 * @param id
	 */
	public void deleteArchiveAward(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			ArchiveAward archiveAward = pm.getObjectById(ArchiveAward.class, id);
			pm.deletePersistent(archiveAward);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Récompense " + id + " non-trouvée.");
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime les récompenses d'une année.
	 * @param year
	 */
	public void deleteArchiveAwardsOfYear(Integer year) {
		List<ArchiveAward> archiveAwards = getArchiveAwardsByYear(year);
		for (ArchiveAward archiveAward : archiveAwards) {
			deleteArchiveAward(archiveAward.getId());
		}
	}
	
	/**
	 * Supprime toutes les récompenses.
	 */
	public void deleteArchiveAwards() {
		List<ArchiveAward> archiveAwards = getArchiveAwards();
		for (ArchiveAward archiveAward : archiveAwards) {
			deleteArchiveAward(archiveAward.getId());
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void deleteArchiveAwardsOfYear(Integer year) {
//		PersistenceManager pm = getPersistenceManager();
//		List<ArchiveAward> result = null;
//		try {
//			Query query = pm.newQuery(ArchiveAward.class, "year == yearParam");
//			query.declareParameters("Integer yearParam");
//			result = (List<ArchiveAward>) query.execute(year);
//			for (ArchiveAward archiveAward: result) {
//				pm.deletePersistent(archiveAward);
//			}
//		} finally {
//			pm.close();
//		}
//	}
	
	/**
	 * Supprime les récompenses d'un utilisateur.
	 * @param userId
	 */
	public void deleteArchiveAwardsOfUser(Long userId) {
		List<ArchiveAward> archiveAwards = getArchiveAwards();
		for (ArchiveAward archiveAward: archiveAwards) {
			if (Util.contains(archiveAward.getUsersIds(), userId)) {
				deleteArchiveAward(archiveAward.getId());
			}
		}
	}
	
	// ===== Gestion de la classe ArchiveRank =====
	
	/**
	 * Ajoute un rang.
	 * @param archiveRank
	 */
	public void addArchiveRank(ArchiveRank archiveRank) {
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(archiveRank);
		} finally {
			pm.close();
		}		
	}
	
	/**
	 * Retourne un rang.
	 * @param id
	 * @return
	 */
	public ArchiveRank getArchiveRank(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			return pm.getObjectById(ArchiveRank.class, id);
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Retourne tous les rangs.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveRank> getArchiveRanks() {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveRank> result = null;
		try {
			Query query = pm.newQuery(ArchiveRank.class);
			query.setOrdering("year");
			result = (List<ArchiveRank>) query.execute();
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les rangs d'une année.
	 * @param year
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArchiveRank> getArchiveRanksByYear(Integer year) {
		PersistenceManager pm = getPersistenceManager();
		List<ArchiveRank> result = null;
		try {
			Query query = pm.newQuery(ArchiveRank.class, "year == yearParam");
			query.declareParameters("Integer yearParam");
			result = (List<ArchiveRank>) query.execute(year);
		} finally {
			pm.close();
		}
		return result;
	}
	
	/**
	 * Retourne les rangs d'un utilisageur.
	 * @param userId
	 * @return
	 */
	public List<ArchiveRank> getArchiveRanksByUser(Long userId) {
		List<ArchiveRank> archiveRanks = new ArrayList<ArchiveRank>();
		
		List<ArchiveRank> result = getArchiveRanks();
		for (ArchiveRank archiveRank : result) {
			if (archiveRank.getUsersIds() != null && Util.contains(archiveRank.getUsersIds(), userId)) {
				archiveRanks.add(archiveRank);
			}
		}
		return archiveRanks;
	}
	
	/**
	 * Supprime un rang.
	 * @param id
	 */
	public void deleteArchiveRank(Long id) {
		PersistenceManager pm = getPersistenceManager();
		try {
			ArchiveRank archiveRank = pm.getObjectById(ArchiveRank.class, id);
			pm.deletePersistent(archiveRank);
		} catch (JDOObjectNotFoundException e) {
			log.log(Level.WARNING, "Rang " + id + " non-trouvée.");
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Supprime les rangs d'une année.
	 * @param year
	 */
	public void deleteArchiveRanksOfYear(Integer year) {
		List<ArchiveRank> archiveRanks = getArchiveRanksByYear(year);
		for (ArchiveRank archiveRank : archiveRanks) {
			deleteArchiveRank(archiveRank.getId());
		}
	}
}
