package com.bbsim;

import java.util.List;

import com.bbsim.ApiQuery.Game;
import com.bbsim.ApiQuery.Lineups;
import com.bbsim.state.StateManager;
import com.bbsim.state.impl.BetClassState;
import com.bbsim.state.impl.BetSubjectState;
import com.bbsim.state.impl.BetTypeState;
import com.bbsim.state.impl.BetValueState;
import com.bbsim.state.impl.GamePickerState;
import com.bbsim.state.impl.MainState;
import com.bbsim.state.impl.OtherState;
import com.bbsim.state.impl.ParlayBuilderState;
import com.bbsim.state.impl.SimulationState;

/**
 * Hello world!
 *
 */
public class App 
{
	//Screen states
	public static final String MAIN_STATE = "main_state";
	public static final String OTHER_STATE = "other_state";
	public static final String GAME_PICKER_STATE = "game_picker_state";
	public static final String PARLAY_BUILDER_STATE = "parlay_builder_state";
	public static final String BET_CLASS_STATE = "bet_class_state";
	public static final String BET_TYPE_STATE = "bet_type_state";
	public static final String BET_SUBJECT_STATE = "bet_subject_state";
	public static final String BET_VALUE_STATE = "bet_value_state";
	
	//Function states
	public static final String SIMULATION_STATE = "simulation_state";
	
	private static StateManager manager;
	private static List<Parlay> parlays;

    public static void main( String[] args )
    {
    	initialize();
    	

    	//clearConsole();
    	
    	
    	
    	
    	
    	/*
    	Lineups lineups = ApiQuery.getLineups("662988");
    	
    	System.out.println("-------- TEAMS -------");
    	Team homeTeam = new Team(StateVar.HOME, lineups.homeLineup);
    	homeTeam.print();
    	Team awayTeam = new Team(StateVar.AWAY, lineups.awayLineup);
    	awayTeam.print();
    	
    	int homeWins = 0;
    	int awayWins = 0;
    	
    	int totalHomeRuns = 0;
    	int totalAwayRuns = 0;
    	
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
    	
    	
    	ParlayBuilder parlayBuilder = new ParlayBuilder(simulations);
    	parlayBuilder.setHomeTeamData(homeTeam, homeWins, totalHomeRuns);
    	parlayBuilder.setHomePitcher(homeTeam.getPitcher());
    	parlayBuilder.setHomeBatters(homeTeam.getBatters());
    	
    	parlayBuilder.setAwayTeamData(awayTeam, awayWins, totalAwayRuns);
    	parlayBuilder.setAwayPitcher(awayTeam.getPitcher());
    	parlayBuilder.setAwayBatters(awayTeam.getBatters());
    	
    	parlayBuilder.printParlayBuilderData();
    	System.out.println();
    	
    	
    	Parlay basicParlay = parlayBuilder.build(ParlayLevel.BASIC);
    	Parlay aggressiveParlay = parlayBuilder.build(ParlayLevel.AGGRESSIVE);
    	Parlay conservativeParlay = parlayBuilder.build(ParlayLevel.CONSERVATIVE);

    	for (int i = 0; i < simulations; i++) {
            GameSimulation game = new GameSimulation(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            basicParlay.evaluate();
            aggressiveParlay.evaluate();
            conservativeParlay.evaluate();
            
            homeTeam.endGame(true);
            awayTeam.endGame(true);
    	}
    	
    	basicParlay.print();
    	conservativeParlay.print();
    	aggressiveParlay.print();
    	
    	Parlay prunedParlay = parlayBuilder.pruneParlay(aggressiveParlay, 0.53f);
    	for (int i = 0; i < simulations; i++) {
            GameSimulation game = new GameSimulation(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            prunedParlay.evaluate();
            
            homeTeam.endGame(true);
            awayTeam.endGame(true);
    	}
    	
    	prunedParlay.print();
    	
    	System.out.println();
    	
    	FirstInningStats.printFirstInningResults();
    	*/

    }
    
    public static void initialize() {
    	StateManager manager = new StateManager();
    	manager.addState(MAIN_STATE, new MainState());
    	manager.addState(OTHER_STATE, new OtherState());
    	manager.addState(GAME_PICKER_STATE, new GamePickerState(ApiQuery.getAllGames()));
    	manager.addState(PARLAY_BUILDER_STATE, new ParlayBuilderState());
    	manager.addState(BET_CLASS_STATE, new BetClassState());
    	manager.addState(BET_TYPE_STATE, new BetTypeState());
    	manager.addState(BET_SUBJECT_STATE, new BetSubjectState());
    	manager.addState(BET_VALUE_STATE, new BetValueState());
    	
    	manager.addState(SIMULATION_STATE, new SimulationState());
    	
    	manager.activateManager(MAIN_STATE);
    }
    
    public static String percentage(float rate) {
    	return String.format("%.2f%%", 100 * rate);
    }
    
    public static String decimal(float val) {
    	return String.format("%.2f", val);
    }
    
    public static void clearConsole() {
    	try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
