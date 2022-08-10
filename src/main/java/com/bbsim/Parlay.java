package com.bbsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbsim.ApiQuery.Game;

public class Parlay
{
	Game game;
	List<Bet> bets;
	float expectedWinRate;
	int sportsbookOdds;
	boolean isStillAlive;
	
	
	transient private float attempts;
	transient private float parlayWins;
	transient private Map<Bet, Integer> betWins;
	
	public Parlay(Game game) {
		this.game = game;
		this.bets = new ArrayList<>();
		this.expectedWinRate = -1;
		this.isStillAlive = true;
		betWins = new HashMap<>();
	}
	
	public void setSportsbookOdds(int odds) {
		this.sportsbookOdds = odds;
	}
	
	public void addBet(Bet bet) {
		bets.add(bet);
		betWins.put(bet, 0);
		
		resetEvaluation();
	}
	
	public void removeBet(Bet bet) {
		bets.remove(bet);
		betWins.remove(bet);
		resetEvaluation();
	}
	
	public void initialize() {
		betWins = new HashMap<>();
		for (Bet b : bets) {
			betWins.put(b, 0);
		}
	}
	
	public void evaluate(GameSimulation sim) {
		attempts++;
		
		boolean hasFailed = false;
		for (Bet b : bets) {
			if (b.evaluate(sim)) {
				betWins.put(b, betWins.get(b) + 1);
			} else {
				hasFailed = true;
			}
		}
		
		if (!hasFailed) {
			parlayWins++;
		}
	}
	
	public void endEvaluation() {
		this.expectedWinRate = parlayWins / attempts;
		for (Bet b : bets) {
			b.setExpectedProbability(betWins.get(b) / attempts);
		}
	}
	
	public void resetEvaluation() {
		attempts = 0;
		parlayWins = 0;
		expectedWinRate = -1;
		for (Bet b : bets) {
			b.setExpectedProbability(-1);
			betWins.put(b, 0);
		}
	}
	
	public boolean isEmpty() {
		return bets.isEmpty();
	}
	
	public boolean isDead() {
		return !isStillAlive;
	}
	
	public float getExpectedWinRate() {
		return expectedWinRate;
	}
	
	public int getSportsbookOdds() {
		return sportsbookOdds;
	}
	
	public List<Bet> getBets(){
		return bets;
	}
	
	public Game getGame() {
		return game;
	}

	
	public void print() {
		Collections.sort(bets);
		System.out.println(App.leftJustifyText("bets:", 2, true));
		for (Bet b : bets) {
			b.print();
		}
		if (expectedWinRate != -1) {
			System.out.println(App.TABLE_EMPTY_LINE);
			System.out.println(App.leftJustifyText("Parlay win rate: " + App.percentage(expectedWinRate), 2, true));
		}
	}
	
	public void printStatus(CurrentGameData gameData, boolean printDead) {
		if (!printDead && !isStillAlive) {
			return;
		}
		
		System.out.println(App.centerText(game.awayTeam + " " + gameData.gameStats.awayScore + " @ " + gameData.gameStats.homeScore + " " + game.homeTeam, false, true));
		if (gameData.isGameLive) {
			System.out.println(App.centerText(gameData.gameStats.liveStatus, false, true));
		} else {
			System.out.println(App.centerText("PREGAME", false, true));
		}
		System.out.println(App.centerText("Actual odds: +" + this.sportsbookOdds, false, true));
		
		for (Bet b : bets) {
			if (!b.printStatus(gameData)) {
				isStillAlive = false;
			}
		}
		System.out.println(App.centerText("Expected win rate: " + App.percentage(expectedWinRate), false, true));
		System.out.println(App.TABLE_HORIZ_LINE);
	}
}
