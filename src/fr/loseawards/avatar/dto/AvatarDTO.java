package fr.loseawards.avatar.dto;

import java.io.Serializable;

public class AvatarDTO implements Serializable {
	private static final long serialVersionUID = 2509301055664132073L;
	
	private Long id;
	private byte[] image;

	public AvatarDTO() {
	}

	public AvatarDTO(Long id, byte[] image) {
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
