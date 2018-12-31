package fr.loseawards.archiveaward.dto;

import java.io.Serializable;

public class GraphicsDatumDTO implements Serializable {
	private static final long serialVersionUID = -6843099673276939704L;
	
	private String name;
	private double y;
	
	public GraphicsDatumDTO(String name, double y) {
		this.name = name;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
}
