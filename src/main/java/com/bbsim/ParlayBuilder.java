package com.bbsim;

import org.apache.commons.lang3.StringUtils;

public class ParlayBuilder
{
	public class TeamData {
		Team team;
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
		Batter batter;
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
		Pitcher pitcher;
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
	
	TeamData homeData;
	TeamData awayData;
	
	BatterData[] homeBatterData;
	BatterData[] awayBatterData;
	
	PitcherData homePitcherData;
	PitcherData awayPitcherData;
	
	public ParlayBuilder(float simulations) {
		this.simulations = simulations;
		homeBatterData = new BatterData[9];
		awayBatterData = new BatterData[9];
	}
	
	public void setHomeTeamData(Team team, int wins, int runs) {
		homeData = new TeamData(team, wins, runs, simulations);
	}
	
	public void setAwayTeamData(Team team, int wins, int runs) {
		awayData = new TeamData(team, wins, runs, simulations);
	}
	
	public void setHomeBatters(Batter[] batters) {
		for (int i = 0; i < 9; i++) {
			homeBatterData[i] = new BatterData(batters[i], simulations);
		}
	}
	
	public void setAwayBatters(Batter[] batters) {
		for (int i = 0; i < 9; i++) {
			awayBatterData[i] = new BatterData(batters[i], simulations);
		}
	}
	
	public void setHomePitcher(Pitcher pitcher) {
		homePitcherData = new PitcherData(pitcher, simulations);
	}
	
	public void setAwayPitcher(Pitcher pitcher) {
		awayPitcherData = new PitcherData(pitcher, simulations);
	}
	
	public void printParlayBuilderData() {
		System.out.println("      --------- PARLAY BUILDER DATA ---------");
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
	
	public Parlay build(ParlayLevel level) {
		Parlay parlay = new Parlay(level);
		
		//MONEY LINE
		if (include(level, homeData.winPct, 0.55f, 0.05f)) {
			parlay.addBet(new Bet(BetType.MONEY_LINE, 1, homeData.team, awayData.team));
		} else if (include(level, awayData.winPct, 0.55f, 0.05f)) {
			parlay.addBet(new Bet(BetType.MONEY_LINE, 1, awayData.team, homeData.team));
		}
		
		//RUN LINE
		if (level == ParlayLevel.AGGRESSIVE) {
			if (include(level, homeData.avgRuns - awayData.avgRuns, 1f, 0)) {
				parlay.addBet(new Bet(BetType.RUN_LINE, -1.5f, homeData.team, awayData.team));
			} else if (include(level, awayData.avgRuns - homeData.avgRuns, 1f, 0)) {
				parlay.addBet(new Bet(BetType.RUN_LINE, -1.5f, awayData.team, awayData.team));
			}
		}
		
		//STRIKEOUTS
		int homeKs = Math.round(homePitcherData.avgKs);
		int awayKs = Math.round(awayPitcherData.avgKs);
		switch (level) {
			case BASIC:
				parlay.addBet(new Bet(BetType.SO_OVER, homeKs - 1, homePitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, awayKs - 1, awayPitcherData.pitcher));
				break;
			case CONSERVATIVE:
				parlay.addBet(new Bet(BetType.SO_OVER, homeKs - 2, homePitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, awayKs - 2, awayPitcherData.pitcher));
				break;
			case AGGRESSIVE:
				parlay.addBet(new Bet(BetType.SO_OVER, homeKs, homePitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, awayKs, awayPitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, homeKs - 1, homePitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, awayKs - 1, awayPitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, homeKs - 2, homePitcherData.pitcher));
				parlay.addBet(new Bet(BetType.SO_OVER, awayKs - 2, awayPitcherData.pitcher));
				break;
			default:
				break;
		}
		
		//BATTER PROPS
		for (BatterData b : homeBatterData) {
			addBatterBets(parlay, b, level);
		}
		for (BatterData b : awayBatterData) {
			addBatterBets(parlay, b, level);
		}
		
		return parlay;
	}
	
	public Parlay pruneParlay(Parlay parlay, float minWinPct) {
		Parlay pruned = new Parlay(ParlayLevel.PRUNED);
		
		for (Bet b : parlay.bets) {
			if (b.getWinPct() > minWinPct) {
				if (b.isTeamBet) {
					pruned.addBet(new Bet(b.type, b.value, b.favorite, b.underdog));
				} else {
					pruned.addBet(new Bet(b.type, b.value, b.player));
				}
				
			}
		}
		
		return pruned;
	}
	
	private void addBatterBets(Parlay parlay, BatterData b, ParlayLevel level) {
		if (include(level, b.avgHits, 1, 0.05f)) {
			parlay.addBet(new Bet(BetType.ONE_HIT, 1, b.batter));
		}
		if (include(level, b.avgBases, 2, 0.1f)) {
			parlay.addBet(new Bet(BetType.TWO_BASES, 2, b.batter));
		}
		
		
		if (level == ParlayLevel.AGGRESSIVE) {
			if (include(level, b.avgHits, 2, 0.05f)) {
				parlay.addBet(new Bet(BetType.TWO_HIT, 2, b.batter));
			}
			if (include(level, b.avgBases, 3, 0.1f)) {
				parlay.addBet(new Bet(BetType.THREE_BASES, 3, b.batter));
			}
			if (include(level, b.avgHomers, 1, 0.5f)) {
				parlay.addBet(new Bet(BetType.HOME_RUN, 1, b.batter));
			}
			if (include(level, b.avgRBI, 1, 0.3f)) {
				parlay.addBet(new Bet(BetType.RBI, 1, b.batter));
			}
			if (include(level, b.avgRuns, 1, 0.3f)) {
				parlay.addBet(new Bet(BetType.RUN, 1, b.batter));
			}
		}
	}
	
	private boolean include(ParlayLevel level, float actual, float target, float leniency) {
		float l = leniency;
		switch (level) {
			case BASIC:
				l = 0;
				break;
			case AGGRESSIVE:
				l *= -1;
				break;
			case CONSERVATIVE:
				l *= 1;
				break;
			default:
				break;
		}
		
		float diff = (actual - target) / target;
		if (diff > l) {
			return true;
		} else {
			return false;
		}
	}
}
