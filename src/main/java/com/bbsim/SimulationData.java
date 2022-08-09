package com.bbsim;

import java.util.List;

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
			
			if (!batter.shouldInclude()) {
				return StringUtils.leftPad(batter.getName(), 17) + "  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - ";
			}
			
			return StringUtils.leftPad(batter.getName(), 17) 
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
			if (!pitcher.shouldInclude()) {
				return StringUtils.leftPad(pitcher.getName(), 17) + " has too few starts... consider skipping this game!";
			}
			return StringUtils.leftPad(pitcher.getName(), 17) + "  Ks: " + App.decimal(avgKs);
		}
	}
	
	public class FirstInningData {
		boolean printError;
		
		String awayName;
		String homeName;
		
		float zero_zero;
		float away_win;
		float tie;
		float home_win;
		
		public FirstInningData(Team home, Team away, float simulations) {
			awayName = away.getName();
			homeName = home.getName();
			List<Integer> awayFirst = away.getFirstInningScores();
			List<Integer> homeFirst = home.getFirstInningScores();
			
			if (awayFirst.size() == simulations && homeFirst.size() == simulations) {
				printError = false;
				
				int zzCount = 0;
				int awayCount = 0;
				int tieCount = 0;
				int homeCount = 0;
				
				for (int i = 0; i < simulations; i++) {
					int a = awayFirst.get(i);
					int h = homeFirst.get(i);
					
					if (a == 0 && h == 0) {
						zzCount++;
					}

					if (a == h) {
						tieCount++;
					} else if (a > h) {
						awayCount++;
					} else if (h > a) {
						homeCount++;
					}
				}
				
				zero_zero = zzCount / simulations;
				away_win = awayCount / simulations;
				tie = tieCount / simulations;
				home_win = homeCount / simulations;
			} else {
				printError = true;
			}
		}
		
		public void print() {
			if (printError) {
				System.err.println("ERROR: There was a problem counting first inning results!");
			} else {
				System.out.println(App.centerText("First Inning Results", false, true));
				System.out.println(App.leftJustifyText(StringUtils.leftPad("0-0: ", 18) + App.percentage(zero_zero), 1, true));
				System.out.println(App.leftJustifyText(StringUtils.leftPad(awayName + " win: ", 18) + App.percentage(away_win), 1, true));
				System.out.println(App.leftJustifyText(StringUtils.leftPad("tie: ", 18) + App.percentage(tie), 1, true));
				System.out.println(App.leftJustifyText(StringUtils.leftPad(homeName + " win: ", 18) + App.percentage(home_win), 1, true));
			}
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
	
	public FirstInningData firstInningData;
	
	public CompleteBetSet completeBetSet;
	
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
	
	public void setFirstInningData(Team awayTeam, Team homeTeam) {
		firstInningData = new FirstInningData(awayTeam, homeTeam, simulations);
	}
	
	public void setCompleteBetSet(CompleteBetSet cbs) {
		this.completeBetSet = cbs;
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
		
		System.out.println(App.TABLE_EMPTY_LINE);
		firstInningData.print();
		
//		System.out.println(App.TABLE_EMPTY_LINE);
//		completeBetSet.print();
	}
}
