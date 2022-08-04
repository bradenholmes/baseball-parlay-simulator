package com.bbsim.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
			if (parlay == null) {
				parlay = new Parlay(simData.simGame);
			} else {
				if (parlay.getGame().gameId == simData.simGame.gameId) {
					List<Bet> prevBets = parlay.getBets();
					parlay = new Parlay(simData.simGame);
					for (Bet b : prevBets) {
						parlay.addBet(b);
					}
				} else {
					parlay = new Parlay(simData.simGame);
				}

			}

		} else if (params[0] instanceof Bet) {
			parlay.addBet((Bet) params[0]);
		} else if (params[0] instanceof ArrayList<?>) {
			for (Object b : ((ArrayList<?>) params[0])) {
				if (b instanceof Bet) {
					parlay.addBet((Bet)b);
				}
			}
		} else if (params[0] instanceof Parlay) {
			this.parlay = (Parlay) params[0];
		} else {
			System.err.println("ERROR: ParlayBuilderState argument must be a SimulationData, Bet, or Parlay object");
			return;
		}
	}

	@Override
	public void end() {
	}

	@Override
	public void update() {
		clearConsole();
		if (simData != null) {
			simData.printSimulationData();
		} else {
			System.out.println(App.TABLE_END_LINE);
			System.out.println("no simulation data present... type 'simulate' to run simulations");
			System.out.println(App.TABLE_HORIZ_LINE);
		}
		
		System.out.println(App.TABLE_HORIZ_LINE);
		System.out.println(App.centerText("GAME PARLAY", false, true));
		System.out.println(App.TABLE_HORIZ_LINE);
		if (parlay.isEmpty()) {
			System.out.println(App.centerText("no bets :(", false, true));
			System.out.println(App.centerText("Type 'add' to add a bet to this parlay, or 'discard'", false, true));
		} else {
			parlay.print();
			System.out.println(App.TABLE_EMPTY_LINE);
			System.out.println(App.leftJustifyText("options:", 2, true));
			if (simData == null) {
				System.out.println(App.leftJustifyText(StringUtils.leftPad("'simulate'", 10) + "- run simulations", 2, true));
			}
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'add'", 10) + "- add a bet", 2, true));
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'remove'", 10) + "- remove a bet", 2, true));
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'test'", 10) + "- simulate parlay", 2, true));
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'save'", 10) + "- save parlay", 2, true));
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'discard'", 10) + "- discard parlay", 2, true));
		}
		
		System.out.println(App.TABLE_END_LINE);
	}

	@Override
	public void handleInput(String input) {
		if ("discard".equals(input)){
			System.out.println("Are you sure you want to discard this parlay? (y/n)");
			String answer = this.getManager().getScanner().nextLine();
			if ("y".equals(answer)) {
				this.changeState(App.MAIN_STATE);
			}
			return;
		} else if ("simulate".equals(input)) {
			parlay.resetEvaluation();
			this.changeState(App.SIMULATION_STATE, parlay.getGame());
			return;
		} else if ("add".equals(input)) {
			this.changeState(App.BET_CLASS_STATE, simData);
			return;
		} else if ("test".equals(input)) {
			this.changeState(App.SIMULATION_STATE, simData, parlay, false);
			return;
		} else if ("save".equals(input)) {
			this.changeState(App.SIMULATION_STATE, simData, parlay, true);
			return;
		} else {
			System.out.println("unknown input!");
			return;
		}
	}

}
