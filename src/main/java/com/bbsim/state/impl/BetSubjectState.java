package com.bbsim.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.App;
import com.bbsim.Batter;
import com.bbsim.Bet;
import com.bbsim.BetClass;
import com.bbsim.Pitcher;
import com.bbsim.SimulationData;
import com.bbsim.SimulationData.BatterData;
import com.bbsim.Team;
import com.bbsim.state.ScreenState;

public class BetSubjectState extends ScreenState
{
	SimulationData simData;
	Bet bet;
	List<Batter> batters;
	@Override
	public void init(Object... params) {
		clearConsole(); 
		
		if (params.length != 2) {
			System.err.println("ERROR: Wrong number of arguments passed to BetSubjectState init method!");
			return;
		}
		
		if (!(params[0] instanceof SimulationData)) {
			System.err.println("ERROR: First BetSubjectState argument must be a SimulationData object");
			return;
		}
		
		if (!(params[1] instanceof Bet)) {
			System.err.println("ERROR: Second BetSubjectState argument must be a Bet object");
			return;
		}
		this.simData = (SimulationData) params[0];
		this.bet = (Bet) params[1];
		if (this.bet.getBetClass() == BetClass.GAME) {
			bet.setSubject(simData.homeData.team, simData.awayData.team);
			this.changeState(App.BET_VALUE_STATE, bet);
		} else if (this.bet.getBetClass() == BetClass.TEAM) {
			System.out.println("For which team? (1 or 2)");
			System.out.println("  1.) " + simData.awayData.team.getName());
			System.out.println("  2.) " + simData.homeData.team.getName());
		} else if (this.bet.getBetClass() == BetClass.PITCHER) {
			System.out.println("For which pitcher? (1 or 2)");
			System.out.println("  1.) " + simData.awayPitcherData.pitcher.getName());
			System.out.println("  2.) " + simData.homePitcherData.pitcher.getName());
		} else if (this.bet.getBetClass() == BetClass.BATTER) {
			batters = new ArrayList<>();
			for (BatterData b : simData.awayBatterData) {
				batters.add(b.batter);
			}
			for (BatterData b : simData.homeBatterData) {
				batters.add(b.batter);
			}
			System.out.println("For which batter? (Type the number next to the player name.)");
			System.out.println("You can enter multiple numbers with spaces inbetween (2 3 9 12)");
			for (int i = 0; i < batters.size(); i++) {
				Bet b = simData.completeBetSet.getBatterBet(bet.getBetType(), batters.get(i).getPlayerId());
				String betProb = b == null ? "" : App.percentage(b.getExpectedProbability());
				System.out.println(StringUtils.leftPad("  " + i + ".) ", 8) + StringUtils.rightPad(batters.get(i).getName(), 17) + "   -- " + betProb);
			}
		}
		
		System.out.println("or type 'back'");
	}

	@Override
	public void end()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInput(String input)
	{
		if ("back".equals(input)) {
			this.changeState(App.PARLAY_BUILDER_STATE);
			return;
		}
		
		try {
			if (this.bet.getBetClass() == BetClass.TEAM) {
				chooseTeamBet(input, simData.awayData.team, simData.homeData.team);
				return;
			} else if (this.bet.getBetClass() == BetClass.PITCHER) {
				choosePitcherBet(input, simData.awayPitcherData.pitcher, simData.homePitcherData.pitcher);
				return;
			} else if (this.bet.getBetClass() == BetClass.BATTER) {
	
				List<Bet> batBets = new ArrayList<>();
				List<Integer> inputs = this.getManager().handleManyIntegerInput(input, 0, batters.size(), true);
				if (inputs == null) {
					this.changeState(App.PARLAY_BUILDER_STATE);
					return;
				}
				for (Integer in : inputs) {
					Bet batBet = new Bet(bet.getBetClass(), bet.getBetType());
					batBet.setSubject(batters.get(in));
					batBets.add(batBet);
				}
				
				this.changeState(App.PARLAY_BUILDER_STATE, batBets);
				return;
			}
		} catch (Exception e) {
			System.out.println("Check your inputs and try again");
		}
	}
	
	private void chooseTeamBet(String input, Team awayTeam, Team homeTeam) throws Exception {
		
		int inVal = this.getManager().handleIntegerInput(input, 1, 3, true);
		
		if (inVal == App.UNSET_INT) {
			this.changeState(App.PARLAY_BUILDER_STATE);
			return;
		}
		
		if (inVal == 1) {
			bet.setSubject(simData.awayData.team, simData.homeData.team);
		} else if (inVal == 2) {
			bet.setSubject(simData.homeData.team, simData.awayData.team);
		}
		
		if (bet.requiresValue()) {
			this.changeState(App.BET_VALUE_STATE, bet);
			return;
		} else {
			this.changeState(App.PARLAY_BUILDER_STATE, bet);
			return;
		}
	}
	
	private void choosePitcherBet(String input, Pitcher awayPitcher, Pitcher homePitcher) throws Exception {
		int inVal = this.getManager().handleIntegerInput(input, 1, 3, true);
		
		if (inVal == App.UNSET_INT) {
			this.changeState(App.PARLAY_BUILDER_STATE);
			return;
		}
		
		if (inVal == 1) {
			bet.setSubject(simData.awayPitcherData.pitcher);
		} else if (inVal == 2) {
			bet.setSubject(simData.homePitcherData.pitcher);
		}
		
		if (bet.requiresValue()) {
			this.changeState(App.BET_VALUE_STATE, bet);
			return;
		} else {
			this.changeState(App.PARLAY_BUILDER_STATE, bet);
			return;
		}
	}

}
