package com.bbsim.state.impl;

import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.Parlay;
import com.bbsim.SimulationData;
import com.bbsim.state.ScreenState;

public class ParlayBuilderState extends ScreenState
{
	SimulationData simData;
	Parlay parlay;
	
	@Override
	public void init(Object... params) {
		if (params.length == 0) {
			return;
		}
		
		else if (params.length != 1) {
			System.err.println("ERROR: Wrong number of arguments passed to ParlayBuilderState init method!");
			return;
		}
		
		if ((params[0] instanceof SimulationData)) {
			this.simData = (SimulationData) params[0];
			parlay = new Parlay();

		} else if (params[0] instanceof Bet) {
			parlay.addBet((Bet) params[0]);
		} else {
			System.err.println("ERROR: ParlayBuilderState argument must be a SimulationData object or a Bet object");
			return;
		}
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
		clearConsole();
		simData.printSimulationData();
		System.out.println(" --------------- GAME PARLAY ---------------");
		if (parlay.isEmpty()) {
			System.out.println("no bets :(");
			System.out.println("Type 'add bet' to begin parlay creation!");
		} else {
			parlay.print();
		}
	}

	@Override
	public void handleInput(String input) {
		if ("help".equals(input)) {
			System.out.println("Type 'add bet' to add a bet, 'test' to simulate this parlay, or 'discard' to forget this parlay and return to the main page");
		} else if ("discard".equals(input)){
			this.changeState(App.MAIN_STATE);
			
		} else if ("add bet".equals(input)) {
			this.changeState(App.BET_CLASS_STATE, simData);
		} else {
			System.out.println("unknown input!");
		}
	}

}
