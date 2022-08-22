package com.bbsim.state.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.Constants;
import com.bbsim.Parlay;
import com.bbsim.SimulationData;
import com.bbsim.state.ScreenState;

public class ParlayBuilderState extends ScreenState
{
	SimulationData simData;
	Parlay parlay;
	
	boolean awayMoneylineWinning;
	boolean homeMoneylineWinning;
	
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
			
			awayMoneylineWinning = false;
			homeMoneylineWinning = false;
			
			if (simData.simGame.getAwayMoneylineReturn() != App.UNSET_INT) {
				float mlReturn = (simData.awayData.getWinPct() * (100 * simData.simGame.getAwayMoneylineReturn())) - ((1 - simData.awayData.getWinPct()) * 100);
				if (mlReturn > 0) {
					awayMoneylineWinning = true;
				}
			}
			if (simData.simGame.getHomeMoneylineReturn() != App.UNSET_INT) {
				float mlReturn = (simData.homeData.getWinPct() * (100 * simData.simGame.getHomeMoneylineReturn())) - ((1 - simData.homeData.getWinPct()) * 100);
				if (mlReturn > 0) {
					homeMoneylineWinning = true;
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
		
		System.out.println(App.TABLE_EMPTY_LINE);
		if (homeMoneylineWinning) {
			System.out.println(App.centerText(Constants.ANSI_GREEN + simData.simGame.homeTeam + " moneyline is a winning bet!" + Constants.ANSI_RESET, false, true));
			System.out.println(App.TABLE_EMPTY_LINE);
		}
		if (awayMoneylineWinning) {
			System.out.println(App.centerText(Constants.ANSI_GREEN + simData.simGame.awayTeam + " moneyline is a winning bet!" + Constants.ANSI_RESET, false, true));
			System.out.println(App.TABLE_EMPTY_LINE);
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
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'money'", 10) + "- set a moneyline bet", 2, true));
			System.out.println(App.leftJustifyText(StringUtils.leftPad("'discard'", 10) + "- discard parlay", 2, true));
		}
		
		System.out.println(App.TABLE_END_LINE);
	}

	@Override
	public void handleInput(String input) {
		if ("discard".equals(input)){
			if (this.getManager().askConfirmation("Are you sure you want to discard this parlay?")) {
				this.changeState(App.MAIN_STATE);
			}
			return;
		} else if ("remove".equals(input)) {
			this.clearConsole();
			System.out.println("choose bet(s) to remove:");
			for (int i = 0; i < parlay.getBets().size(); i++) {
				Bet b = parlay.getBets().get(i);
				System.out.println(StringUtils.leftPad("  " + i + ".) ", 8) + b.toString());
			}
			List<Integer> choices = this.getManager().getManyIntegerInput(0, parlay.getBets().size(), true);
			List<Bet> removedBets = new ArrayList<>();
			for (Integer c : choices) {
				removedBets.add(parlay.getBets().get(c));
			}
			for (Bet b : removedBets) {
				parlay.removeBet(b);
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
			System.out.println("Enter FanDuel odds: (or press enter to skip)");
			int sbOdds = this.getManager().getIntegerInput(-10000, 100000, true);
			if (sbOdds != App.UNSET_INT) {
				parlay.setSportsbookOdds(sbOdds);
			}
			return;
		} else if ("save".equals(input)) {
			if (parlay.getSportsbookOdds() == App.UNSET_INT) {
				System.out.println("Please input actual odds from FanDuel: ");
				int sbOdds = this.getManager().getIntegerInput(-10000, 100000, true);
				parlay.setSportsbookOdds(sbOdds);
			}
			this.changeState(App.SIMULATION_STATE, simData, parlay, true);
			return;
		} else if ("money".equals(input)) {
			System.out.println("Place your moneyline bet on which team?");
			System.out.println("  1. " + simData.awayData.team.getName());
			System.out.println("  2. " + simData.homeData.team.getName());
			int in = this.getManager().getIntegerInput(1, 3, true);
			if (in != App.UNSET_INT) {
				if (in == 1) {
					parlay.placeAwayMoneyline();
				} else if (in == 2) {
					parlay.placeHomeMoneyline();
				}
			}
		} else {
			System.out.println("unknown input!");
			return;
		}
	}

}
