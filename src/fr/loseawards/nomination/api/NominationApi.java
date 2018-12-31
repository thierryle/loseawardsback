package fr.loseawards.nomination.api;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.Blob;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.model.Category;
import fr.loseawards.model.Image;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.nomination.dto.NominationBundleDTO;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.util.Converter;
import fr.loseawards.util.Util;

@Path("/nominations")
@Produces(MediaType.APPLICATION_JSON)
public class NominationApi extends AbstractServiceApi {
	/**
	 * Retourne toutes les nominations.
	 * GET http://localhost:8888/api/nominations
	 * @return
	 */
	@GET
	public List<NominationDTO> getNominations() {
		// Récupération des nominations dans la base		
		List<Nomination> nominations = getPersistenceService().getNominations();

		// Conversion en DTO
		return Converter.toNominationsDTO(nominations);
	}
	
	/**
	 * Retourne une nomination.
	 * GET http://localhost:8888/api/nominations/4930210138947584
	 * @param nominationId
	 * @return
	 */
	@GET
	@Path("/{nominationId}")
	public NominationDTO getNomination(@PathParam("nominationId") final Long nominationId) {
		Nomination nomination = getPersistenceService().getNomination(nominationId);
		if (nomination == null) {
			return null;
		}
		return Converter.toDTO(nomination);
	}
	
	/**
	 * Retourne le bundle nécessaire à la page des nominations.
	 * GET http://localhost:8888/api/nominations/bundle
	 * @return
	 */
	@GET
	@Path("/bundle")
	public NominationBundleDTO getNominationBundle() {
		NominationBundleDTO nominationBundleDTO = new NominationBundleDTO();
		
		// Récupération de toutes les nominations
		List<Nomination> nominations = getPersistenceService().getNominations();
		
		// Conversion en DTO et regroupement par catégorie
		List<NominationDTO> nominationsDTO = Converter.toNominationsDTO(nominations);
		Map<Long, List<NominationDTO>> nominationsByCategory = Util.groupByProperty(nominationsDTO, "categoryId");
		nominationBundleDTO.setNominations(nominationsByCategory);		
		return nominationBundleDTO;
	}
	
	/**
	 * Crée une nomination.
	 * POST http://localhost:8888/api/nominations
	 * @param nominationDTO
	 * @return
	 */
	@POST
	public NominationDTO createNomination(final NominationDTO nominationDTO) {
		Nomination nomination = Converter.fromDTO(nominationDTO);
		
		// Image
		if (nominationDTO.getImage() != null) {
			// L'utilisateur a uploadé une image : on l'enregistre, puis on récupère son ID
			Image image = new Image();
			image.setImage(new Blob(nominationDTO.getImage()));
			getPersistenceService().addImage(image);
			
			nomination.setImageId(image.getId());
			nominationDTO.setImageId(image.getId());
			nominationDTO.setImage(null);
		}

		nomination.setDate(new Date());
		getPersistenceService().addNomination(nomination);
		sendNotification(nomination);
		
		nominationDTO.setId(nomination.getId());
		return nominationDTO;
	}
	
	/**
	 * Supprime une nomination.
	 * DELETE http://localhost:8888/api/nominations/4930210138947584
	 * @param nominationId
	 */
	@DELETE
	@Path("/{nominationId}")
	public void deleteNomination(@PathParam("nominationId") final Long nominationId) {
		getPersistenceService().deleteNomination(nominationId);
	}
	
	/**
	 * Supprime toutes les nominations.
	 * DELETE http://localhost:8888/api/nominations
	 */
	@DELETE
	public void deleteNominations() {
		getPersistenceService().deleteNominations();
	}
	
	/**
	 * Met à jour une nomination.
	 * PUT http://localhost:8888/api/nominations/4930210138947584
	 * @param nominationId
	 * @param nominationDTO
	 * @return
	 */
	@PUT
	@Path("/{nominationId}")
	public NominationDTO updateNomination(@PathParam("nominationId") final Long nominationId, final NominationDTO nominationDTO) {
		Nomination nomination = Converter.fromDTO(nominationDTO);
		
		// Image
		if (nominationDTO.getImage() != null) {
			// L'utilisateur a uploadé une image : on l'enregistre, puis on récupère son ID
			Image image = new Image();
			image.setImage(new Blob(nominationDTO.getImage()));
			getPersistenceService().addImage(image);
			
			nomination.setImageId(image.getId());
			nominationDTO.setImageId(image.getId());
			nominationDTO.setImage(null);
		}
		
		nomination.setDate(new Date());
		getPersistenceService().updateNomination(nomination);
		
		return nominationDTO;
	}
	
	/**
	 * Envoie un mail avec toutes les nominations.
	 * GET http://localhost:8888/api/nominations/mail?address=thierry.le@gmail.com
	 * @param address
	 * @return
	 */
	@GET
	@Path("/mail")
	public String sendMail(@QueryParam("address") final String address) {
		Calendar dateDuJour = Calendar.getInstance();
		int annee = dateDuJour.get(Calendar.YEAR);
		
		StringBuilder message = new StringBuilder("<ul>\n"); 
		
		// Utilisateurs, catégories et nominations
		List<User> users = getPersistenceService().getUsers();
		List<Category> categories = getPersistenceService().getCategories();
		List<Nomination> nominations = getPersistenceService().getNominations();
		
		// Parcours des catégories
		for (Category category : categories) {
			message.append("  <li>\n    <b>Catégorie ");
			message.append(category.getName());
			message.append("</b>\n    <ul>\n");
			
			// Parcours des nominations
			for (Nomination nomination : nominations) {
				if (nomination.getCategoryId().equals(category.getId())) {
					message.append("      <li>");
					message.append(Util.getUsersNames(users, nomination.getUsersIds())); 
					message.append(" (");
					message.append(nomination.getReason());
					message.append(")</li>\n");
				}
			}
			
			message.append("    </ul>\n  </li>\n");
		}
		
		message.append("</ul>\n<a href=\"http://loseawards.appspot.com\">http://loseawards.appspot.com</a>");
		
		sendMail(address, address, "Nominations " + annee, message.toString(), true);
		return message.toString();
	}
	
	/**
	 * Envoi d'une notification pour une nouvelle nomination.
	 * @param nomination
	 */
	protected String sendNotification(final Nomination nomination) {
		List<User> users = getPersistenceService().getUsers();
		Category category = getPersistenceService().getCategory(nomination.getCategoryId());
		
		StringBuilder message = new StringBuilder("<p>");
		message.append(Util.getUsersNames(users, nomination.getUsersIds()));
		if (nomination.getUsersIds().length > 1) {
			message.append(" ont été nominés dans la catégorie ");
		} else {
			message.append(" a été nominé dans la catégorie ");
		}
		message.append(category.getName());
		message.append(" (");
		message.append(nomination.getReason());
		message.append(")</p>\n<a href=\"http://loseawards.appspot.com\">http://loseawards.appspot.com</a>");
		
		for (User user : users) {
			if (user.getEmail() != null && !user.getEmail().isEmpty()) {
				sendMail(user.getEmail(), user.getDisplayName(), "Nouvelle nomination", message.toString(), true);
			}
		}
		return message.toString();
	}
}
