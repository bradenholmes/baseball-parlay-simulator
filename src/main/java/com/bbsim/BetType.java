package com.bbsim;

import java.util.ArrayList;
import java.util.List;

public enum BetType
{
	MONEY_LINE(BetClass.TEAM, false), RUN_LINE(BetClass.TEAM, true), SO_OVER(BetClass.PITCHER, true),
	ONE_HIT(BetClass.BATTER, false), TWO_HIT(BetClass.BATTER, false), TWO_BASES(BetClass.BATTER, false), 
	THREE_BASES(BetClass.BATTER, false), HOME_RUN(BetClass.BATTER, false), RBI(BetClass.BATTER, false), RUN(BetClass.BATTER, false),
	FIRST_INNING(BetClass.GAME, true), RUNS_OVER(BetClass.GAME, true), RUNS_UNDER(BetClass.GAME, true);
	
	final BetClass betClass;
	final boolean requireValue;
	
	BetType(BetClass betClass, boolean reqValue){
		this.betClass = betClass;
		this.requireValue = reqValue;
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
	
	public BetClass getBetClass() {
		return betClass;
	}
	
	public boolean doesRequireValue() {
		return requireValue;
	}
}
