/**
 * 
 */
package fr.loseawards.nomination.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Classe représentant une nomination.
 */
public class NominationDTO implements Serializable {
	private static final long serialVersionUID = -4562731457358330451L;
	
	private Long id;
	private List<Long> usersIds;
	private Long categoryId;
	private String reason;
	private Date date;
	private byte[] image;
	private Long imageId;
	
	public NominationDTO() {
	}
	
	public NominationDTO(Long id, Long[] usersIds, Long categoryId, String reason, Date date, Long imageId) {
		this.id = id;
		if (usersIds != null) {
			this.usersIds = Arrays.asList(usersIds);
		}		
		this.categoryId = categoryId;
		this.reason = reason;
		this.date = date;
		this.imageId = imageId;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public List<Long> getUsersIds() {
		return usersIds;
	}

	public void setUsersIds(List<Long> usersIds) {
		this.usersIds = usersIds;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NominationDTO other = (NominationDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
