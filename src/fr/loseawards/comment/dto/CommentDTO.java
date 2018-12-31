/**
 * 
 */
package fr.loseawards.comment.dto;

import java.io.Serializable;

/**
 * Classe représentant un commentaire.
 */
public class CommentDTO implements Serializable {
	private static final long serialVersionUID = 6142087402103062568L;
	
	private Long id;
	private Long authorId;
	private String date;
	private String content;
	private Long nominationId;
	
	public CommentDTO() {
	}
	
	public CommentDTO(Long id, Long authorId, String date, String content, Long nominationId) {
		this.id = id;
		this.authorId = authorId;
		this.date = date;
		this.content = content;
		this.nominationId = nominationId;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
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
