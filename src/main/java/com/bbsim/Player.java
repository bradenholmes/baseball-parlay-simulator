package com.bbsim;

public abstract class Player
{
	private String name;
	private StateVar homeAway;
	private boolean includeStats;
	
	public Player(String name, StateVar homeAway) {
		this.name = name;
		this.homeAway = homeAway;
		this.includeStats = true;
	}
	
	public void ignoreStats() {
		includeStats = false;
	}
	
	public boolean shouldInclude() {
		return includeStats;
	}
	
	public String getName() {
		return name;
	}
	
	public StateVar getHomeAway() {
		return homeAway;
	}
}
