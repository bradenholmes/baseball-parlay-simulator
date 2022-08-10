package com.bbsim;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ApiQuery
{
	public static class Game {
		public String homeTeam;
		public String awayTeam;
		public String gameId;
		
		public Game(String homeTeam, String awayTeam, String gameId) {
			this.homeTeam = homeTeam;
			this.awayTeam = awayTeam;
			this.gameId = gameId;
		}
		
		public String toString() {
			return gameId + " - " + awayTeam + " @ " + homeTeam;
		}
	}
	
	public static class PlayerNameId {
		public String name;
		public String id;
		public String fullId;
		
		public PlayerNameId(String fullId) {
			this.fullId = fullId;
			
			String[] parts = StringUtils.split(fullId, '-');
			StringBuilder nameBuilder = new StringBuilder();
			for(int i = 0; i < parts.length - 1; i ++) {
				nameBuilder.append(StringUtils.capitalize(parts[i]));
				nameBuilder.append(" ");
			}
			String playerName = StringUtils.trim(nameBuilder.toString());
			if (playerName.length() > 17) {
				nameBuilder = new StringBuilder();
				String[] nameParts = StringUtils.split(playerName, " ");
				nameBuilder.append(StringUtils.left(nameParts[0], 2));
				nameBuilder.append(". ");
				nameBuilder.append(nameParts[nameParts.length - 1]);
				playerName = nameBuilder.toString();
			}
			this.name = playerName;
			this.id = parts[parts.length - 1];
		}
	}
	
	public static class Lineups {
		public TeamLineup homeLineup;
		public TeamLineup awayLineup;
		
		public Lineups(TeamLineup homeLineup, TeamLineup awayLineup) {
			this.homeLineup = homeLineup;
			this.awayLineup = awayLineup;
		}
	}
	
	public static class TeamLineup {
		public String teamName;
		public PlayerNameId pitcher;
		public PlayerNameId[] batters;
		
		public TeamLineup(String teamName, PlayerNameId pitcher, PlayerNameId[] batters) throws Exception {
			this.teamName = teamName;
			this.pitcher = pitcher;
			if (batters.length != 9) {
				System.err.println("MORE OR LESS THAN 9 BATTERS WERE FOUND IN THE LINEUP");
			} else {
				this.batters = batters;
			}
			
			if (pitcher == null) {
				throw new Exception();
			}
			for (PlayerNameId bat : batters) {
				if (bat == null) {
					throw new Exception();
				}
			}
		}
	}
	
	public static class BattingSplit {
		BattingSplitType type;
		int pas;
		int ks;
		int bbs;
		int hits;
		int singles;
		int doubles;
		int triples;
		int homers;
		
		float kChance;
		float bbChance;
		float hitChance;
		float doubleChance;
		float tripleChance;
		float homerChance;
		
		public BattingSplit(BattingSplitType type, int pas, int ks, int bbs, int hits, int doubles, int triples, int homers) {
			this.type = type;
			this.pas = pas;
			this.ks = ks;
			this.bbs = bbs;
			this.hits = hits;
			this.doubles = doubles;
			this.triples = triples;
			this.homers = homers;
			
			calculate();
		}
		
		private void calculate() {
			this.singles = hits - doubles - triples - homers;
			this.kChance = (float) ks / (float) pas;
			this.bbChance = (float) bbs / (float) pas;
			this.hitChance = (float) hits / (float) pas;
			this.doubleChance = (float) doubles / (float) hits;
			this.tripleChance = (float) triples / (float) hits;
			this.homerChance = (float) homers / (float) hits;
		}
		
		public void print() {
			System.out.println("    " + type + " split ---   PAs: " + pas + "   Ks: " + ks + "   BBs: " + bbs + "   Hits: " + hits + "   2B: " + doubles + "   3B: " + triples + "   HR: " + homers);
		}
		
		public void addOtherSplit(BattingSplit o) {
			this.pas += o.pas;
			this.ks += o.ks;
			this.bbs += o.bbs;
			this.hits += o.hits;
			this.doubles += o.doubles;
			this.triples += o.triples;
			this.homers += o.homers;
			calculate();
		}
	}
	
	//Player stats API
	private static final String API_URL_P1 = "http://lookup-service-prod.mlb.com/json/named.";
	private static final String API_URL_P2 = ".bam?league_list_id='";
	private static final String API_URL_P3 = "'&game_type='R'&season='";
	private static final String API_URL_P4 = "'&player_id='";
	
	public static final String API_BATTING_ENDPOINT = "sport_hitting_tm";
	public static final String API_PITCHING_ENDPOINT = "sport_pitching_tm";
	public static final String API_PLAYERINFO_ENDPOINT = "player_info";
	
	
	//MLB.com call for lineups
	private static final String MLB_LINEUPS = "https://www.mlb.com/starting-lineups";
	private static final String BATTING_SPLITS_START = "https://baseballsavant.mlb.com/savant-player/";
	private static final String BATTING_SPLITS_END = "?stats=splits-r-hitting-mlb&season=2022";
	
	
	@SuppressWarnings("deprecation")
	public static JsonObject query(String endpoint, String league, String year, String playerId) {
		StringBuilder sb = new StringBuilder();
		sb.append(API_URL_P1);
		sb.append(endpoint);
		sb.append(API_URL_P2);
		sb.append(league);
		sb.append(API_URL_P3);
		sb.append(year);
		sb.append(API_URL_P4);
		sb.append(playerId);
		sb.append("'");
		
		try {
			URL url = new URL(sb.toString());
			URLConnection request = url.openConnection();
			request.connect();
			
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			
			JsonObject queryResults = root.getAsJsonObject().getAsJsonObject(endpoint).getAsJsonObject("queryResults");
			
			if (queryResults == null) {
				return null;
			}
			
	    	JsonObject stats;
	    	if (queryResults.get("row") instanceof JsonArray) {
	    		JsonArray rows = queryResults.getAsJsonArray("row");
	    		stats = rows.get(rows.size() - 1).getAsJsonObject();
	    	} else {
	    		stats = queryResults.getAsJsonObject("row");
	    	}
			
			return stats;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Game> getAllGames() {
		try {
			URL url = new URL(MLB_LINEUPS);
			URLConnection request = url.openConnection();
			request.connect();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");
			}
			in.close();
			
			
			List<Game> games = new ArrayList<>();
			
			Document doc = Jsoup.parse(sb.toString());
			Elements matchups = doc.getElementsByClass("starting-lineups__matchup ");
			Iterator<Element> it = matchups.iterator();
			while(it.hasNext()) {
				Element e = it.next();
				String id = e.attr("data-gamepk");
				String homeTeam = e.getElementsByClass("starting-lineups__team-name starting-lineups__team-name--home").text();
				String awayTeam = e.getElementsByClass("starting-lineups__team-name starting-lineups__team-name--away").text();
				
				games.add(new Game(homeTeam, awayTeam, id));
			}

			return games;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Lineups getLineups(String gameId) {
		
		try {
			URL url = new URL(MLB_LINEUPS);
			URLConnection request = url.openConnection();
			request.connect();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");
			}
			in.close();
			
			Document doc = Jsoup.parse(sb.toString());
			Elements games = doc.getElementsByClass("starting-lineups__matchup ");
			Iterator<Element> it = games.iterator();
			Element gameElement = null;
			while(it.hasNext()) {
				Element e = it.next();
				if (gameId.equals(e.attributes().get("data-gamepk"))) {
					gameElement = e;
					break;
				}
			}
			
			Element teamNames = gameElement.getElementsByClass("starting-lineups__game").get(0).getElementsByClass("starting-lineups__team-names").get(0);
			String awayTeam = teamNames.getElementsByClass("starting-lineups__team-name starting-lineups__team-name--away").text();
			String homeTeam = teamNames.getElementsByClass("starting-lineups__team-name starting-lineups__team-name--home").text();
			
			Elements pitchers = gameElement.getElementsByClass("starting-lineups__pitchers").get(0).firstElementChild().getElementsByClass("starting-lineups__pitcher-summary");
			
			Element homePitElem = pitchers.get(2).getElementsByClass("starting-lineups__pitcher-name").get(0).firstElementChild();
			PlayerNameId homePitcher = new PlayerNameId(StringUtils.substringAfter(homePitElem.attr("href"), "/player/"));
			Element awayPitElem = pitchers.get(0).getElementsByClass("starting-lineups__pitcher-name").get(0).firstElementChild();
			PlayerNameId awayPitcher = new PlayerNameId(StringUtils.substringAfter(awayPitElem.attr("href"), "/player/"));

			Element teams = gameElement.getElementsByClass("starting-lineups__teams starting-lineups__teams--sm starting-lineups__teams--xl").get(0);
			Elements awayBatters = teams.getElementsByClass("starting-lineups__team  starting-lineups__team--away").get(0).getElementsByClass("starting-lineups__player");
			Elements homeBatters = teams.getElementsByClass("starting-lineups__team starting-lineups__team--home").get(0).getElementsByClass("starting-lineups__player");
			PlayerNameId[] awayBats = new PlayerNameId[9];
			PlayerNameId[] homeBats = new PlayerNameId[9];
			
			it = awayBatters.iterator();
			int i = 0;
			while(it.hasNext()) {
				Element e = it.next().firstElementChild();
				awayBats[i] = new PlayerNameId(StringUtils.substringAfter(e.attr("href"), "/player/"));
				i++;
			}
			
			it = homeBatters.iterator();
			i = 0;
			while(it.hasNext()) {
				Element e = it.next().firstElementChild();
				homeBats[i] = new PlayerNameId(StringUtils.substringAfter(e.attr("href"), "/player/"));
				i++;
			}
			
			
			
			
			TeamLineup homeLineup = new TeamLineup(homeTeam, homePitcher, homeBats);
			TeamLineup awayLineup = new TeamLineup(awayTeam, awayPitcher, awayBats);
			
			return new Lineups(homeLineup, awayLineup);
			
			
		} catch (Exception e) {
			System.out.println("Looks like the full lineups for this game have not been posted!");
			return null;
		}
	}
	
	public static Map<BattingSplitType, BattingSplit> getBattingSplits(String fullPlayerId) {
		StringBuilder sb = new StringBuilder();
		sb.append(BATTING_SPLITS_START);
		sb.append(fullPlayerId);
		sb.append(BATTING_SPLITS_END);
		
		try {
			URL url = new URL(sb.toString());
			URLConnection request = url.openConnection();
			request.connect();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			sb = new StringBuilder();
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");
			}
			in.close();
			
			Document doc = Jsoup.parse(sb.toString());
			
			Map<BattingSplitType, BattingSplit> splits = new HashMap<>();
			splits.put(BattingSplitType.LHP, extractSplit(BattingSplitType.LHP, doc));
			splits.put(BattingSplitType.RHP, extractSplit(BattingSplitType.RHP, doc));
			splits.put(BattingSplitType.HOME, extractSplit(BattingSplitType.HOME, doc));
			splits.put(BattingSplitType.AWAY, extractSplit(BattingSplitType.AWAY, doc));
			
			return splits;
		} catch (Exception e) {
			System.out.println("failed setting stats for playerId: " + fullPlayerId);
			return null;
		}
	}
	
	
	private static BattingSplit extractSplit(BattingSplitType type, Document doc) {
		Elements tableRows = doc.getElementById(type.getTableId()).getElementsByTag("tr");
		
		Iterator<Element> rowIt = tableRows.iterator();
		rowIt.next();
		Elements relevantRows = new Elements();
		while(rowIt.hasNext()) {
			Element row = rowIt.next();
			Elements vals = row.getElementsByTag("td");
			if (type.getRowSpecifier().equals(vals.get(BattingSplitType.ROW_SPECIFIER_IDX).text())) {
				relevantRows.add(row);
			}
		}
		BattingSplit split = new BattingSplit(type, 0, 0, 0, 0, 0, 0, 0);
		rowIt = relevantRows.iterator();
		while(rowIt.hasNext()) {
			Element row = rowIt.next();
			Elements vals = row.getElementsByTag("td");
			BattingSplit rowSplit = new BattingSplit(type, 
					pullStat(vals, BattingSplitType.PLATE_APP_IDX),
					pullStat(vals, BattingSplitType.STRIKE_OUT_IDX),
					pullStat(vals, BattingSplitType.WALK_IDX),
					pullStat(vals, BattingSplitType.HITS_IDX),
					pullStat(vals, BattingSplitType.DOUBLES_IDX),
					pullStat(vals, BattingSplitType.TRIPLES_IDX),
					pullStat(vals, BattingSplitType.HOMERS_IDX));
			
			split.addOtherSplit(rowSplit);
		}
		
		return split;
	}
	
	
	private static int pullStat(Elements vals, int index) {
		return Integer.parseInt(vals.get(index).text());
	}
}
