package fr.loseawards.image.dto;

import java.io.Serializable;

public class ImageDTO implements Serializable {
	private static final long serialVersionUID = 4109598470363953821L;
	
	private Long id;
	private byte[] image;

	public ImageDTO() {
	}

	public ImageDTO(Long id, byte[] image) {
		this.id = id;
		this.image = image;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}
