package fr.loseawards.archiveuser.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.archiveuser.dto.ArchiveUserDTO;
import fr.loseawards.model.ArchiveUser;
import fr.loseawards.util.Converter;

@Path("/archiveusers")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveUserApi extends AbstractServiceApi {
	/**
	 * Retourne tous les utilisateurs d'archive.
	 * GET http://localhost:8888/api/archiveusers
	 * @return
	 */
	@GET
	public List<ArchiveUserDTO> getArchiveUsers() {
		// Récupération des utilisateurs dans la base		
		List<ArchiveUser> archiveUsers = getPersistenceService().getArchiveUsers();

		// Conversion en DTO
		return Converter.toArchiveUsersDTO(archiveUsers);
	}
	
	/**
	 * Retourne un utilisateur d'archive.
	 * GET http://localhost:8888/api/archiveusers/5845003813257216
	 * @param archiveUserId
	 * @return
	 */
	@GET
	@Path("/{archiveUserId}")
	public ArchiveUserDTO getArchiveUser(@PathParam("archiveUserId") final Long archiveUserId) {
		ArchiveUser archiveUser = getPersistenceService().getArchiveUser(archiveUserId);
		if (archiveUser == null) {
			return null;
		}

		// Conversion en DTO
		return Converter.toDTO(archiveUser);
	}
	
	/**
	 * Crée un utilisateur d'archive.
	 * POST http://localhost:8888/api/archiveusers
	 * @param archiveUserDTO
	 * @return
	 */
	@POST
	public ArchiveUserDTO createArchiveUser(final ArchiveUserDTO archiveUserDTO) {
		ArchiveUser archiveUser = Converter.fromDTO(archiveUserDTO);
		getPersistenceService().addArchiveUser(archiveUser);
		archiveUserDTO.setId(archiveUser.getId());
		
		return archiveUserDTO;
	}
	
	/**
	 * Supprime un utilisateur d'archive.
	 * DELETE http://localhost:8888/api/archiveusers/5845003813257216
	 * @param archiveUserId
	 */
	@DELETE
	@Path("/{archiveUserId}")
	public void deleteArchiveUser(@PathParam("archiveUserId") final Long archiveUserId) {
		getPersistenceService().deleteArchiveUser(archiveUserId);
	}
	
	/**
	 * Met à jour un utilisateur d'archive.
	 * PUT http://localhost:8888/api/archiveusers/5845003813257216
	 * @param archiveUserId
	 * @param archiveUserDTO
	 * @return
	 */
	@PUT
	@Path("/{archiveUserId}")
	public ArchiveUserDTO updateArchiveUser(@PathParam("archiveUserId") final Long archiveUserId, final ArchiveUserDTO archiveUserDTO) {
		ArchiveUser archiveUser = Converter.fromDTO(archiveUserDTO);
		getPersistenceService().updateArchiveUser(archiveUser);
		
		return archiveUserDTO;
	}
}
