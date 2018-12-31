package fr.loseawards.global.api;

import java.util.ArrayList;
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
import fr.loseawards.global.dto.GlobalDTO;
import fr.loseawards.model.Global;
import fr.loseawards.model.GlobalKey;
import fr.loseawards.util.Converter;

@Path("/globals")
@Produces(MediaType.APPLICATION_JSON)
public class GlobalApi extends AbstractServiceApi {
	/**
	 * Retourne la liste des variables globales.
	 * GET http://localhost:8888/api/globals
	 * @return
	 */
	@GET
	public List<GlobalDTO> getGlobals() {
		List<GlobalDTO> globalsDTO = new ArrayList<GlobalDTO>();
		
		for (GlobalKey globalKey : GlobalKey.values()) {
			Global global = getPersistenceService().getGlobalByKey(globalKey.getKey());
			if (global == null) {
				// Initialisation
				global = new Global();
				global.setKey(globalKey.getKey());
				getPersistenceService().addGlobal(global);
			}
			globalsDTO.add(Converter.toDTO(global));
		}
		return globalsDTO;
	}
	
	/**
	 * Retourne une variable globale.
	 * GET http://localhost:8888/api/globals/5633897580724224
	 * @param globalId
	 * @return
	 */
	@GET
	@Path("/{globalId}")
	public GlobalDTO getGlobal(@PathParam("globalId") final Long globalId) {
		Global global = getPersistenceService().getGlobal(globalId);
		if (global == null) {
			return null;
		}
		return Converter.toDTO(global);
	}
	
	/**
	 * Crée une variable globale.
	 * POST http://localhost:8888/api/globals
	 * @param globalDTO
	 * @return
	 */
	@POST
	public GlobalDTO createGlobal(final GlobalDTO globalDTO) {
		Global global = Converter.fromDTO(globalDTO);
		getPersistenceService().addGlobal(global);
		globalDTO.setId(global.getId());
		return globalDTO;
	}
	
	/**
	 * Supprime une variable globale.
	 * DELETE http://localhost:8888/api/globals/5633897580724224
	 * @param globalId
	 */
	@DELETE
	@Path("/{globalId}")
	public void deleteGlobal(@PathParam("globalId") final Long globalId) {
		getPersistenceService().deleteGlobal(globalId);
	}
	
	/**
	 * Supprime toutes les variables globales.
	 * DELETE http://localhost:8888/api/globals
	 */
	@DELETE
	public void deleteGlobals() {
		getPersistenceService().deleteGlobals();
	}
	
	/**
	 * Met à jour une variable globale.
	 * PUT http://localhost:8888/api/globals/5633897580724224
	 * @param idGlobal
	 * @param globalDTO
	 */
	@PUT
	@Path("/{globalId}")
	public void updateGlobal(@PathParam("globalId") final Long idGlobal, final GlobalDTO globalDTO) {
		getPersistenceService().updateGlobal(Converter.fromDTO(globalDTO));
	}
	
	/**
	 * Met à jour un ensemble de variables globales.
	 * PUT http://localhost:8888/api/globals
	 * @param globalsDTO
	 */
	@PUT
	@Path("/bulk")
	public void updateGlobals(List<GlobalDTO> globalsDTO) {
		for (GlobalDTO globalDTO: globalsDTO) {
			getPersistenceService().updateGlobal(Converter.fromDTO(globalDTO));
		}		
	}
}
