package com.bbsim.state.impl;

import com.bbsim.ApiQuery;
import com.bbsim.ApiQuery.Game;
import com.bbsim.ApiQuery.Lineups;
import com.bbsim.App;
import com.bbsim.GameSimulation;
import com.bbsim.SimulationData;
import com.bbsim.StateVar;
import com.bbsim.Team;
import com.bbsim.state.FunctionState;

public class SimulationState extends FunctionState
{
	
	Game simGame;
	
	@Override
	public void init(Object... params) {
		if (params.length != 1) {
			System.err.println("ERROR: Wrong number of arguments passed to SimulationState init method!");
			return;
		}
		
		if (!(params[0] instanceof Game)) {
			System.err.println("ERROR: SimulationState argument must be a Game object");
			return;
		}
		
		this.simGame = (Game) params[0];
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FunctionResult function() {
    	Lineups lineups = ApiQuery.getLineups(simGame.gameId);
    	
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
            } else {
            	awayWins++;
            }
            
            totalHomeRuns += homeTeam.getRuns();
            totalAwayRuns += awayTeam.getRuns();
            
            homeTeam.endGame(false);
            awayTeam.endGame(false);
    	}
    	
    	SimulationData data = new SimulationData(simulations);
    	data.setHomeTeamData(homeTeam, homeWins, totalHomeRuns);
    	data.setAwayTeamData(awayTeam, awayWins, totalAwayRuns);
    	
		return new FunctionResult(App.PARLAY_BUILDER_STATE, data);
	}

}
