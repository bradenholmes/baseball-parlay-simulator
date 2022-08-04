package com.bbsim.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.App;
import com.bbsim.Batter;
import com.bbsim.Bet;
import com.bbsim.BetClass;
import com.bbsim.SimulationData;
import com.bbsim.SimulationData.BatterData;
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
			System.out.println("For which team?");
			System.out.println("  1.) " + simData.awayData.team.getName());
			System.out.println("  2.) " + simData.homeData.team.getName());
		} else if (this.bet.getBetClass() == BetClass.PITCHER) {
			System.out.println("For which pitcher?");
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
			System.out.println("For which batter?");
			for (int i = 0; i < batters.size(); i++) {
				System.out.println("  " + i + ".) " + batters.get(i).getName());
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
		
		if (this.bet.getBetClass() == BetClass.TEAM) {
			if ("1".equals(input)) {
				bet.setSubject(simData.awayData.team, simData.homeData.team);
				if (bet.requiresValue()) {
					this.changeState(App.BET_VALUE_STATE, bet);
					return;
				} else {
					this.changeState(App.PARLAY_BUILDER_STATE, bet);
					return;
				}
			} else if ("2".equals(input)) {
				bet.setSubject(simData.homeData.team, simData.awayData.team);
				if (bet.requiresValue()) {
					this.changeState(App.BET_VALUE_STATE, bet);
					return;
				} else {
					this.changeState(App.PARLAY_BUILDER_STATE, bet);
					return;
				}
			} else {
				System.out.println("Please enter 1 or 2");
			}
		} else if (this.bet.getBetClass() == BetClass.PITCHER) {
			if ("1".equals(input)) {
				bet.setSubject(simData.awayPitcherData.pitcher);
				if (bet.requiresValue()) {
					this.changeState(App.BET_VALUE_STATE, bet);
					return;
				} else {
					this.changeState(App.PARLAY_BUILDER_STATE, bet);
					return;
				}
			} else if ("2".equals(input)) {
				bet.setSubject(simData.homePitcherData.pitcher);
				if (bet.requiresValue()) {
					this.changeState(App.BET_VALUE_STATE, bet);
					return;
				} else {
					this.changeState(App.PARLAY_BUILDER_STATE, bet);
					return;
				}
			} else {
				System.out.println("Please enter 1 or 2");
			}
		} else if (this.bet.getBetClass() == BetClass.BATTER) {
			try {
				List<Bet> batBets = new ArrayList<>();
				String[] inputs = StringUtils.split(input, ' ');
				for (int i = 0; i < inputs.length; i++) {
					int idx = Integer.parseInt(inputs[i]);
					Bet batBet = new Bet(bet.getBetClass(), bet.getBetType());
					batBet.setSubject(batters.get(idx));
					batBets.add(batBet);
				}
				
				this.changeState(App.PARLAY_BUILDER_STATE, batBets);

			} catch (Exception e) {
				System.out.println("Check your inputs and try again");
			}
		}
		
		
		
	}

}
