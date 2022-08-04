package com.bbsim;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.ApiQuery.Game;

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
		
		public String toString() {
			return StringUtils.leftPad(batter.getName(), 15) 
					+ "  Hits: " + App.decimal(avgHits) 
					+ "   Bases: " + App.decimal(avgBases) 
					+ "   Homers: " + App.decimal(avgHomers) 
					+ "   RBI: " + App.decimal(avgRBI) 
					+ "   Runs: " + App.decimal(avgRuns);
		}
	}
	
	public class PitcherData {
		public Pitcher pitcher;
		float avgKs;
		
		public PitcherData(Pitcher p, float simulations) {
			this.pitcher = p;
			avgKs = (float) p.getSOs() / simulations;
		}
		
		public String toString() {
			return StringUtils.leftPad(pitcher.getName(), 15) + "  Ks: " + App.decimal(avgKs);
		}
	}
	
	public Game simGame;
	float simulations;
	
	public TeamData homeData;
	public TeamData awayData;
	
	public BatterData[] homeBatterData;
	public BatterData[] awayBatterData;
	
	public PitcherData homePitcherData;
	public PitcherData awayPitcherData;
	
	public SimulationData(Game game, float simulations) {
		this.simGame = game;
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
		System.out.println(App.TABLE_END_LINE);
		System.out.println(App.centerText("SIMULATION  DATA", false, true));
		System.out.println(App.TABLE_HORIZ_LINE);

		System.out.println(App.centerText(awayData.team.getName().toUpperCase(), false, true));
		System.out.println(App.centerText("win " + App.percentage(awayData.winPct) + " of games", false, true));
		System.out.println(App.centerText("score " + App.decimal(awayData.avgRuns) + " runs", false, true));
		System.out.println(App.leftJustifyText(awayPitcherData.toString(), 4, true));
		System.out.println(App.TABLE_EMPTY_LINE);
		for (BatterData b : awayBatterData) System.out.println(App.leftJustifyText(b.toString(), 4, true));
		System.out.println(App.TABLE_EMPTY_LINE);
		
		System.out.println(App.centerText(homeData.team.getName().toUpperCase(), false, true));
		System.out.println(App.centerText("win " + App.percentage(homeData.winPct) + " of games", false, true));
		System.out.println(App.centerText("score " + App.decimal(homeData.avgRuns) + " runs", false, true));
		System.out.println(App.leftJustifyText(homePitcherData.toString(), 4, true));
		System.out.println(App.TABLE_EMPTY_LINE);
		for (BatterData b : homeBatterData) System.out.println(App.leftJustifyText(b.toString(), 4, true));
	}
}
