package com.bbsim.state.impl;

import java.util.List;

import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.BetClass;
import com.bbsim.BetType;
import com.bbsim.SimulationData;
import com.bbsim.state.ScreenState;

public class BetTypeState extends ScreenState
{
	private SimulationData simData;
	private BetClass betClass;
	private List<BetType> types;
	
	@Override
	public void init(Object... params) {
		clearConsole();
		
		if (params.length != 2) {
			System.err.println("ERROR: Wrong number of arguments passed to BetTypeState init method!");
			return;
		}
		
		if (!(params[0] instanceof SimulationData)) {
			System.err.println("ERROR: First BetTypeState argument must be a SimulationData object");
			return;
		}
		
		if (!(params[1] instanceof BetClass)) {
			System.err.println("ERROR: Second BetTypeState argument must be a BetClass object");
			return;
		}
		
		this.simData = (SimulationData) params[0];
		this.betClass = (BetClass) params[1];
		
		types = BetType.getTypesOfClass(this.betClass);
		
		System.out.println("Add which type of " + this.betClass + " bet?");
		for (int i = 0; i < types.size(); i++) {
			System.out.println("  " + i + "). " + types.get(i));
		}
		System.out.println("or type 'back'");
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
	}

	@Override
	public void handleInput(String input) {
		if ("back".equals(input)) {
			this.changeState(App.PARLAY_BUILDER_STATE);
			return;
		}
		
		try {
			int idx = Integer.parseInt(input);
			if (idx >= 0 && idx < types.size()) {
				Bet bet = new Bet(betClass, types.get(idx));
				this.changeState(App.BET_SUBJECT_STATE, simData, bet);
			}
		} catch (Exception e) {
			System.out.println("Unknown input!");
		}
	}

}
