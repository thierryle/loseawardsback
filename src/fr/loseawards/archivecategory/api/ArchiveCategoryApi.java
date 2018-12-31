package fr.loseawards.archivecategory.api;

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
import fr.loseawards.archivecategory.dto.ArchiveCategoryDTO;
import fr.loseawards.model.ArchiveCategory;
import fr.loseawards.util.Converter;

@Path("/archivecategories")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveCategoryApi extends AbstractServiceApi {
	/**
	 * Retourne les catégories.
	 * GET http://localhost:8888/api/archivecategories
	 * @return
	 */
	@GET
	public List<ArchiveCategoryDTO> getArchiveCategories() {
		// Récupération des catégories dans la base		
		List<ArchiveCategory> archiveCategories = getPersistenceService().getArchiveCategories();

		// Conversion en DTO
		return Converter.toArchiveCategoriesDTO(archiveCategories);
	}
	
	/**
	 * Retourne une catégorie.
	 * GET http://localhost:8888/api/archivecategories/6229832882978816
	 * @param archiveCategoryId
	 */
	@GET
	@Path("/{archiveCategoryId}")
	public ArchiveCategoryDTO getArchiveCategory(@PathParam("archiveCategoryId") final Long archiveCategoryId) {
		ArchiveCategory archiveCategory = getPersistenceService().getArchiveCategory(archiveCategoryId);
		if (archiveCategory == null) {
			return null;
		}
		return Converter.toDTO(archiveCategory);
	}
	
	/**
	 * Crée une catégorie.
	 * POST http://localhost:8888/api/archivecategories
	 * @param archiveCategoryDTO
	 * @return
	 */
	@POST
	public ArchiveCategoryDTO createArchiveCategory(final ArchiveCategoryDTO archiveCategoryDTO) {
		ArchiveCategory archiveCategory = Converter.fromDTO(archiveCategoryDTO);
		getPersistenceService().addArchiveCategory(archiveCategory);
		archiveCategoryDTO.setId(archiveCategory.getId());
		return archiveCategoryDTO;
	}
	
	/**
	 * Supprime une catégorie.
	 * DELETE http://localhost:8888/api/archivecategories/6229832882978816
	 * @param archiveCategoryId
	 */
	@DELETE
	@Path("/{archiveCategoryId}")
	public void deleteArchiveCategory(@PathParam("archiveCategoryId") final Long archiveCategoryId) {
		getPersistenceService().deleteArchiveCategory(archiveCategoryId);
	}
	
	/**
	 * Supprime toutes les catégories.
	 * DELETE http://localhost:8888/api/archivecategories
	 */
	@DELETE
	public void deleteArchiveCategories() {
		List<ArchiveCategory> archiveCategories = getPersistenceService().getArchiveCategories();
		for (ArchiveCategory archiveCategory: archiveCategories) {
			getPersistenceService().deleteArchiveCategory(archiveCategory.getId());
		}		
	}
	
	/**
	 * Met à jour une catégorie.
	 * PUT http://localhost:8888/api/archivecategories/6229832882978816
	 * @param archiveCategoryId
	 * @param archiveCategoryDTO
	 */
	@PUT
	@Path("/{archiveCategoryId}")
	public void updateArchiveCategory(@PathParam("archiveCategoryId") final Long archiveCategoryId, final ArchiveCategoryDTO archiveCategoryDTO) {
		getPersistenceService().updateArchiveCategory(Converter.fromDTO(archiveCategoryDTO));
	}	
}
