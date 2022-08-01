package com.bbsim;

import org.apache.commons.lang3.StringUtils;

public class SimulationData
{
	public class TeamData {
		public Team team;
		float winPct;
		float avgRuns;
		
		public TeamData(Team t, float wins, float runs, float simulations) {
			this.team = t;
			winPct = wins / simulations;
			avgRuns = runs / simulations;
		}
		
		public void print() {
			System.out.println(team.getName().toUpperCase() + " ---  win rate: " + App.percentage(winPct) + "   avg runs: " + App.decimal(avgRuns));
		}
	}
	
	public class BatterData {
		public Batter batter;
		float avgHits;
		float avgBases;
		float avgHomers;
		float avgRBI;
		float avgRuns;
		
		public BatterData(Batter b, float simulations) {
			this.batter = b;
    		avgHits = (float) b.getHits() / simulations;
    		avgBases = (float) b.getBases() / simulations;
    		avgHomers = (float) b.getHomers() / simulations;
    		avgRBI = (float) b.getRBI() / simulations;
    		avgRuns = (float) b.getRuns() / simulations;
		}
		
		public void print() {
			System.out.println(StringUtils.leftPad(batter.getName(), 24) + "  Hits: " + App.decimal(avgHits) + "   Bases: " + App.decimal(avgBases) + "   Homers: " + App.decimal(avgHomers) + "   RBI: " + App.decimal(avgRBI) + "   Runs: " + App.decimal(avgRuns));
		}
	}
	
	public class PitcherData {
		public Pitcher pitcher;
		float avgKs;
		
		public PitcherData(Pitcher p, float simulations) {
			this.pitcher = p;
			avgKs = (float) p.getSOs() / simulations;
		}
		
		public void print() {
			System.out.println(StringUtils.leftPad(pitcher.getName(), 24) + "  Ks: " + App.decimal(avgKs));
		}
	}
	
	float simulations;
	
	public TeamData homeData;
	public TeamData awayData;
	
	public BatterData[] homeBatterData;
	public BatterData[] awayBatterData;
	
	public PitcherData homePitcherData;
	public PitcherData awayPitcherData;
	
	public SimulationData(float simulations) {
		this.simulations = simulations;
		homeBatterData = new BatterData[9];
		awayBatterData = new BatterData[9];
	}
	
	public void setHomeTeamData(Team team, int wins, int runs) {
		homeData = new TeamData(team, wins, runs, simulations);
		setHomeBatters(team.getBatters());
		setHomePitcher(team.getPitcher());
	}
	
	public void setAwayTeamData(Team team, int wins, int runs) {
		awayData = new TeamData(team, wins, runs, simulations);
		setAwayBatters(team.getBatters());
		setAwayPitcher(team.getPitcher());
	}
	
	private void setHomeBatters(Batter[] batters) {
		for (int i = 0; i < 9; i++) {
			homeBatterData[i] = new BatterData(batters[i], simulations);
		}
	}
	
	private void setAwayBatters(Batter[] batters) {
		for (int i = 0; i < 9; i++) {
			awayBatterData[i] = new BatterData(batters[i], simulations);
		}
	}
	
	private void setHomePitcher(Pitcher pitcher) {
		homePitcherData = new PitcherData(pitcher, simulations);
	}
	
	private void setAwayPitcher(Pitcher pitcher) {
		awayPitcherData = new PitcherData(pitcher, simulations);
	}
	
	public void printSimulationData() {
		System.out.println("      ---------  SIMULATION RESULTS  ---------");
		System.out.println("                  ~~ HOME TEAM ~~");
		homeData.print();
		homePitcherData.print();
		for (BatterData b : homeBatterData) b.print();
		System.out.println();
		System.out.println("                  ~~ AWAY TEAM ~~");
		awayData.print();
		awayPitcherData.print();
		for (BatterData b : awayBatterData) b.print();
		System.out.println("      ---------------------------------------");
	}
}
