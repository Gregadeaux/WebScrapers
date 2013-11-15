package com.deaux.ws.frc;

import java.util.ArrayList;
import java.util.List;

public class Regional {
	private String date;
	private List<Integer> teams;
	private String name;
	private String url;
	private String type;
	private String venue;
	private String location;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Regional() {
		teams = new ArrayList<Integer>();
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public List<Integer> getTeams() {
		return teams;
	}
	

	public void setTeams(List<Integer> teams) {
		this.teams = teams;
	}

	public void addTeam(Integer team) {
		teams.add(team);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<Integer> getEqualTeams(Regional regional) {
		List<Integer> retVal = new ArrayList<Integer>();
		
		for(Integer team : regional.getTeams()) {
			if(this.teams.contains(team)) {
				retVal.add(team);
			}
		}
		
		return retVal;
	}
}
