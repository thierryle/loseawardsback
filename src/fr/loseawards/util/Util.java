package fr.loseawards.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import fr.loseawards.model.User;

public class Util {
	/**
	 * Envoi d'un mail.
	 * @param address
	 * @param name
	 * @param message
	 * @param isHtml
	 */
	public static void sendMail(final String address, final String name, final String subject, final String message, final boolean isHtml) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("thierry.le@gmail.com", "Lose Awards"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(address, name));
            msg.setSubject(subject);
            if (isHtml) {
            	Multipart mp = new MimeMultipart();

                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(message, "text/html");
                mp.addBodyPart(htmlPart);

                msg.setContent(mp);
            } else {
            	msg.setText(message);
            }
            Transport.send(msg);
        } catch (MessagingException | UnsupportedEncodingException e) {
        	throw new IllegalArgumentException("Erreur dans l'envoi du mail", e);
		}
	}
	
	/**
	 * Invoque une méthode (sans paramètre) sur un objet.
	 * @param element
	 * @param methodName
	 * @return
	 */
	public static <T> Object invokeMethod(final T element, final String methodName) {
		Method method = null;
		try {
			method = element.getClass().getMethod(methodName, (Class[]) null);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		
		try {
			if (method != null) {
				return method.invoke(element, (Object[]) null);
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		
		return null;
	}
	
	/**
	 * Retourne une propriété d'un objet.
	 * @param element
	 * @param propertyName
	 * @return
	 */
	public static <T> Object getProperty(final T element, final String propertyName) {
		return invokeMethod(element, "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1));
	} 
	
	/**
	 * Retourne un objet d'une liste avec l'ID en paramètre.
	 * @param elements
	 * @param id
	 * @return
	 */
	public static <T> T getObjectById(final List<T> elements, final Long id) {
		return getObjectByProperty(elements, "id", id);
	}
	
	/**
	 * TODO Utiliser deleteObjectsByProperty
	 * Supprime un objet d'une liste avec l'ID en paramètre.
	 * @param elements
	 * @param id
	 */
	public static <T> void deleteObjectById(final List<T> elements, final Long id) {
		for (int i = 0; i < elements.size(); i++) {
			T element = elements.get(i);
			Long elementId = (Long) getProperty(element, "id");
			if (id.equals(elementId)) {
				elements.remove(i);
				break;
			}
		}
	}
	
	public static <T> void deleteObjectsByProperty(final List<T> elements, final String propertyName, final Object propertyValue) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			Object property = getProperty(elements.get(i), propertyName);
			if (property != null && property.equals(propertyValue)) {
				elements.remove(i);
			}
		}
	}
	
	/**
	 * Met à jour un objet dans une liste avec l'ID en paramètre.
	 * @param elements
	 * @param newElement
	 * @param id
	 */
	public static <T> void setObjectById(final List<T> elements, final T newElement, final Long id) {
		for (int i = 0; i < elements.size(); i++) {
			T element = elements.get(i);
			Long elementId = (Long) getProperty(element, "id");
			if (id.equals(elementId)) {
				elements.set(i, newElement);
				break;
			}
		}
	}
	
	/**
	 * Retourne un objet d'une liste avec la propriété passée en paramètre.
	 * @param elements
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public static <T> T getObjectByProperty(final List<T> elements, final String propertyName, final Object propertyValue) {
		for (T element : elements) {
			Object property = getProperty(element, propertyName);
			if (propertyValue.equals(property)) {
				return element;
			}
		}
		
		return null;
	}
	
	/**
	 * Retourne une sous-liste dont les éléments vérifient une propriété.
	 * @param elements
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public static <T> List<T> getSublistByProperty(final List<T> elements, final String propertyName, final Object propertyValue) {
		List<T> result = new ArrayList<T>();
		
		for (T element : elements) {
			Object property = getProperty(element, propertyName);
			if (propertyValue.equals(property)) {
				result.add(element);
			}
		}
		
		return result;
	}
	
	/**
	 * Regroupe les éléments d'une liste selon une propriété.
	 * @param elements
	 * @param propertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <G, T> Map<G, List<T>> groupByProperty(final List<T> elements, final String propertyName) {
		Map<G, List<T>> groupByProperty = new HashMap<G, List<T>>();
		for (T element : elements) {
			G property = (G) Util.getProperty(element, propertyName);
			List<T> elementsOfOneGroup = groupByProperty.get(property);
			if (elementsOfOneGroup == null) {
				elementsOfOneGroup = new ArrayList<T>();
				groupByProperty.put(property, elementsOfOneGroup);
			}
			elementsOfOneGroup.add(element);
		}
		
		
		return groupByProperty;
	}
	
	/**
	 * Place les éléments dans une map en fonction d'une propriété.
	 * @param elements
	 * @param propertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <P, T> Map<P, T> sortByProperty(final List<T> elements, final String propertyName) {
		Map<P, T> elementsByProperty = new HashMap<P, T>();
		for (T element : elements) {
			P property = (P) Util.getProperty(element, propertyName);
			elementsByProperty.put(property, element);
		}
		return elementsByProperty;
	}
	
	/**
	 * Retourne les éléments les plus récurrents d'une liste.
	 * @param elements
	 * @param restrictions Les éléments doivent faire partie de cette liste si elle n'est pas nulle
	 * @return
	 */
	public static List<Long> getMostRecurrentElements(final List<Long> elements, final List<Long> restrictions) {
		List<Long> results = new ArrayList<Long>();
		
		if (elements == null || elements.isEmpty()) {
			return results;
		}
		
		// Recherche de l'itération max
		Map<Long, Integer> iterations = new HashMap<Long, Integer>();
		Integer count;
		for (Long element: elements) {
			if (restrictions == null || restrictions.contains(element)) {
				count = iterations.get(element);
				if (count == null) {
					iterations.put(element, new Integer(1));
				} else {
					iterations.put(element, new Integer(count + 1));
				}
			}
		}
		Integer max = Collections.max(iterations.values());
		
		// Recherche des éléments qui ont l'itération max
		Set<Long> keys = iterations.keySet();
		for (Long element : keys) {
			count = iterations.get(element);
			if (count.equals(max)) {
				results.add(element);
			}
		}
		return results;
	}
	
	/**
	 * Construit les noms des utilisateurs.
	 * @param users
	 * @param usersIds
	 * @return
	 */
	public static String getUsersNames(final List<User> users, final Long[] usersIds) {
		if (usersIds == null || usersIds.length == 0) {
			return "";
		}
		
		StringBuilder usersNames = new StringBuilder();
		for (int i = 0; i < usersIds.length; i++) {
			if (i > 0) {
				if (i == usersIds.length - 1) {
					usersNames.append(" et ");
				} else {
					usersNames.append(", ");
				}
			}
			User user = getObjectById(users, usersIds[i]);
			if (user != null) {
				usersNames.append(user.getFirstName());
			}
		}
		return usersNames.toString();
	}
	
	/**
	 * Indique si un tableau contient un ID.
	 * @param ids
	 * @param id
	 * @return
	 */
	public static boolean contains(final Long[] ids, final Long id) {
		if (ids == null) {
			return false;
		}
		for (Long currentId : ids) {
			if (currentId.equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retourne true si les deux paramètres sont égaux ou tous les deux nulls.
	 * Retourne false dans le cas contraire.
	 * @param id1
	 * @param id2
	 * @return
	 */
	public static boolean equalsOrNulls(final Object id1, final Object id2) {
		if (id1 == null) {
			if (id2 == null) {
				return true;
			}
			return false;
		}
		if (id2 == null) {
			return false;
		}
		return id1.equals(id2);
	}
}
