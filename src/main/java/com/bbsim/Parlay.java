package com.bbsim;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Parlay
{
	ParlayLevel level;
	List<Bet> bets;
	
	private int wins;
	private int attempts;
	
	public Parlay(ParlayLevel level) {
		this.level = level;
		this.bets = new ArrayList<>();
	}
	
	public void addBet(Bet bet) {
		bets.add(bet);
	}
	
	public void evaluate() {
		attempts++;
		boolean win = true;
		for (Bet b : bets) {
			boolean r = b.evaluate();
			if (r == false) {
				win = false;
			}
			b.recordResult(r);
		}
		
		if (win) {
			wins++;
		}
	}
	
	public ParlayLevel getLevel() {
		return level;
	}
	
	public void print() {
		System.out.println("------------------");
		System.out.println(level);
		for (Bet b : bets) {
			if (b.isTeamBet) {
				if (b.type == BetType.MONEY_LINE) {
					System.out.println(StringUtils.leftPad(b.favorite.getName() + " win: ", 30) + App.percentage(b.getWinPct()));
				} else {
					System.out.println(StringUtils.leftPad(b.favorite.getName() + (b.value > 0 ? " + " : " ") + b.value + ": ", 30) + App.percentage(b.getWinPct()));
				}
				
			} else {
				if(b.type == BetType.SO_OVER) {
					System.out.println(StringUtils.leftPad(b.player.getName() + " " + b.value + "+ SO: ", 30) + App.percentage(b.getWinPct()));
				} else {
					System.out.println(StringUtils.leftPad(b.player.getName() + " " + b.type + ": ", 30) + App.percentage(b.getWinPct()));
				}
			}
		}
		System.out.println("PARLAY WINS: " + App.percentage((float) wins / (float) attempts) + "   (" + wins + " times)");
		System.out.println("------------------");
		System.out.println();
	}
}
