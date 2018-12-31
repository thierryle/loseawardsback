package fr.loseawards.model;

public enum GlobalKey {
	NOMINATIONS_OPEN("NOMINATIONS_OPEN"), VOTES_OPEN("VOTES_OPEN"), VOTERS_IDS("VOTERS_IDS"), SHOW_LASTNAME("SHOW_LASTNAME"), NEWS("NEWS");
	
	private final String key;
	
	private GlobalKey(final String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
