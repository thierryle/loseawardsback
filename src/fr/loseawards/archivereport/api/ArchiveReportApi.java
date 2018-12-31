package fr.loseawards.archivereport.api;

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
import fr.loseawards.archivereport.dto.ArchiveReportDTO;
import fr.loseawards.model.ArchiveReport;
import fr.loseawards.util.Converter;

@Path("/archivereports")
@Produces(MediaType.APPLICATION_JSON)
public class ArchiveReportApi extends AbstractServiceApi {
	/**
	 * Retourne la liste des comptes-rendus.
	 * GET http://localhost:8888/api/archivereports
	 * @return
	 */
	@GET
	public List<ArchiveReportDTO> getArchiveReports() {
		List<ArchiveReport> archiveReports = getPersistenceService().getArchiveReports();
		
		// Conversion en DTO
		List<ArchiveReportDTO> archiveReportsDTO = Converter.toArchiveReportsDTO(archiveReports);
		return archiveReportsDTO;
	}
	
	/**
	 * Retourne un compte-rendu.
	 * GET http://localhost:8888/api/archivereports/6361774278311936
	 * @param archiveReportId
	 * @return
	 */
	@GET
	@Path("/{archiveReportId}")
	public ArchiveReportDTO getArchiveReport(@PathParam("archiveReportId") final Long archiveReportId) {
		ArchiveReport archiveReport = getPersistenceService().getArchiveReport(archiveReportId);
		if (archiveReport == null) {
			return null;
		}
		return Converter.toDTO(archiveReport);
	}
	
	/**
	 * Retourne le compte-rendu d'une année.
	 * GET http://localhost:8888/api/archivereports/year/2009
	 * @param year
	 * @return
	 */
//	@GET
//	@Path("/year/{year}")
//	public ArchiveReportDTO getArchiveReportOfYear(@PathParam("year") final Integer year) {
//		ArchiveReport archiveReport = getPersistenceService().getArchiveReportOfYear(year);
//		if (archiveReport == null) {
//			return null;
//		}
//		return Converter.toDTO(archiveReport);
//	}
	
	/**
	 * Crée un compte-rendu.
	 * POST http://localhost:8888/api/archivereports
	 * @param archiveReportDTO
	 * @return
	 */
	@POST
	public ArchiveReportDTO createArchiveReport(final ArchiveReportDTO archiveReportDTO) {
		ArchiveReport archiveReport = Converter.fromDTO(archiveReportDTO);
		getPersistenceService().addArchiveReport(archiveReport);
		
		archiveReportDTO.setId(archiveReport.getId());
		return archiveReportDTO;
	}
	
	/**
	 * Met à jour un compte-rendu.
	 * PUT http://localhost:8888/api/archivereports/6361774278311936
	 * @param archiveReportId
	 * @return
	 */
	@PUT
	@Path("/{archiveReportId}")
	public void updateArchiveReport(@PathParam("archiveReportId") final Long archiveReportId, final ArchiveReportDTO archiveReportDTO) {
		ArchiveReport archiveReport = Converter.fromDTO(archiveReportDTO);
		getPersistenceService().updateArchiveReport(archiveReport);
	}
	
	/**
	 * Supprime un compte-rendu.
	 * DELETE http://localhost:8888/api/archivereports/6361774278311936
	 * @param archiveReportId
	 */
	@DELETE
	@Path("/{archiveReportId}")
	public void deleteArchiveReport(@PathParam("archiveReportId") final Long archiveReportId) {
		getPersistenceService().deleteArchiveReport(archiveReportId);
	}
}
