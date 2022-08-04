package com.bbsim;

public enum FirstInningBet
{
	ZERO_ZERO, HOME_WIN, TIE, AWAY_WIN;
	
	public static FirstInningBet ofOridinal(int i) {
		for (FirstInningBet b : FirstInningBet.values()) {
			if (b.ordinal() == i) {
				return b;
			}
		}
		
		return null;
	}
}
