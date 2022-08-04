package com.bbsim;

import java.util.Map;
import java.util.Random;

import com.bbsim.ApiQuery.BattingSplit;

public class Batter extends Player
{
	private Map<BattingSplitType, BattingSplit> splits;
	
	private int stolenBases, caughtStealing;
	private float flyoutChance;
	private float gidpChance;
	
	private int igHits = 0;
	private int igBases = 0;
	private int igRBI = 0;
	private int igRuns = 0;
	private int igHomers = 0;
	
	public Batter(String name, String playerId, StateVar homeAway) {
		super(name, playerId, homeAway);
	}
	
	public void setBattingSplits(Map<BattingSplitType, BattingSplit> splits) {
		this.splits = splits;
	}
	
	public void setStealingData(int stolenBases, int caughtStealing) {
		this.stolenBases = stolenBases;
		this.caughtStealing = caughtStealing;
	}
	
	public void setOutData(float strikeOuts, float groundOuts, float flyOuts, float gidp, float gidp_opp) {
		flyoutChance = flyOuts / (strikeOuts + groundOuts + flyOuts);
		gidpChance = gidp / gidp_opp;
	}
	
	public void setEmptyData() {
		this.ignoreStats();
	}
	
	public float getHitChance(StateVar pitcherHand) {
		return combineAvgs(getSplit(getHomeAway()).hitChance, getSplit(pitcherHand).hitChance);
	}
	
	public float getKChance(StateVar pitcherHand) {
		return combineAvgs(getSplit(getHomeAway()).kChance, getSplit(pitcherHand).kChance);
	}
	
	public float getWalkChance(StateVar pitcherHand) {
		return combineAvgs(getSplit(getHomeAway()).bbChance, getSplit(pitcherHand).bbChance);
	}
	
	public Result guessHitOutcome(StateVar pitcherHand) {
		
		if (!this.shouldInclude()) {
			return Result.SINGLE;
		}
		
		float val = getRandomFloat();
		
		float doubleChance = combineAvgs(getSplit(getHomeAway()).doubleChance, getSplit(pitcherHand).doubleChance);
		float tripleChance = combineAvgs(getSplit(getHomeAway()).tripleChance, getSplit(pitcherHand).tripleChance);
		float homerChance = combineAvgs(getSplit(getHomeAway()).homerChance, getSplit(pitcherHand).homerChance);
		
		if (val <= homerChance) {
			return Result.HOMER;
		} else if (val <= homerChance + tripleChance) {
			return Result.TRIPLE;
		} else if (val <= homerChance + tripleChance + doubleChance) {
			return Result.DOUBLE;
		} else {
			return Result.SINGLE;
		}
	}
	
	public boolean doesAttemptSteal() {
		float val = getRandomFloat();
		
		if (!this.shouldInclude()) {
			return false;
		}
		
		float tryPct = (float) (stolenBases + caughtStealing) / (float) (getSplit(getHomeAway()).singles + getSplit(getHomeAway()).bbs);
		
		if (val <= tryPct) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean doesStealSuccessfully() {
		float val = getRandomFloat();
		
		float successPct = (float) stolenBases / (float) (stolenBases + caughtStealing);
		
		if (val <= successPct) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean doesFlyout() {
		float val = getRandomFloat();
		if (val <= flyoutChance) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean doesGIDP() {
		float val = getRandomFloat();
		if (val <= gidpChance) {
			return true;
		} else {
			return false;
		}
	}
	
	public void addHit() {
		igHits++;
	}
	
	public void addBases(int i) {
		igBases += i;
	}
	
	public void addRBI(int i ) {
		igRBI += i;
	}
	
	public void addRun() {
		igRuns++;
	}
	
	public void addHomer() {
		igHomers++;
	}
	
	public int getHits() {
		return igHits;
	}
	
	public int getBases() {
		return igBases;
	}
	
	public int getRBI() {
		return igRBI;
	}
	
	public int getRuns() {
		return igRuns;
	}
	
	public int getHomers() {
		return igHomers;
	}
	
	public void endGame() {
		igHits = 0;
		igBases = 0;
		igRBI = 0;
		igRuns = 0;
		igHomers = 0;
	}
	
	private BattingSplit getSplit(StateVar var) {
		switch (var) {
			case HOME: {
				return splits.get(BattingSplitType.HOME);
			}
			case AWAY: {
				return splits.get(BattingSplitType.AWAY);
			}
			case LEFTY: {
				return splits.get(BattingSplitType.LHP);
			}
			case RIGHTY: {
				return splits.get(BattingSplitType.RHP);
			}
			default: {
				return null;
			}
		}
	}
	
	
	private float getRandomFloat() {
		Random random = new Random();
		for (int i = 0; i < 25; i++) {
			random.nextFloat();
		}
		return random.nextFloat();
	}
	
	private float combineAvgs(float avg1, float avg2) {
		return (avg1 + avg2) / 2;
	}
	
	public void print() {
		System.out.println(getName());
		if (splits != null) {
			for (BattingSplit split : splits.values()) {
				split.print();
			}
		} else {
			System.out.println("    IGNORED");
		}

	}
}
