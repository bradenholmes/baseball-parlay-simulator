package com.bbsim;

public class Bet
{
	BetClass betClass;
	BetType type;
	
	float value;
	
	//Subjects
	Team favorite;
	Team underdog;
	Batter batter;
	Pitcher pitcher;
	

	
	public Bet(BetClass betClass, BetType type) {
		this.type = type;
		this.betClass = betClass;
	}
	
	public void setSubject(Team favorite, Team underdog) {
		this.favorite = favorite;
		this.underdog = underdog;
		this.batter = null;
		this.pitcher = null;
	}
	
	public void setSubject(Batter batter) {
		this.batter = batter;
		this.pitcher = null;
		this.favorite = null;
		this.underdog = null;
	}
	
	public void setSubject(Pitcher pitcher) {
		this.pitcher = pitcher;
		this.batter = null;
		this.favorite = null;
		this.underdog = null;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public BetClass getBetClass() {
		return this.betClass;
	}
	
	public boolean requiresValue() {
		return this.type.doesRequireValue();
	}
	
	public void print() {
		System.out.println("A " + type + " BET");
	}
	
	
	
}
