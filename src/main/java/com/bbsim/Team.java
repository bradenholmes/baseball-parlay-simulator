package com.bbsim;

import com.bbsim.ApiQuery.TeamLineup;
import com.google.gson.JsonObject;


public class Team
{
	
	private static final int MINIMUM_PAS = 50;
	private static final int MINIMUM_STARTS = 3;
	
	private StateVar homeAway;
	private String name;
	transient private Pitcher pitcher;
	transient private Batter[] batters;
	
	transient private int igScore;
	transient private int firstInningScore;
	
	public Team(StateVar homeAway, TeamLineup lineup) {
		this.homeAway = homeAway;
		batters = new Batter[9];
		this.name = lineup.teamName;
		loadFromLineup(lineup);
	}
	
	public void addRuns(int runs) {
		this.igScore += runs;
	}
	
	public int getRuns() {
		return igScore;
	}
	
	public void addFirstInningRuns(int runs) {
		this.firstInningScore += runs;
	}
	
	public int getFirstInningRuns() {
		return firstInningScore;
	}
	
	public String getName() {
		return name;
	}
	
	public StateVar getHomeAway() {
		return homeAway;
	}
	
	public Pitcher getPitcher() {
		return pitcher;
	}
	
	public Batter getBatter(int number) {
		if (number < 0 || number > 8) {
			return null;
		}
		
		return batters[number];
	}
	
	public Batter[] getBatters() {
		return batters;
	}
	
	public void endGame(boolean resetPlayerStats) {
		this.igScore = 0;
		this.firstInningScore = 0;
		if (resetPlayerStats) {
			pitcher.endGame();
			for (Batter b : batters) {
				b.endGame();
			}
		}
	}
	
	
	
	private void loadFromLineup(TeamLineup lineup) {


		pitcher = new Pitcher(lineup.pitcher.name, lineup.pitcher.id, homeAway);
		JsonObject pitchingStats = ApiQuery.query(ApiQuery.API_PITCHING_ENDPOINT, "mlb", "2022", lineup.pitcher.id);
		StateVar handed = "R".equals(ApiQuery.query(ApiQuery.API_PLAYERINFO_ENDPOINT, "mlb", "2022", lineup.pitcher.id).get("throws").getAsString()) ? StateVar.RIGHTY : StateVar.LEFTY;
		pitcher.setHandedness(handed);
		if (pitchingStats == null || pitchingStats.get("gs").getAsInt() < MINIMUM_STARTS) {
			if (pitchingStats == null) {
				System.out.println("NOTE: " + pitcher.getName() + "'s stats could not be found! Check handedness....");
			} else {
				System.out.println("NOTE: " + pitcher.getName() + " has only " + pitchingStats.get("gs").getAsInt() + " starts. His stats will not be included!");
			}
			pitcher.setEmptyStats();
		} else {
			pitcher.setGameStats(pitchingStats.get("gs").getAsFloat(), pitchingStats.get("ip").getAsFloat());
			pitcher.setBatterStats(pitchingStats.get("tbf").getAsFloat(), pitchingStats.get("h").getAsFloat(), pitchingStats.get("so").getAsFloat(), pitchingStats.get("bb").getAsFloat(), pitchingStats.get("hb").getAsFloat());
		}


		for (int i = 0; i < 9; i++) {
			Batter batter = new Batter(lineup.batters[i].name, lineup.batters[i].id, homeAway);
			JsonObject battingStats = ApiQuery.query(ApiQuery.API_BATTING_ENDPOINT, "mlb", "2022", lineup.batters[i].id);

			if (battingStats != null && battingStats.get("tpa").getAsInt() > MINIMUM_PAS) {
				batter.setBattingSplits(ApiQuery.getBattingSplits(lineup.batters[i].fullId));
				batter.setStealingData(battingStats.get("sb").getAsInt(), battingStats.get("cs").getAsInt());
				batter.setOutData(battingStats.get("so").getAsInt(), battingStats.get("go").getAsInt(), battingStats.get("ao").getAsInt(), battingStats.get("gidp").getAsInt(), battingStats.get("gidp_opp").getAsInt());
			} else {
				if (battingStats != null) {
					System.out.println("NOTE: " + batter.getName() + " has only " + battingStats.get("tpa").getAsInt() + " PAs. His stats will not be included!");
				} else {
					System.out.println("NOTE: " + batter.getName() + "'s stats could not be found!");
				}
				
				batter.setEmptyData();
			}
			batters[i] = batter;
		}

	}
	
	public void print() {
		System.out.println(this.name);
		this.pitcher.print();
		for (int i = 0; i < 9; i++) {
			this.batters[i].print();
		}
		System.out.println();
	}
}
