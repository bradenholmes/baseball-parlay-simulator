package com.bbsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bbsim.SimulationData.BatterData;
import com.bbsim.SimulationData.PitcherData;

public class CompleteBetSet
{
	private static final BetType[] BATTER_BET_TYPES = { BetType.ONE_HIT, BetType.TWO_HIT, BetType.TWO_BASES, 
														BetType.THREE_BASES, BetType.HOME_RUN, BetType.RBI, BetType.RUN};
	
	private static final int[] SO_OVER_VALS = {3, 4, 5, 6, 7, 8, 9};
	
	private static final float[] RUNS_OVER_VALS = {6.5f, 8.5f, 10.5f};
	private static final float[] RUNS_UNDER_VALS = {14.5f, 12.5f, 10.5f};
	
	private String gameId;
	private List<Bet> allBets;
	
	public CompleteBetSet(SimulationData data) {
		createBetList(data);
	}
	
	private void createBetList(SimulationData data) {
		this.gameId = data.simGame.gameId;
		allBets = new ArrayList<>();
		
		//Away batters
		for (BatterData bd : data.awayBatterData) {
			if (bd.batter.shouldInclude()) {
				for (BetType type : BATTER_BET_TYPES) {
					Bet b = new Bet(BetClass.BATTER, type);
					b.setSubject(bd.batter);
					allBets.add(b);
				}
			}
		}
		//Home batters
		for (BatterData bd : data.homeBatterData) {
			if (bd.batter.shouldInclude()) {
				for (BetType type : BATTER_BET_TYPES) {
					Bet b = new Bet(BetClass.BATTER, type);
					b.setSubject(bd.batter);
					allBets.add(b);
				}
			}
		}
		
		//Away pitcher
		PitcherData awayP = data.awayPitcherData;
		if (awayP.pitcher.shouldInclude()) {
			for (int i : SO_OVER_VALS) {
				Bet b = new Bet(BetClass.PITCHER, BetType.SO_OVER);
				b.setSubject(awayP.pitcher);
				b.setValue(i);
				allBets.add(b);
			}
		}
		//Home pitcher
		PitcherData homeP = data.homePitcherData;
		if (homeP.pitcher.shouldInclude()) {
			for (int i : SO_OVER_VALS) {
				Bet b = new Bet(BetClass.PITCHER, BetType.SO_OVER);
				b.setSubject(homeP.pitcher);
				b.setValue(i);
				allBets.add(b);
			}
		}
		
		//Money line
		Bet awayMoneyLine = new Bet(BetClass.TEAM, BetType.MONEY_LINE);
		awayMoneyLine.setSubject(data.awayData.team, data.homeData.team);
		Bet homeMoneyLine = new Bet(BetClass.TEAM, BetType.MONEY_LINE);
		homeMoneyLine.setSubject(data.homeData.team, data.awayData.team);
		allBets.add(awayMoneyLine);
		allBets.add(homeMoneyLine);
		
		//First inning outcomes
		Bet firstZeroZero = new Bet(BetClass.GAME, BetType.FIRST_INNING);
		firstZeroZero.setSubject(data.awayData.team, data.homeData.team);
		firstZeroZero.setValue(FirstInningBet.ZERO_ZERO.ordinal());
		Bet firstAwayWin = new Bet(BetClass.GAME, BetType.FIRST_INNING);
		firstAwayWin.setSubject(data.awayData.team, data.homeData.team);
		firstAwayWin.setValue(FirstInningBet.AWAY_WIN.ordinal());
		Bet firstTie = new Bet(BetClass.GAME, BetType.FIRST_INNING);
		firstTie.setSubject(data.awayData.team, data.homeData.team);
		firstTie.setValue(FirstInningBet.TIE.ordinal());
		Bet firstHomeWin = new Bet(BetClass.GAME, BetType.FIRST_INNING);
		firstHomeWin.setSubject(data.homeData.team, data.awayData.team);
		firstHomeWin.setValue(FirstInningBet.HOME_WIN.ordinal());
		allBets.add(firstZeroZero);
		allBets.add(firstAwayWin);
		allBets.add(firstTie);
		allBets.add(firstHomeWin);
		
		//Over Under
		for (float val : RUNS_OVER_VALS) {
			Bet b = new Bet(BetClass.GAME, BetType.RUNS_OVER);
			b.setSubject(data.awayData.team, data.homeData.team);
			b.setValue(val);
			allBets.add(b);
		}
		for (float val : RUNS_UNDER_VALS) {
			Bet b = new Bet(BetClass.GAME, BetType.RUNS_OVER);
			b.setSubject(data.awayData.team, data.homeData.team);
			b.setValue(val);
			allBets.add(b);
		}
		
	}
	
	public List<Bet> getAllBets(){
		return allBets;
	}
	
	public List<Bet> getBetsOfType(BetType type) {
		List<Bet> betsOfType = new ArrayList<>();
		for (Bet b : allBets) {
			if (b.type == type) {
				betsOfType.add(b);
			}
		}
		
		return betsOfType;
	}
	
	public String getGameId() {
		return gameId;
	}
	
	public void print() {
		Collections.sort(allBets);
		System.out.println(App.leftJustifyText("COMPLETE BET SET:", 2, true));
		for (Bet b : allBets) {
			b.print();
		}
	}
	
	
}
