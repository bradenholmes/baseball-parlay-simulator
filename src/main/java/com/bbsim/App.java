package com.bbsim;

import com.bbsim.ApiQuery.Lineups;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
    	
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
            Game game = new Game(homeTeam, awayTeam);
            
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
            Game game = new Game(homeTeam, awayTeam);
            
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
            Game game = new Game(homeTeam, awayTeam);
            
            while(game.step(false)) {
            	
            }
            
            prunedParlay.evaluate();
            
            homeTeam.endGame(true);
            awayTeam.endGame(true);
    	}
    	
    	prunedParlay.print();
    	
    	System.out.println();
    	
    	FirstInningStats.printFirstInningResults();

    }
    
    public static String percentage(float rate) {
    	return String.format("%.2f%%", 100 * rate);
    }
    
    public static String decimal(float val) {
    	return String.format("%.2f", val);
    }
}
