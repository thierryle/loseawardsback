package fr.loseawards.user.api;

import java.util.ArrayList;
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

import com.google.appengine.api.datastore.Blob;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.model.Avatar;
import fr.loseawards.model.Nomination;
import fr.loseawards.model.User;
import fr.loseawards.nomination.dto.NominationDTO;
import fr.loseawards.user.dto.UserBundleDTO;
import fr.loseawards.user.dto.UserDTO;
import fr.loseawards.util.Converter;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserApi extends AbstractServiceApi {
	/**
	 * Récupère tous les utilisateurs.
	 * GET http://localhost:8888/api/users
	 * @return
	 */
	@GET
	public List<UserDTO> getUsers() {
		// Récupération des utilisateurs dans la base		
		List<User> users = getPersistenceService().getUsers();

		// Conversion en DTO
		return Converter.toUsersDTO(users);
	}
	
	/**
	 * Récupère un utilisateur
	 * GET http://localhost:8888/api/users/6641050231767040
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/{userId}")
	public UserDTO getUser(@PathParam("userId") final Long userId) {
		User user = getPersistenceService().getUser(userId);
		if (user == null) {
			return null;
		}
		return Converter.toDTO(user);
	}
	
	/**
	 * Crée un utilisateur.
	 * POST http://localhost:8888/api/users
	 * @param userDTO
	 * @return
	 */
	@POST
	public UserDTO createUser(final UserDTO userDTO) {
		User user = Converter.fromDTO(userDTO);
		
		// Avatar
		if (userDTO.getAvatar() != null) {
			Avatar avatar = new Avatar();
			avatar.setImage(new Blob(userDTO.getAvatar()));
			getPersistenceService().addAvatar(avatar);
			
			user.setAvatarId(avatar.getId());
			userDTO.setAvatarId(avatar.getId());
			userDTO.setAvatar(null);
		}
		
		getPersistenceService().addUser(user);
		userDTO.setId(user.getId());
		
		return userDTO;
	}
	
	/**
	 * Supprime un utilisateur.
	 * DELETE http://localhost:8888/api/users/6641050231767040
	 * @param idUser
	 */
	@DELETE
	@Path("/{idUser}")
	public void deleteUser(@PathParam("idUser") final Long idUser) {
		getPersistenceService().deleteUser(idUser);
	}
	
	/**
	 * Met à jour un utilisateur.
	 * PUT http://localhost:8888/api/users/6641050231767040
	 * @param idUser
	 * @param userDTO
	 * @return
	 */
	@PUT
	@Path("/{idUser}")
	public UserDTO updateUser(@PathParam("idUser") final Long idUser, final UserDTO userDTO) {
		User user = Converter.fromDTO(userDTO);
		
		// Avatar
		if (userDTO.getAvatar() != null) {
			Avatar avatar = new Avatar();
			avatar.setImage(new Blob(userDTO.getAvatar()));
			getPersistenceService().addAvatar(avatar);
			
			user.setAvatarId(avatar.getId());
			userDTO.setAvatarId(avatar.getId());
			userDTO.setAvatar(null);
		}
		
		getPersistenceService().updateUser(user);
		
		return userDTO;
	}
	
	/**
	 * Retourne le bundle nécessaire à l'affichage de la page des utilisateurs.
	 * http://localhost:8888/api/users/bundle
	 * @return
	 */
	@GET
	@Path("/bundle")
	public UserBundleDTO getUserBundle() {
		UserBundleDTO userBundleDTO = new UserBundleDTO();
		
		// Récupération de toutes les nominations
		List<Nomination> nominations = getPersistenceService().getNominations();
		
		// Regroupement des nominations par nominés
		Map<Long, List<NominationDTO>> nominationsByUser = new HashMap<Long, List<NominationDTO>>();
		for (Nomination nomination : nominations) {
			if (nomination.getUsersIds() != null) {
				for (Long userId : nomination.getUsersIds()) {
					List<NominationDTO> nominationsOfOneUser = nominationsByUser.get(userId);
					if (nominationsOfOneUser == null) {
						nominationsOfOneUser = new ArrayList<NominationDTO>();
						nominationsByUser.put(userId, nominationsOfOneUser);
					}
					nominationsOfOneUser.add(new NominationDTO(nomination.getId(), nomination.getUsersIds(), nomination.getCategoryId(), nomination.getReason(), nomination.getDate(), nomination.getImageId()));
				}				
			}
		}
		
		userBundleDTO.setNominations(nominationsByUser);		
		return userBundleDTO;
	}
}
