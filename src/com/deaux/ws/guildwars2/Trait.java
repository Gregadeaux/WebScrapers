package com.deaux.ws.guildwars2;

public class Trait {
	private String name;
	private String description;
	private String modifier1;
	private String modifier2;
	
	private String[] boonLevels;
	private String[] boonNames;
	private String[] boonDescription;
	
	public String[] getBoonLevels() {
		return boonLevels;
	}
	public void setBoonLevels(String[] boonLevels) {
		this.boonLevels = boonLevels;
	}
	public String[] getBoonNames() {
		return boonNames;
	}
	public void setBoonNames(String[] boonNames) {
		this.boonNames = boonNames;
	}
	public String[] getBoonDescription() {
		return boonDescription;
	}
	public void setBoonDescription(String[] boonDescription) {
		this.boonDescription = boonDescription;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getModifier1() {
		return modifier1;
	}
	public void setModifier1(String modifier1) {
		this.modifier1 = modifier1;
	}
	public String getModifier2() {
		return modifier2;
	}
	public void setModifier2(String modifier2) {
		this.modifier2 = modifier2;
	}
	
	public String toString() {
		return name + '\n' + description + '\n' + modifier1 + '\n' + modifier2 + '\n';
	}
	
	
}
