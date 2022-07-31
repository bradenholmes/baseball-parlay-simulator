package com.bbsim;

public class Bet
{
	boolean isTeamBet;
	Team favorite;
	Team underdog;
	Player player;
	
	BetType type;
	float value;
	
	private int attempts = 0;
	private int wins = 0;
	
	public Bet(BetType type, float value, Player player) {
		this.type = type;
		this.value = value;
		
		if (type.isTeamBet()) {
			System.err.println(type + " is a team bet!");
		}
	
		this.isTeamBet = false;
		this.favorite = null;
		this.underdog = null;
		this.player = player;
	}
	
	public Bet(BetType type, float value, Team favorite, Team underdog) {
		this.type = type;
		this.value = value;
		
		if (!type.isTeamBet()) {
			System.err.println(type + " is a player bet!");
		}
		
		this.isTeamBet = true;
		this.favorite = favorite;
		this.underdog = underdog;
		this.player = null;
	}
	
	public boolean evaluate() {
		switch (type) {
			case MONEY_LINE:
				if (favorite.getRuns() >= underdog.getRuns()) {
					return true;
				}
				break;
			case RUN_LINE:
				if (favorite.getRuns() + value > underdog.getRuns()) {
					return true;
				}
				break;
			case SO_OVER:
				if (((Pitcher) player).getSOs() >= value) {
					return true;
				}
				break;
			case ONE_HIT:
				if (((Batter) player).getHits() >= 1) {
					return true;
				}
				break;
			case TWO_HIT:
				if (((Batter) player).getHits() >= 2) {
					return true;
				}
				break;
			case TWO_BASES:
				if (((Batter) player).getBases() >= 2) {
					return true;
				}
				break;
			case THREE_BASES:
				if (((Batter) player).getBases() >= 3) {
					return true;
				}
				break;
			case HOME_RUN:
				if (((Batter) player).getHomers() >= 1) {
					return true;
				}
				break;
			case RBI:
				if (((Batter) player).getRBI() >= 1) {
					return true;
				}
				break;
			case RUN:
				if (((Batter) player).getRuns() >= 1) {
					return true;
				}
				break;
		}
		
		return false;
	}
	
	public void recordResult(boolean win) {
		attempts++;
		if (win) {
			wins++;
		}
	}
	
	public float getWinPct() {
		return (float) wins / (float) attempts;
	}
	
	
}
