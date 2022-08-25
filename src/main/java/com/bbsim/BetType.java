package com.bbsim;

import java.util.ArrayList;
import java.util.List;

public enum BetType
{
	MONEY_LINE(BetClass.TEAM, false, 1), RUN_LINE(BetClass.TEAM, true, 2), 
	RUNS_OVER(BetClass.GAME, true, 3), RUNS_UNDER(BetClass.GAME, true, 4), FIRST_INNING(BetClass.GAME, true, 5),
	SO_OVER(BetClass.PITCHER, true, 6), SO_UNDER(BetClass.PITCHER, true, 6),
	ONE_HIT(BetClass.BATTER, false, 13), TWO_HIT(BetClass.BATTER, false, 10), TWO_BASES(BetClass.BATTER, false, 9), 
	THREE_BASES(BetClass.BATTER, false, 8), HOME_RUN(BetClass.BATTER, false, 7), RBI(BetClass.BATTER, false, 12), RUN(BetClass.BATTER, false, 11);
	
	
	final BetClass betClass;
	final boolean requireValue;
	final int displayPriority;
	
	BetType(BetClass betClass, boolean reqValue, int displayPriority){
		this.betClass = betClass;
		this.requireValue = reqValue;
		this.displayPriority = displayPriority;
	}
	
	
	public static List<BetType> getTypesOfClass(BetClass betClass) {
		List<BetType> types = new ArrayList<>();
		for (BetType t : BetType.values()) {
			if (t.getBetClass() == betClass) {
				types.add(t);
			}
		}
		
		return types;
	}
	
	public static boolean isPlayerBet(BetType type) {
		if (type == MONEY_LINE || type == RUN_LINE || type == RUNS_OVER || type == RUNS_UNDER || type == FIRST_INNING) {
			return false;
		} else {
			return true;
		}
	}
	
	public BetClass getBetClass() {
		return betClass;
	}
	
	public boolean doesRequireValue() {
		return requireValue;
	}
	
	public int getDisplayPriority() {
		return displayPriority;
	}
}
