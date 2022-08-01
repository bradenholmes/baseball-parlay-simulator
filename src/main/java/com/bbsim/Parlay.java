package com.bbsim;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Parlay
{
	List<Bet> bets;
	
	public Parlay() {
		this.bets = new ArrayList<>();
	}
	
	public void addBet(Bet bet) {
		bets.add(bet);
	}
	
	public boolean isEmpty() {
		return bets.isEmpty();
	}

	
	public void print() {
		for (Bet b : bets) {
			b.print();
		}
	}
}
