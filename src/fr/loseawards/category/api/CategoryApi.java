package fr.loseawards.category.api;

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
import fr.loseawards.category.dto.CategoryDTO;
import fr.loseawards.model.Category;
import fr.loseawards.util.Converter;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryApi extends AbstractServiceApi {
	@GET
	@Path("/{idCategory}")
	public CategoryDTO getCategory(@PathParam("idCategory") final Long idCategory) {
		Category category = getPersistenceService().getCategory(idCategory);
		if (category == null) {
			return null;
		}
		return Converter.toDTO(category);
	}
	
	@GET
	public List<CategoryDTO> getCategories() {
		// Récupération des catégories dans la base		
		List<Category> categories = getPersistenceService().getCategories();

		// Conversion en DTO
		return Converter.toCategoriesDTO(categories);
	}
	
	@POST
	public CategoryDTO createCategory(final CategoryDTO categoryDTO) {
		Category category = Converter.fromDTO(categoryDTO);
		getPersistenceService().addCategory(category);
		categoryDTO.setId(category.getId());
		return categoryDTO;
	}
	
	@DELETE
	@Path("/{idCategory}")
	public void deleteCategory(@PathParam("idCategory") final Long idCategory) {
		getPersistenceService().deleteCategory(idCategory);
	}
	
	@DELETE
	public void deleteCategories() {
		getPersistenceService().deleteCategories();
	}
	
	@PUT
	@Path("/{idCategory}")
	public void updateCategory(@PathParam("idCategory") final Long idCategory, final CategoryDTO categoryDTO) {
		getPersistenceService().updateCategory(Converter.fromDTO(categoryDTO));
	}	
}
