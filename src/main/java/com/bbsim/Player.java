package com.bbsim;

public abstract class Player
{
	private String name;
	private String playerId;
	private StateVar homeAway;
	private boolean includeStats;
	
	public Player(String name, String playerId, StateVar homeAway) {
		this.name = name;
		this.playerId = playerId;
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
	
	public String getPlayerId() {
		return playerId;
	}
	
	public StateVar getHomeAway() {
		return homeAway;
	}
}
