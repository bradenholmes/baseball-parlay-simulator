package com.bbsim;

public enum BetType
{
	MONEY_LINE(true), RUN_LINE(true), SO_OVER, ONE_HIT, TWO_HIT, TWO_BASES, THREE_BASES, HOME_RUN, RBI, RUN;
	
	private boolean isTeamBet;
	
	BetType(){
		this.isTeamBet = false;
	}
	
	BetType(boolean isTeamBet) {
		this.isTeamBet = isTeamBet;
	}
	
	public boolean isTeamBet() {
		return isTeamBet;
	}
}
