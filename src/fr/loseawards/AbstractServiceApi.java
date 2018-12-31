package fr.loseawards;

import javax.ws.rs.core.Context;

import com.sun.jersey.api.core.ResourceContext;

import fr.loseawards.persistence.LoseAwardsPersistenceService;
import fr.loseawards.util.Util;

public class AbstractServiceApi {
	@Context
	protected ResourceContext resourceContext;
	
	/**
	 * Retourne le service de persistance.
	 * @return
	 */
	protected LoseAwardsPersistenceService getPersistenceService() {
		return LoseAwardsPersistenceService.getInstance();
	}
	
	/**
	 * Envoi de mail.
	 * @param address
	 * @param name
	 * @param subject
	 * @param message
	 * @param isHtml
	 */
	protected void sendMail(final String address, final String name, final String subject, final String message, final boolean isHtml) {
		Util.sendMail(address, name, subject, message, isHtml);
	}
}
