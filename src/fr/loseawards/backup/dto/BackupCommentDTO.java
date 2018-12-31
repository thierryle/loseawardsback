/**
 * 
 */
package fr.loseawards.backup.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe représentant un backup de commentaire.
 */
public class BackupCommentDTO implements Serializable {
	private static final long serialVersionUID = -520505202248146622L;
	
	private String authorName;
	private Date date;
	private String content;
	private String nominationCategoryName;
	private String nominationReason;
	
	public String getAuthorName() {
		return authorName;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
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
	
	public String getNominationCategoryName() {
		return nominationCategoryName;
	}
	
	public void setNominationCategoryName(String nominationCategoryName) {
		this.nominationCategoryName = nominationCategoryName;
	}
	
	public String getNominationReason() {
		return nominationReason;
	}
	
	public void setNominationReason(String nominationReason) {
		this.nominationReason = nominationReason;
	}
}
