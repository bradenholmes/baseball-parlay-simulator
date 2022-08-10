package com.bbsim.state.impl;

import com.bbsim.ApiQuery;
import com.bbsim.ApiQuery.Game;
import com.bbsim.ApiQuery.Lineups;
import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.CompleteBetSet;
import com.bbsim.GameSimulation;
import com.bbsim.Parlay;
import com.bbsim.Simularity;
import com.bbsim.SimulationData;
import com.bbsim.StateVar;
import com.bbsim.Team;
import com.bbsim.state.FunctionState;

public class SimulationState extends FunctionState
{
	
	Game simGame;
	
	SimulationData simData;
	Parlay parlay;
	
	boolean saveToMain;
	
	@Override
	public void init(Object... params) {
		
		if (params.length == 1) {
			if (!(params[0] instanceof Game)) {
				System.err.println("ERROR: SimulationState argument must be a Game object");
				return;
			}
			
			this.simGame = (Game) params[0];
			this.simData = null;
			this.parlay = null;
		} else if (params.length == 3) {
			if (!(params[0] instanceof SimulationData)) {
				System.err.println("ERROR: First SimulationState argument must be a SimulationData object");
				return;
			}
			
			if (!(params[1] instanceof Parlay)) {
				System.err.println("ERROR: Second SimulationState argument must be a Parlay object");
				return;
			}
			
			if (!(params[2] instanceof Boolean)) {
				System.err.println("ERROR: Third SimulationState argument must be a boolean");
			}
			this.simGame = null;
			this.simData = (SimulationData) params[0];
			this.parlay = (Parlay) params[1];
			this.saveToMain = (boolean) params[2];
		} else {
			System.err.println("ERROR: Wrong number of arguments passed to SimulationState init method!");
			return;
		}
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FunctionResult function() {
		if (simGame != null) {
			return preParlaySim();
		} else {
			return parlayTestSim();
		}
	}
	
	
	private FunctionResult preParlaySim() {
    	Lineups lineups = ApiQuery.getLineups(simGame.gameId);
    	if (lineups == null) {
    		System.out.println("Press enter to continue...");
    		this.getManager().getScanner().nextLine();
    		return new FunctionResult(App.MAIN_STATE);
    	}
    	
    	System.out.println("getting team statistics....");
    	Team homeTeam = new Team(StateVar.HOME, lineups.homeLineup);
    	Team awayTeam = new Team(StateVar.AWAY, lineups.awayLineup);
    	
    	int homeWins = 0;
    	int awayWins = 0;
    	
    	int totalHomeRuns = 0;
    	int totalAwayRuns = 0;
    	
    	System.out.println("simulating games....");
    	float simulations = 100000;
    	for (int i = 0; i < simulations; i++) {
            GameSimulation game = new GameSimulation(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            if (homeTeam.getRuns() > awayTeam.getRuns()) {
            	homeWins++;
            } else if (awayTeam.getRuns() > homeTeam.getRuns()) {
            	awayWins++;
            } else {
            	System.out.println("ERROR: Ties should not be possible...");
            }
            
            totalHomeRuns += homeTeam.getRuns();
            totalAwayRuns += awayTeam.getRuns();
            
            homeTeam.endGame(false);
            awayTeam.endGame(false);
    	}
    	
    	SimulationData data = new SimulationData(simGame, simulations);
    	data.setHomeTeamData(homeTeam, homeWins, totalHomeRuns);
    	data.setAwayTeamData(awayTeam, awayWins, totalAwayRuns);
    	data.setFirstInningData(awayTeam, homeTeam);
    	
    	completeBetSetSim(data);
    	
		return new FunctionResult(App.PARLAY_BUILDER_STATE, data);
	}
	
	private void completeBetSetSim(SimulationData data) {
		Team homeTeam = data.homeData.team;
		Team awayTeam = data.awayData.team;
		
		homeTeam.endGame(true);
		awayTeam.endGame(true);
		
		CompleteBetSet betSet = new CompleteBetSet(data);
		
		Parlay testParlay = new Parlay(simGame);
		for (Bet b: betSet.getAllBets()) {
			testParlay.addBet(b);
		}
		
		
    	System.out.println("simulating all bets....");
    	float simulations = 100000;
    	for (int i = 0; i < simulations; i++) {
            GameSimulation game = new GameSimulation(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            testParlay.evaluate(game);
            
            homeTeam.endGame(true);
            awayTeam.endGame(true);
    	}
    	
    	testParlay.endEvaluation();
    	
    	Simularity.saveBetPredictions(betSet);
    	data.setCompleteBetSet(betSet);
    	
	}
	
	private FunctionResult parlayTestSim() {
		Team homeTeam = simData.homeData.team;
		Team awayTeam = simData.awayData.team;
		
		homeTeam.endGame(true);
		awayTeam.endGame(true);
		
    	System.out.println("simulating games....");
    	float simulations = 100000;
    	for (int i = 0; i < simulations; i++) {
            GameSimulation game = new GameSimulation(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            parlay.evaluate(game);
            
            homeTeam.endGame(true);
            awayTeam.endGame(true);
    	}
    	
    	parlay.endEvaluation();
    	
    	if (saveToMain) {
    		return new FunctionResult(App.MAIN_STATE, parlay);
    	} else {
    		return new FunctionResult(App.PARLAY_BUILDER_STATE, parlay);
    	}
    	
	}

}
