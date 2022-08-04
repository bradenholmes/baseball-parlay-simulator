package com.bbsim;

public class Pitcher extends Player
{
	private StateVar handed;
	private int avgInningsPerGame;
	
	private float hitChance, kChance, bbChance;
	
	private int igKs = 0;
	
	public Pitcher(String name, String playerId, StateVar homeAway) {
		super(name, playerId, homeAway);
	}
	
	public void setHandedness(StateVar handed) {
		this.handed = handed;
	}
	
	public void setGameStats(float gamesStarted, float inningsPitched) {
		this.avgInningsPerGame = (int) (inningsPitched / gamesStarted);
	}
	
	public void setBatterStats(float totalBattersFaced, float hits, float ks, float bbs, float hitBatters) {
		this.hitChance = hits / totalBattersFaced;
		this.kChance = ks / totalBattersFaced;
		this.bbChance = (bbs + hitBatters) / totalBattersFaced;
	}
	
	public void setEmptyStats() {
		avgInningsPerGame = 1;
		hitChance = -1;
		kChance = -1;
		bbChance = -1;
		this.ignoreStats();
	}
	
	public StateVar getHandedness() {
		return handed;
	}
	
	public int getAvgInningsPerGame() {
		return avgInningsPerGame;
	}
	
	public float getHitChance() {
		return hitChance;
	}
	
	public float getKChance() {
		return kChance;
	}
	
	public float getWalkChance() {
		return bbChance;
	}
	
	public void addSO() {
		igKs++;
	}
	
	public int getSOs() {
		return igKs;
	}
	
	public void endGame() {
		igKs = 0;
	}
	
	public void print() {
		System.out.println(getName());
	}
}
