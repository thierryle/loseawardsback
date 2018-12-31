package fr.loseawards.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

/**
 * Classe représentant un classement.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchiveReport implements Serializable {
	private static final long serialVersionUID = 6755783654709531152L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected Integer year;
	
	@Persistent
	protected Blob report;
	
	public ArchiveReport() {
	}
	
	public ArchiveReport(Long id, Integer year, Blob report) {
		this.id = id;
		this.year = year;
		this.report = report;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Blob getReport() {
		return report;
	}

	public void setReport(Blob report) {
		this.report = report;
	}
}
