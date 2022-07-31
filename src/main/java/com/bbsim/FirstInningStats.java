package com.bbsim;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FirstInningStats
{
	
	public static Map<InningScore, Integer> scoreMap = new HashMap<>();
	public static float anyOtherScore = 0;
	
	public static float totalRecords = 0;
	public static float homeWins = 0;
	public static float ties = 0;
	public static float awayWins = 0;
	
	
	public static void recordFirstInning(int homeRuns, int awayRuns) {
		if (homeRuns <= 3 && awayRuns <= 3) {
			InningScore score = new InningScore(homeRuns, awayRuns);
			scoreMap.put(score, scoreMap.getOrDefault(score, 0) + 1);
		} else {
			anyOtherScore++;
		}
		

		
		totalRecords++;
		if (homeRuns > awayRuns) {
			homeWins++;
		} else if (homeRuns < awayRuns) {
			awayWins++;
		} else {
			ties++;
		}
	}
	
	public static void printFirstInningResults() {
		System.out.println(" ~~~~~~~~~~~~~~~~~~~~~~~FIRST INNING RESULTS~~~~~~~~~~~~~~~~~~~~~~~~ ");
		System.out.println();
		System.out.println("Home team wins: " + App.percentage(homeWins / totalRecords));
		System.out.println("           Tie: " + App.percentage(ties / totalRecords));
		System.out.println("Away team wins: " + App.percentage(awayWins / totalRecords));
		System.out.println();
		System.out.println("Score distribution: ");
		for (Entry<InningScore, Integer> e : scoreMap.entrySet()) {
			System.out.println("(h: " + e.getKey().getHomeScore() + ", a: " + e.getKey().getAwayScore() + ") occurs " + App.percentage(e.getValue() / totalRecords));
		}
		
	}
	
	
}
