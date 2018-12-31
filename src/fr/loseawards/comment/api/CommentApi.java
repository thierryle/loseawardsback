package fr.loseawards.comment.api;

import java.util.ArrayList;
import java.util.Date;
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

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.comment.dto.CommentDTO;
import fr.loseawards.model.Comment;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
public class CommentApi extends AbstractServiceApi {
	protected static int MAX_COMMENTS = 10;
	
	/**
	 * Retourne tous les commentaires.
	 * GET http://localhost:8888/api/comments
	 * @return
	 */
	@GET
	public List<CommentDTO> getComments() {
		// Récupération des utilisateurs dans la base
		List<Comment> comments = getPersistenceService().getComments();

		// Conversion en DTO
		List<CommentDTO> commentsDTO = new ArrayList<CommentDTO>();
		for (Comment comment : comments) {
			commentsDTO.add(Converter.toDTO(comment));
		}
		return commentsDTO;
	}
	
	/**
	 * Retourne un commentaire.
	 * GET http://localhost:8888/api/comments/4859841394769920
	 * @return
	 */
	@GET
	@Path("/{commentId}")
	public CommentDTO getComment(@PathParam("commentId") final Long commentId) {
		Comment comment = getPersistenceService().getComment(commentId);
		if (comment == null) {
			return null;
		}
		return Converter.toDTO(comment);
	}
	
	/**
	 * Retourne les commentaires regroupés par nomination.
	 * GET http://localhost:8888/api/comments/group
	 * @return
	 */
	@GET
	@Path("/group")
	public Map<Long, List<CommentDTO>> getCommentsByNominations() {
		List<Comment> comments = getPersistenceService().getComments();
		
		// Conversion en DTO
		List<CommentDTO> commentsDTO = Converter.toCommentsDTO(comments);
		return Util.groupByProperty(commentsDTO, "nominationId");
	}
	
	/**
	 * Retourne les derniers commentaires regroupés par nomination.
	 * GET http://localhost:8888/api/comments/groupLatest
	 * @return
	 */
	@GET
	@Path("/groupLatest")
	public Map<Long, List<CommentDTO>> getLatestCommentsByNominations() {
		Map<Long, List<CommentDTO>> commentsByNomination = new HashMap<Long, List<CommentDTO>>();
		
		List<Comment> comments = getPersistenceService().getComments();
		int nbComments = 0;
		int index = 0;
		
		// On parcourt les commentaires en commençant par les plus récents
		while (nbComments < MAX_COMMENTS && index < comments.size()) {
			Comment comment = comments.get(index);
			
			if (commentsByNomination.get(comment.getNominationId()) == null) {
				// Pour chaque commentaire, il faut récupérer tous les commentaires de la même nomination
				List<Comment> commentsOfOneNomination = Util.getSublistByProperty(comments, "nominationId", comment.getNominationId());
				commentsByNomination.put(comment.getNominationId(), Converter.toCommentsDTO(commentsOfOneNomination));
				nbComments += commentsOfOneNomination.size();
			}
			index++;
		}
		
		return commentsByNomination;
	}

	/**
	 * Création d'un commentaire.
	 * POST http://localhost:8888/api/comments
	 * @param commentDTO
	 * @return
	 */
	@POST
	public CommentDTO createComment(final CommentDTO commentDTO) {
		Comment comment = Converter.fromDTO(commentDTO);
		comment.setDate(new Date());
		getPersistenceService().addComment(comment);
		sendNotification(comment);
		
		commentDTO.setId(comment.getId());
		commentDTO.setDate(Converter.dateToString(comment.getDate()));
		return commentDTO;
	}
	
	/**
	 * Supprime un commentaire.
	 * DELETE http://localhost:8888/api/comments/4859841394769920
	 * @param commentId
	 */
	@DELETE
	@Path("/{commentId}")
	public void deleteComment(@PathParam("commentId") final Long commentId) {
		getPersistenceService().deleteComment(commentId);
	}
	
	/**
	 * Supprime tous les commentaires.
	 * DELETE http://localhost:8888/api/comments
	 */
	@DELETE
	public void deleteComments() {
		getPersistenceService().deleteComments();
	}
	
	/**
	 * Met à jour un commentaire.
	 * PUT http://localhost:8888/api/comments/4859841394769920
	 * @param idComment
	 * @param commentDTO
	 * @return
	 */
	@PUT
	@Path("/{idComment}")
	public CommentDTO updateComment(@PathParam("idComment") final Long idComment, final CommentDTO commentDTO) {
		Comment comment = Converter.fromDTO(commentDTO);
		comment.setDate(new Date());
		getPersistenceService().updateComment(comment);
		
		commentDTO.setDate(Converter.dateToString(comment.getDate()));
		return commentDTO;
	}
	
	/**
	 * Envoi d'une notification pour un nouveau commentaire.
	 * @param comment
	 */
	protected String sendNotification(final Comment comment) {
		List<User> users = getPersistenceService().getUsers();
		Nomination nomination = getPersistenceService().getNomination(comment.getNominationId());
		
		StringBuilder builder = new StringBuilder("<p>");
		builder.append(Util.getObjectById(users, comment.getAuthorId()).getDisplayName());
		builder.append(" a commenté la nomination \"");
		builder.append(nomination.getReason());
		builder.append("\" :</p>\n<p><i>");
		builder.append(comment.getContent());
		builder.append("</i></p>\n<a href=\"http://loseawards.appspot.com\">http://loseawards.appspot.com</a>");
		String message = builder.toString();
		
		for (User user : users) {
			if (user.getEmail() != null && !user.getEmail().isEmpty()) {
				sendMail(user.getEmail(), user.getDisplayName(), "Nouveau commentaire", message, true);
			}
		}
		
		return message;
	}
}
