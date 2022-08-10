package com.bbsim;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbsim.CurrentGameData.BatterStats;
import com.bbsim.CurrentGameData.GameStats;
import com.bbsim.CurrentGameData.PitcherStats;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Simularity
{
	public class FinalBoxScore {
		private GameStats gameStats;
		private BatterStats[] awayBatterStats;
		private BatterStats[] homeBatterStats;
		private PitcherStats awayPitcherStats;
		private PitcherStats homePitcherStats;
		
		private FinalBoxScore(CurrentGameData gameData) {
			this.gameStats = gameData.gameStats;
			this.awayBatterStats = gameData.awayBatterStats;
			this.homeBatterStats = gameData.homeBatterStats;
			this.awayPitcherStats = gameData.awayPitcherStats;
			this.homePitcherStats = gameData.homePitcherStats;
		}
	}
	
	public class BetPredictions {
		List<SimpleBet> bets;
		
		private BetPredictions(CompleteBetSet betSet) {
			bets = new ArrayList<>();
			for (Bet b : betSet.getAllBets()) {
				switch (b.getBetClass()) {
					case GAME:
						bets.add(new SimpleBet(null, b.getBetType(), b.value, b.expectedProbability));
						break;
					case TEAM:
						bets.add(new SimpleBet(null, b.getBetType(), b.value, b.expectedProbability));
						break;
					case PITCHER:
						bets.add(new SimpleBet(b.pitcher.getPlayerId(), b.getBetType(), b.value, b.expectedProbability));
						break;
					case BATTER:
						bets.add(new SimpleBet(b.batter.getPlayerId(), b.getBetType(), b.value, b.expectedProbability));
						break;
				}
			}
		}
	}
	
	public class SimpleBet {
		String id;
		BetType t;
		float v;
		float p;
		
		SimpleBet(String id, BetType type, float value, float prob) {
			this.id = id;
			this.t = type;
			this.v = value;
			this.p = prob;
		}
	}
	
	public class PredictionOutcomePair {
		String gameId;
		BetPredictions betProbs;
		FinalBoxScore boxScore;
		
		float simularity;
		
		private PredictionOutcomePair(String gameId) {
			this.gameId = gameId;
		}
		
		private void setBetPredictions(BetPredictions betPredictions) {
			this.betProbs = betPredictions;
		}
		
		private void setFinalBoxScore(FinalBoxScore boxScore) {
			this.boxScore = boxScore;
		}
	}
	private static final String OUTCOME_SAVE_FILE = "/Programming/GameWorkspace/BaseballSimulator/simularityDatabank.json";
	
	private static Simularity instance = null;
	
	private Gson gson;
	private Map<String, PredictionOutcomePair> allOutcomePairs;
	
	private Simularity() {
		gson = new Gson();
		try {
			loadDatabank();
		} catch (Exception e) {
			System.err.println(e.getMessage() + "... instantiating an empty file");
			allOutcomePairs = new HashMap<>();
		}
	}
	
	public static Simularity get() {
		if (instance == null) {
			instance = new Simularity();
		}
		return instance;
	}
	
	public static void saveFinalScore(CurrentGameData gameData) {
		get().saveScore(gameData);
	}
	
	public static void saveBetPredictions(CompleteBetSet betSet) {
		get().saveBets(betSet);
	}
	
	public static void cleanup() {
		get().cleanDatabank();
	}
	
	private void saveScore(CurrentGameData gameData) {
		PredictionOutcomePair pair = allOutcomePairs.getOrDefault(gameData.game.gameId, null);
		if (pair == null) {
			pair = new PredictionOutcomePair(gameData.game.gameId);
			allOutcomePairs.put(gameData.game.gameId, pair);
		}
		
		pair.setFinalBoxScore(new FinalBoxScore(gameData));
		saveDatabank();
	}
	
	private void saveBets(CompleteBetSet betSet) {
		PredictionOutcomePair pair = allOutcomePairs.getOrDefault(betSet.getGameId(), null);
		if (pair == null) {
			pair = new PredictionOutcomePair(betSet.getGameId());
			allOutcomePairs.put(betSet.getGameId(), pair);
		}
		
		pair.setBetPredictions(new BetPredictions(betSet));
		saveDatabank();
	}
	
	
	private void loadDatabank() throws Exception{
		try {
			System.out.print("Loading databank file...  ");
			BufferedReader reader = new BufferedReader(new FileReader(OUTCOME_SAVE_FILE));
			String data = reader.readLine();
			Type outcomeMapType = new TypeToken<Map<String, PredictionOutcomePair>>() {}.getType();
			allOutcomePairs = gson.fromJson(data, outcomeMapType);

			reader.close();
			System.out.println("DONE!");
			
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void saveDatabank() {
		try {
			String data = gson.toJson(allOutcomePairs);
			FileOutputStream outputStream = new FileOutputStream(OUTCOME_SAVE_FILE);
			byte[] strBytes = data.getBytes();
			outputStream.write(strBytes);
			outputStream.close();
		} catch (Exception e) {
			System.err.println("ERROR: Failed to save databank file");
			e.printStackTrace();
		}
	}
	
	private void cleanDatabank() {
		List<String> removeList = new ArrayList<>();
		for (PredictionOutcomePair pair : allOutcomePairs.values()) {
			if (pair.betProbs == null || pair.boxScore == null) {
				removeList.add(pair.gameId);
			}
		}
		
		for (String gameId : removeList) {
			allOutcomePairs.remove(gameId);
		}
		
		saveDatabank();
	}
}
