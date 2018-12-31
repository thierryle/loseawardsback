/**
 * 
 */
package fr.loseawards.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Classe représentant un commentaire.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Long authorId;
	
	@Persistent
	protected Date date;
	
	@Persistent
	protected String content;
	
	@Persistent
	protected Long nominationId;
	
	public Comment() {
	}
	
	public Comment(Long id, Long authorId, Date date, String content, Long nominationId) {
		this.id = id;
		this.authorId = authorId;
		this.date = date;
		this.content = content;
		this.nominationId = nominationId;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getNominationId() {
		return nominationId;
	}

	public void setNominationId(Long nominationId) {
		this.nominationId = nominationId;
	}
}
