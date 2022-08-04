package com.bbsim.state.impl;

import com.bbsim.App;
import com.bbsim.BetClass;
import com.bbsim.SimulationData;
import com.bbsim.state.ScreenState;

public class BetClassState extends ScreenState
{
	SimulationData simData;
	@Override
	public void init(Object... params) {
		
		if (params.length != 1) {
			System.err.println("ERROR: Wrong number of arguments passed to BetClassState init method!");
			return;
		}
		
		if (!(params[0] instanceof SimulationData)) {
			System.err.println("ERROR: BetClassState argument must be a SimulationData object");
			return;
		}
		
		simData = (SimulationData) params[0];
		
		clearConsole();
		System.out.println("Add which class of bet?");
		System.out.println("  1. GAME");
		System.out.println("  2. TEAM");
		System.out.println("  3. PITCHER");
		System.out.println("  4. BATTER");
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
		} else if ("1".equals(input)){
			this.changeState(App.BET_TYPE_STATE, simData, BetClass.GAME);
			return;
		} else if ("2".equals(input)){
			this.changeState(App.BET_TYPE_STATE, simData, BetClass.TEAM);
			return;
		} else if ("3".equals(input)){
			this.changeState(App.BET_TYPE_STATE, simData, BetClass.PITCHER);
			return;
		} else if ("4".equals(input)){
			this.changeState(App.BET_TYPE_STATE, simData, BetClass.BATTER);
			return;
		}
	}

}
