package com.bbsim;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import com.bbsim.ApiQuery.Game;

public class CurrentGameData
{
	public class GameStats {
		public transient String liveStatus;
		public int awayScore;
		public int homeScore;
		public int awayFirstScore;
		public int homeFirstScore;
		public transient int currentInning;
		
		private void set(String liveStatus, String awayScore, String homeScore, String awayFirstScore, String homeFirstScore) {
			this.liveStatus = liveStatus;
			this.awayScore = Integer.parseInt(awayScore);
			this.homeScore = Integer.parseInt(homeScore);
			
			if (StringUtils.isNotEmpty(awayFirstScore)) {
				this.awayFirstScore = Integer.parseInt(awayFirstScore);
			} else {
				this.awayFirstScore = 0;
			}
			if (StringUtils.isNotEmpty(homeFirstScore)) {
				this.homeFirstScore = Integer.parseInt(homeFirstScore);
			} else {
				this.homeFirstScore = 0;
			}
			
			if ("FINAL".equals(this.liveStatus)) {
				this.currentInning = 9;
			} else {
				this.currentInning = Integer.parseInt(StringUtils.right(this.liveStatus, 1));
			}
		}
	}
	
	public class BatterStats {
		public String playerId;
		public int hits;
		public int doubles;
		public int triples;
		public int homers;
		public int totalBases;
		public int rbi;
		public int runs;
		public transient boolean stillPlaying = true;
		
		private void set(String playerId, String hits, String doubles, String triples, String homers, String rbi, String runs) {
			this.playerId = playerId;
			this.hits = Integer.parseInt(hits);
			this.doubles = Integer.parseInt(doubles);
			this.triples = Integer.parseInt(triples);
			this.homers = Integer.parseInt(homers);
			this.rbi = Integer.parseInt(rbi);
			this.runs = Integer.parseInt(runs);
			
			int singles = this.hits - this.doubles - this.triples - this.homers;
			this.totalBases = singles + (2 * this.doubles) + (3 * this.triples) + (4 * this.homers);
		}
		
		private void endGame() {
			stillPlaying = false;
		}
	}
	
	public class PitcherStats {
		public String playerId;
		public int strikeouts;
		public transient boolean stillPlaying = true;
		
		private void set(String playerId, String strikeouts) {
			this.playerId = playerId;
			this.strikeouts = Integer.parseInt(strikeouts);
		}
		
		private void endGame() {
			stillPlaying = false;
		}
	}
	
	
	private static final String ENDPOINT_PT1 = "https://baseballsavant.mlb.com/gamefeed?gamePk=";
	private static final String ENDPOINT_PT2 = "&hf=boxScore";
	private static final String GAME_STATUS = "game-status_";
	private static final String AWAY_SCORE = "away-score_";
	private static final String HOME_SCORE = "home-score_";
	private static final String BOX_SCORE = "boxScoreTable_";
	private static final String LINE_SCORE = "lineScore_";
	
	public Game game;
	
	public GameStats gameStats;
	public BatterStats[] awayBatterStats;
	public BatterStats[] homeBatterStats;
	public PitcherStats awayPitcherStats;
	public PitcherStats homePitcherStats;
	
	public boolean isGameLive = false;
	public boolean isGameFinished = false;
	

	
	
	public CurrentGameData(Game game) {
		this.game = game;
		
		gameStats = new GameStats();
		awayBatterStats = new BatterStats[9];
		homeBatterStats = new BatterStats[9];
		for (int i = 0; i < 9; i++) {
			awayBatterStats[i] = new BatterStats();
			homeBatterStats[i] = new BatterStats();
		}
		awayPitcherStats = new PitcherStats();
		homePitcherStats = new PitcherStats();
		
	}
	
	public PitcherStats getPitcherOfId(String playerId) {
		if (homePitcherStats.playerId.equals(playerId)) {
			return homePitcherStats;
		} else if (awayPitcherStats.playerId.equals(playerId)) {
			return awayPitcherStats;
		} else {
			return null;
		}
	}
	
	public BatterStats getBatterOfId(String playerId) {
		for (BatterStats b : awayBatterStats) {
			if (b.playerId.equals(playerId)) {
				return b;
			}
		}
		for (BatterStats b : homeBatterStats) {
			if (b.playerId.equals(playerId)) {
				return b;
			}
		}
		
		return null;
	}
	
	
	
	public void update(WebDriver driver) {
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(Duration.ofMillis(1000))
					.pollingEvery(Duration.ofMillis(500))
					.ignoring(NoSuchElementException.class);

			driver.get(ENDPOINT_PT1 + game.gameId + ENDPOINT_PT2);
			List<WebElement> elems = wait.until(new Function<WebDriver, List<WebElement>>() {
				public List<WebElement> apply(WebDriver driver) {
					List<WebElement> elements = new ArrayList<>();
					elements.add(driver.findElement(By.id(GAME_STATUS + game.gameId)));
					elements.add(driver.findElement(By.id(AWAY_SCORE + game.gameId)));
					elements.add(driver.findElement(By.id(HOME_SCORE + game.gameId)));
					elements.add(driver.findElement(By.id(BOX_SCORE + game.gameId)));
					elements.add(driver.findElement(By.id(LINE_SCORE + game.gameId)));
					return elements;
				}
			});
			
			isGameLive = true;
			
			String[] firstInningScores = extractFirstInningScore(elems.get(4));
			
			gameStats.set(elems.get(0).getText(), elems.get(1).getText(), elems.get(2).getText(), firstInningScores[0], firstInningScores[1]);

			Document doc = Jsoup.parse(elems.get(3).getAttribute("innerHTML"));
			Elements tables = doc.getElementsByClass("table-savant");
			Element awayBatters = tables.get(0);
			Element awayPitchers = tables.get(1);
			Element homeBatters = tables.get(2);
			Element homePitchers = tables.get(3);
			
			extractBatterData(awayBatters, awayBatterStats);
			extractBatterData(homeBatters, homeBatterStats);
			
			extractPitcherData(awayPitchers, awayPitcherStats);
			extractPitcherData(homePitchers, homePitcherStats);
			
			if ("FINAL".equals(gameStats.liveStatus)) {
				Simularity.saveFinalScore(this);
				endGame();
			}

			
		} catch (Throwable enve) {
			isGameLive = false;
		}
	}
	
	private void endGame() {
		awayPitcherStats.endGame();
		homePitcherStats.endGame();
		for (BatterStats bs : awayBatterStats) {
			bs.endGame();
		}
		for (BatterStats bs : homeBatterStats) {
			bs.endGame();
		}
		isGameFinished = true;
	}
	
	private void extractBatterData(Element table, BatterStats[] result) {
		Elements rows = table.getElementsByTag("tr");
		Iterator<Element> rowIt = rows.iterator();
		rowIt.next();
		
		int batterIndex = 0;
		while(rowIt.hasNext()) {
			Element row = rowIt.next();
			String playerId = StringUtils.right(row.getElementsByTag("a").attr("href"), 6);
			Iterator<Element> valIt = row.getElementsByTag("td").iterator();
			List<String> values = new ArrayList<>();
			while(valIt.hasNext()) {
				values.add(valIt.next().text());
			}

			if (StringUtils.startsWith(values.get(0), "⤷")) {
				result[batterIndex - 1].endGame();
			} else {
				result[batterIndex].set(playerId, values.get(4), values.get(5), values.get(6), values.get(7), values.get(8), values.get(3));
				batterIndex++;
			}
		}
	}
	
	private void extractPitcherData(Element table, PitcherStats result) {
		Elements rows = table.getElementsByTag("tr");
		Iterator<Element> rowIt = rows.iterator();
		rowIt.next();
		int index = 0;
		while(rowIt.hasNext()) {
			Element row = rowIt.next();
			String playerId = StringUtils.right(row.getElementsByTag("a").attr("href"), 6);
			
			Iterator<Element> valIt = row.getElementsByTag("td").iterator();
			List<String> values = new ArrayList<>();
			while(valIt.hasNext()) {
				values.add(valIt.next().text());
			}
			
			if (index == 0) {
				result.set(playerId, values.get(6));
				if ("9.0".equals(values.get(1))) {
					result.endGame();
				}
			} else {
				result.endGame();
				break;
			}
			
			index++;
		}
	}
	
	private String[] extractFirstInningScore(WebElement element) {
		Document doc = Jsoup.parse(element.getAttribute("innerHTML"));
		Elements rows = doc.getElementsByTag("tr");
		Element awayRow = rows.get(1);
		Element homeRow = rows.get(2);
		Elements awayScores = awayRow.getElementsByTag("td");
		Elements homeScores = homeRow.getElementsByTag("td");
		
		String[] runs = new String[2];
		runs[0] = awayScores.get(1).text();
		runs[1] = homeScores.get(1).text();
		
		return runs;
	}
}
