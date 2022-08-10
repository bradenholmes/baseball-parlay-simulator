package com.bbsim;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.CurrentGameData.BatterStats;
import com.bbsim.CurrentGameData.PitcherStats;

public class Bet implements Comparable<Bet>
{
	
	private static final char CHECK = '$';
	private static final char FAIL = 'X';
	private static final char WINNING_NOW = '~';
	private static final char LOSING_NOW = '!';
	private static final char EMPTY = ' ';
	
	BetClass betClass;
	BetType type;
	
	float value;
	
	//Subjects
	Team favorite;
	Team underdog;
	Batter batter;
	Pitcher pitcher;
	
	float expectedProbability;
	

	
	public Bet(BetClass betClass, BetType type) {
		this.type = type;
		this.betClass = betClass;
		this.expectedProbability = -1;
	}
	
	public void setSubject(Team favorite, Team underdog) {
		this.favorite = favorite;
		this.underdog = underdog;
		this.batter = null;
		this.pitcher = null;
	}
	
	public void setSubject(Batter batter) {
		this.batter = batter;
		this.pitcher = null;
		this.favorite = null;
		this.underdog = null;
	}
	
	public void setSubject(Pitcher pitcher) {
		this.pitcher = pitcher;
		this.batter = null;
		this.favorite = null;
		this.underdog = null;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public BetClass getBetClass() {
		return this.betClass;
	}
	
	public BetType getBetType() {
		return this.type;
	}
	
	public boolean requiresValue() {
		return this.type.doesRequireValue();
	}
	
	public void setExpectedProbability(float probability) {
		this.expectedProbability = probability;
	}
	
	public float getExpectedProbability() {
		return this.expectedProbability;
	}
	
	public boolean evaluate(GameSimulation sim) {
		boolean result = false;
		switch (type) {
			case MONEY_LINE:
				if (favorite.getHomeAway() == StateVar.HOME) {
					if (favorite.getRuns() > underdog.getRuns()) {
						result = true;
					}
				} else {
					if (favorite.getRuns() > underdog.getRuns()) {
						result = true;
					}
				}

				break;
			case RUN_LINE:
				if (favorite.getRuns() + value > underdog.getRuns()) {
					result = true;
				}
				break;
			case RUNS_OVER:
				if (favorite.getRuns() + underdog.getRuns() > value) {
					result = true;
				}
				break;
			case RUNS_UNDER:
				if (favorite.getRuns() + underdog.getRuns() < value) {
					result = true;
				}
				break;
			case FIRST_INNING:
				FirstInningBet fiBet = FirstInningBet.ofOridinal((int)value);
				switch (fiBet) {
					//With the way teams are set for game bets: favorite is home, underdog is away
					case ZERO_ZERO:
						if (favorite.getFirstInningRuns() == 0 && underdog.getFirstInningRuns() == 0) {
							result = true;
						}
						break;
					case HOME_WIN:
						if (favorite.getFirstInningRuns() > underdog.getFirstInningRuns()) {
							result = true;
						}
						break;
					case TIE:
						if (favorite.getFirstInningRuns() == underdog.getFirstInningRuns()) {
							result = true;
						}
						break;
					case AWAY_WIN:
						if (favorite.getFirstInningRuns() > underdog.getFirstInningRuns()) {
							result = true;
						}
						break;
				}
				//betString = "First inning " + FirstInningBet.ofOridinal((int)value);
				break;
			case SO_OVER:
				if (pitcher.getSOs() > value) {
					result = true;
				}
				break;
			case ONE_HIT:
				if (batter.getHits() >= 1) {
					result = true;
				}
				break;
			case TWO_HIT:
				if (batter.getHits() >= 2) {
					result = true;
				}
				break;
			case TWO_BASES:
				if (batter.getBases() >= 2) {
					result = true;
				}
				break;
			case THREE_BASES:
				if (batter.getBases() >= 3) {
					result = true;
				}
				break;
			case HOME_RUN:
				if (batter.getHomers() >= 1) {
					result = true;
				}
				break;
			case RBI:
				if (batter.getRBI() >= 1) {
					result = true;
				}
				break;
			case RUN:
				if (batter.getRuns() >= 1) {
					result = true;
				}
				break;
		}
		
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String namePart;
		String valuePart;
		if (betClass == BetClass.BATTER) {
			namePart = batter.getName();
			valuePart = "" + type;
		} else {
			switch (type) {
				case MONEY_LINE:
					namePart = favorite.getName();
					valuePart = "win";
					break;
				case RUN_LINE:
					namePart = favorite.getName();
					valuePart = (value > 0 ? "+" : "") + value;
					break;
				case RUNS_OVER:
					namePart = "Over";
					valuePart = value + " runs";
					break;
				case RUNS_UNDER:
					namePart = "Under";
					valuePart = value + " runs";
					break;
				case FIRST_INNING:
					namePart = "First inning";
					valuePart = createFirstInningValPart();
					break;
				case SO_OVER:
					namePart = pitcher.getName();
					valuePart = (int) value + "+ SO's";
					break;
				default:
					namePart = "";
					valuePart = "";
					break;
			}
		}
		sb.append(StringUtils.leftPad(namePart, 18));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		if (expectedProbability != -1) {
			sb.append(App.percentage(expectedProbability));
		}
		
		return sb.toString();
	}
	
	public void print() {
		System.out.println(App.leftJustifyText(toString(), 1, true));
	}
	
	public boolean printStatus(CurrentGameData gameData) {
		if (gameData == null || !gameData.isGameLive) {
			printStatusInactive();
			return true;
		} else {
			return printStatusActive(gameData);
		}
	}
	
	private boolean printStatusActive(CurrentGameData gameData) {
		StringBuilder sb = new StringBuilder();
		String namePart;
		String valuePart;
		
		char[] boxes = null;
		PitcherStats pDat;
		BatterStats bDat;

		switch (type) {
			case MONEY_LINE:
				namePart = favorite.getName();
				valuePart = "win";
				boxes = fillGameBoxes(type, gameData);
				break;
			case RUN_LINE:
				namePart = favorite.getName();
				valuePart = (value > 0 ? "+" : "") + value;
				boxes = fillGameBoxes(type, gameData);
				break;
			case RUNS_OVER:
				namePart = "Over";
				valuePart = value + " runs";
				boxes = fillGameBoxes(type, gameData);
				break;
			case RUNS_UNDER:
				namePart = "Under";
				valuePart = value + " runs";
				boxes = fillGameBoxes(type, gameData);
				break;
			case FIRST_INNING:
				namePart = "First inning";
				valuePart =  createFirstInningValPart();
				boxes = new char[1];
				boxes[0] = evaluateFirstInning(gameData);
				break;
			case SO_OVER:
				namePart = pitcher.getName();
				valuePart = (int) value + "+ SO's";
				pDat = gameData.getPitcherOfId(pitcher.getPlayerId());
				boxes = fillBoxes((int) value, pDat.strikeouts, pDat.stillPlaying);
				break;
			case ONE_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(1, bDat.hits, bDat.stillPlaying);
				break;
			case TWO_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(2, bDat.hits, bDat.stillPlaying);
				break;
			case TWO_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				if (bDat == null) {
					boxes = emptyBoxes(2);
				} else {
					boxes = fillBoxes(2, bDat.totalBases, bDat.stillPlaying);
				}
				break;
			case THREE_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(3, bDat.totalBases, bDat.stillPlaying);
				break;
			case HOME_RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(1, bDat.homers, bDat.stillPlaying);
				break;
			case RBI:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(1, bDat.rbi, bDat.stillPlaying);
				break;
			case RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = fillBoxes(1, bDat.runs, bDat.stillPlaying);
				break;
			default:
				namePart = "";
				valuePart = "";
		}

		sb.append(StringUtils.leftPad(namePart, 18));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		sb.append(createCheckboxes(boxes));
		
		System.out.println(App.leftJustifyText(sb.toString(), 1, true));
		
		for (char c : boxes) {
			if (c == FAIL) {
				return false;
			}
		}
		return true;
		
	}
	
	private void printStatusInactive() {
		StringBuilder sb = new StringBuilder();
		String namePart;
		String valuePart;
		
		char[] boxes = null;

		switch (type) {
			case MONEY_LINE:
				namePart = favorite.getName();
				valuePart = "win";
				boxes = emptyBoxes(1);
				
				break;
			case RUN_LINE:
				namePart = favorite.getName();
				valuePart = (value > 0 ? "+" : "") + value;
				boxes = emptyBoxes(1);
				break;
			case RUNS_OVER:
				namePart = "Over";
				valuePart = value + " runs";
				boxes = emptyBoxes(1);
				break;
			case RUNS_UNDER:
				namePart = "Under";
				valuePart = value + " runs";
				boxes = emptyBoxes(1);
				break;
			case FIRST_INNING:
				namePart = "First inning";
				valuePart = createFirstInningValPart();
				boxes = emptyBoxes(1);
				break;
			case SO_OVER:
				namePart = pitcher.getName();
				valuePart = (int) value + "+ SO's";
				boxes = emptyBoxes((int) value);		
				break;
			case ONE_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = new char[1];
				boxes = emptyBoxes(1);
				break;
			case TWO_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(2);
				break;
			case TWO_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(2);
				break;
			case THREE_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(3);
				break;
			case HOME_RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(1);
				break;
			case RBI:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(1);
				break;
			case RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				boxes = emptyBoxes(1);
				break;
			default:
				boxes = emptyBoxes(1);
				namePart = "";
				valuePart = "";
		}

		sb.append(StringUtils.leftPad(namePart, 18));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		sb.append(createCheckboxes(boxes));
		
		System.out.println(App.leftJustifyText(sb.toString(), 1, true));
	}

	@Override
	public int compareTo(Bet arg0) {
		return this.type.displayPriority - arg0.type.displayPriority;
	}
	
	private String createCheckboxes(char[] boxes) {
		StringBuilder sb = new StringBuilder();
		if (boxes == null) {
			return "";
		}
		for (int i = 0; i < boxes.length; i++) {
			sb.append("[");
			sb.append(boxes[i]);
			sb.append("]");
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	private char[] emptyBoxes(int count) {
		char[] boxes = new char[count];
		for (int i = 0; i < count; i++) {
			boxes[i] = EMPTY;
		}
		return boxes;
	}
	
	private char[] fillGameBoxes(BetType type, CurrentGameData gameData) {
		char[] boxes = new char[1];
		
		int favRuns = 0;
		int udRuns = 0;
		if (favorite != null && underdog != null) {
			favRuns = favorite.getHomeAway() == StateVar.HOME ? gameData.gameStats.homeScore : gameData.gameStats.awayScore;
			udRuns = underdog.getHomeAway() == StateVar.HOME ? gameData.gameStats.homeScore : gameData.gameStats.awayScore;
		}
		
		char winningChar;
		char losingChar;
		if ("FINAL".equals(gameData.gameStats.liveStatus)) {
			winningChar = CHECK;
			losingChar = FAIL;
		} else {
			winningChar = WINNING_NOW;
			losingChar = LOSING_NOW;
		}
		
		char result;
		switch (type) {
			case MONEY_LINE:
				if (favRuns > udRuns) {
					result = winningChar;
				} else {
					result = losingChar;
				}
				break;
			case RUN_LINE:
				if (favRuns + value > udRuns) {
					result = winningChar;
				} else {
					result = losingChar;
				}
				break;
			case RUNS_OVER:
				if (favRuns + udRuns > value) {
					result = CHECK;
				} else {
					result = losingChar;
				}
				break;
			case RUNS_UNDER:
				if (favRuns + udRuns > value) {
					result = FAIL;
				} else {
					result = winningChar;
				}
				break;
			default:
				result = EMPTY;
		}
		
		boxes[0] = result;
		return boxes;
	}
	
	private char[] fillBoxes(int numBoxes, int numChecks, boolean stillPlaying) {
		char[] boxes = new char[numBoxes];
		for (int i = 0; i < numBoxes; i++) {
			if (stillPlaying) {
				boxes[i] = EMPTY;
			} else {
				boxes[i] = FAIL;
			}
		}
		
		if (numChecks > numBoxes) numChecks = numBoxes;
		for (int i = 0; i < numChecks; i++) {
			boxes[i] = CHECK;
		}
		
		return boxes;
		
	}
	
	private String createFirstInningValPart() {
		FirstInningBet fib = FirstInningBet.ofOridinal((int) value);
		String homeName = favorite.getHomeAway() == StateVar.HOME ? favorite.getName() : underdog.getName();
		String awayName = underdog.getHomeAway() == StateVar.AWAY ? underdog.getName() : favorite.getName();
		
		String valuePart;

		switch (fib) {
			case ZERO_ZERO:
				valuePart = "0-0";
				break;
			case AWAY_WIN:
				valuePart = awayName + " win";
				break;
			case TIE:
				valuePart = "tie";
				break;
			case HOME_WIN:
				valuePart = homeName + " win";
				break;
			default:
				valuePart = "";
		}
		
		return valuePart;
	}
	
	private char evaluateFirstInning(CurrentGameData gameData) {
		FirstInningBet fib = FirstInningBet.ofOridinal((int) value);

		char winningChar;
		char losingChar;
		if (gameData.gameStats.currentInning > 1) {
			winningChar = CHECK;
			losingChar = FAIL;
		} else {
			winningChar = WINNING_NOW;
			losingChar = LOSING_NOW;
		}
		
		char result;
		switch (fib) {
			case ZERO_ZERO:
				if (gameData.gameStats.homeFirstScore == 0 && gameData.gameStats.awayFirstScore == 0) {
					result = winningChar;
				} else {
					result = FAIL;
				}
				break;
			case AWAY_WIN:
				if (gameData.gameStats.homeFirstScore < gameData.gameStats.awayFirstScore) {
					result = winningChar;
				} else {
					result = losingChar;
				}
				break;
			case TIE:
				if (gameData.gameStats.homeFirstScore == gameData.gameStats.awayFirstScore) {
					result = winningChar;
				} else {
					result = losingChar;
				}
				break;
			case HOME_WIN:
				if (gameData.gameStats.homeFirstScore > gameData.gameStats.awayFirstScore) {
					result = winningChar;
				} else {
					result = losingChar;
				}
				break;
			default:
				result = ' ';
		}
		
		return result;
	}
	
	
	
}
