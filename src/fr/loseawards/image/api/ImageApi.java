package fr.loseawards.image.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.loseawards.AbstractServiceApi;
import fr.loseawards.image.dto.ImageBundleDTO;
import fr.loseawards.image.dto.ImageDTO;
import fr.loseawards.model.Image;
import fr.loseawards.model.Nomination;
import fr.loseawards.util.Converter;

@Path("/images")
public class ImageApi extends AbstractServiceApi {
	/**
	 * Retourne une image (binaire).
	 * GET http://localhost:8888/api/images/5323835301691392
	 * @param idImage
	 * @return
	 */
	@GET
	@Path("/{imageId}")
	@Produces("image/png")
	public Response getImage(@PathParam("imageId") final Long imageId) {
		Image image = getPersistenceService().getImage(imageId);
		return Response.ok(image.getImage().getBytes()).build();
	}
	
	/**
	 * Retourne toutes les images.
	 * GET http://localhost:8888/api/image
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ImageDTO> getImages() {
		// Récupération des images dans la base		
		List<Image> images = getPersistenceService().getImages();

		// Conversion en DTO
		return Converter.toImagesDTO(images);
	}
	
	/**
	 * Supprime une image
	 * DELETE http://localhost:8888/api/images/5323835301691392
	 * @param imageId
	 */
	@DELETE
	@Path("/{imageId}")
	public void deleteImage(@PathParam("imageId") final Long imageId) {
		getPersistenceService().deleteImage(imageId);
	}
	
	/**
	 * Retourne le bundle nécessaire à la page des images.
	 * GET http://localhost:8888/api/images/bundle
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/bundle")
	public ImageBundleDTO getImageBundle() {
		ImageBundleDTO imageBundleDTO = new ImageBundleDTO();
		
		List<Nomination> nominations = getPersistenceService().getNominationsWithImage(); 
		
		// Conversion en DTO
		imageBundleDTO.setNominations(Converter.toNominationsDTO(nominations));
		
		return imageBundleDTO;
	}
}
