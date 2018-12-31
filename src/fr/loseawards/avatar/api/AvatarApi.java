package fr.loseawards.avatar.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.avatar.dto.AvatarDTO;
import fr.loseawards.model.Avatar;
import fr.loseawards.util.Converter;

@Path("/avatars")
public class AvatarApi extends AbstractServiceApi {
	/**
	 * Retourne l'image d'un avatar.
	 * GET http://localhost:8888/api/avatars/4547580092481536
	 * @param avatarId
	 * @return
	 */
	@GET
	@Path("/{avatarId}")
	@Produces("image/png")
	public Response getAvatar(@PathParam("avatarId") final Long avatarId) {
		Avatar avatar = getPersistenceService().getAvatar(avatarId);
		return Response.ok(avatar.getImage().getBytes()).build();
	}
	
	/**
	 * Retourne tous les avatars.
	 * GET http://localhost:8888/api/avatars/
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AvatarDTO> getAvatars() {
		// Récupération des avatars dans la base		
		List<Avatar> avatars = getPersistenceService().getAvatars();

		// Conversion en DTO
		return Converter.toAvatarsDTO(avatars);
	}
	
	/**
	 * Supprime un avatar.
	 * DELETE http://localhost:8888/api/avatars/4547580092481536
	 * @param avatarId
	 */
	@DELETE
	@Path("/{avatarId}")
	public void deleteAvatar(@PathParam("avatarId") final Long avatarId) {
		getPersistenceService().deleteAvatar(avatarId);
	}
}
