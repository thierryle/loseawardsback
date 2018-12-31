package fr.loseawards.decision.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.decision.dto.DecisionDTO;
import fr.loseawards.model.Decision;
import fr.loseawards.util.Converter;

@Path("/decisions")
@Produces(MediaType.APPLICATION_JSON)
public class DecisionApi extends AbstractServiceApi {
	private final Logger log = Logger.getLogger(DecisionApi.class.getName());
	private Cache cachedDecisions = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DecisionApi() {
		Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, 60);

        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cachedDecisions = cacheFactory.createCache(props);
        } catch (CacheException e) {
        	// Pas bloquant
        	log.log(Level.WARNING, "Problème dans la création des caches", e);
        }
	}
	
	protected Cache getCachedDecisions() {
		return cachedDecisions;
	}
	
	/**
	 * Crée un ensemble de décisions.
	 * POST http://localhost:8888/api/decisions/bulk
	 * @param decisionsDTO
	 */
	@POST
	@Path("/bulk")
	public void createDecisions(final List<DecisionDTO> decisionsDTO) {
		// Suppression des décisions précédentes du président
		getPersistenceService().deleteDecisions();

		// Sauvegarde en base
		for (DecisionDTO decisionDto: decisionsDTO) {
			getPersistenceService().addDecision(Converter.fromDTO(decisionDto));
		}
		
		// Mise en cache
		getCachedDecisions().put(0L, decisionsDTO);
	}
	
	/**
	 * Retourne toutes les décisions.
	 * GET http://localhost:8888/api/decisions
	 * @return
	 */
	@GET
	@SuppressWarnings("unchecked")
	public List<DecisionDTO> getDecisions() {
		// Récupération des décisions en cache
		List<DecisionDTO> decisionsDTO = (List<DecisionDTO>) getCachedDecisions().get(0L);
		if (decisionsDTO != null) {
			return decisionsDTO;
		}
		
		// Récupération des décisions en base
		List<Decision> decisions = getPersistenceService().getDecisions();
		
		// Conversion en DTO
		return Converter.toDecisionsDTO(decisions);
	}
	
	/**
	 * Retourne une décision.
	 * GET http://localhost:8888/api/decisions/6643249255022592
	 * @param decisionId
	 * @return
	 */
	@GET
	@Path("/{decisionId}")
	public DecisionDTO getDecision(@PathParam("decisionId") final Long decisionId) {
		Decision decision = getPersistenceService().getDecision(decisionId);
		
		// Conversion en DTO
		return Converter.toDTO(decision);
	}
	
	/**
	 * Supprime une décision.
	 * GET http://localhost:8888/api/decisions/6643249255022592
	 * @param decisionId
	 * @return
	 */
	@DELETE
	@Path("/{decisionId}")
	public void deleteDecision(@PathParam("decisionId") final Long decisionId) {
		getPersistenceService().deleteDecision(decisionId);
	}
	
	/**
	 * Supprime toutes les décisions.
	 * DELETE http://localhost:8888/api/decisions
	 */
	@DELETE
	public void deleteDecisions() {
		getPersistenceService().deleteDecisions();
	}
}
