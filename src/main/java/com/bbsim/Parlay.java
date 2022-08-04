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
	
	private float attempts;
	private float parlayWins;
	private Map<Bet, Integer> betWins;
	
	public Parlay(Game game) {
		this.game = game;
		this.bets = new ArrayList<>();
		this.expectedWinRate = -1;
		
		betWins = new HashMap<>();
	}
	
	public void addBet(Bet bet) {
		bets.add(bet);
		betWins.put(bet, 0);
		
		resetEvaluation();
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
	
	public float getExpectedWinRate() {
		return expectedWinRate;
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
	
	public void printStatus(CurrentGameData gameData) {
		System.out.println(App.centerText(game.awayTeam + " " + gameData.gameStats.awayScore + " @ " + gameData.gameStats.homeScore + " " + game.homeTeam, false, true));
		System.out.println(App.centerText(gameData.gameStats.liveStatus, false, true));
		for (Bet b : bets) {
			b.printStatus(gameData);
		}
		System.out.println(App.TABLE_HORIZ_LINE);
	}
}
