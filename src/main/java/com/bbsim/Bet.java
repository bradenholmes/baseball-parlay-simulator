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
				if (favorite.getRuns() >= underdog.getRuns()) {
					result = true;
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
						if (favorite.getFirstInningRuns() < underdog.getFirstInningRuns()) {
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
	
	public void print() {
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
					valuePart = "" + FirstInningBet.ofOridinal((int)value);
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
		sb.append(StringUtils.leftPad(namePart, 14));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		if (expectedProbability != -1) {
			sb.append(App.percentage(expectedProbability));
		}
		
		System.out.println(App.leftJustifyText(sb.toString(), 5, true));
	}
	
	public void printStatus(CurrentGameData gameData) {
		if (gameData == null || !gameData.isGameLive) {
			printStatusInactive();
		} else {
			printStatusActive(gameData);
		}
	}
	
	private void printStatusActive(CurrentGameData gameData) {
		StringBuilder sb = new StringBuilder();
		String namePart;
		String valuePart;
		
		char[] boxes = null;
		PitcherStats pDat;
		BatterStats bDat;
		
		int favRuns = 0;
		int udRuns = 0;
		if (favorite != null && underdog != null) {
			favRuns = favorite.getHomeAway() == StateVar.HOME ? gameData.gameStats.homeScore : gameData.gameStats.awayScore;
			udRuns = underdog.getHomeAway() == StateVar.HOME ? gameData.gameStats.homeScore : gameData.gameStats.awayScore;
		}

		switch (type) {
			case MONEY_LINE:
				namePart = favorite.getName();
				valuePart = "win";
				boxes = new char[1];
				if ("FINAL".equals(gameData.gameStats.liveStatus)) {
					if (favRuns > udRuns) {
						boxes[0] = CHECK;
					} else {
						boxes[0] = FAIL;
					}
				} else {
					if (favRuns > udRuns) {
						boxes[0] = WINNING_NOW;
					} else {
						boxes[0] = LOSING_NOW;
					}
				}
				
				break;
			case RUN_LINE:
				namePart = favorite.getName();
				valuePart = (value > 0 ? "+" : "") + value;
				boxes = new char[1];
				if ("FINAL".equals(gameData.gameStats.liveStatus)) {
					if (favRuns + value > udRuns) {
						boxes[0] = CHECK;
					} else {
						boxes[0] = FAIL;
					}
				} else {
					if (favRuns + value > udRuns) {
						boxes[0] = WINNING_NOW;
					} else {
						boxes[0] = LOSING_NOW;
					}
				}
				break;
			case RUNS_OVER:
				namePart = "Over";
				valuePart = value + " runs";
				boxes = new char[1];
				if (gameData.gameStats.awayScore + gameData.gameStats.homeScore > value) {
					boxes[0] = CHECK;
				} else {
					if ("FINAL".equals(gameData.gameStats.liveStatus)) {
						boxes[0] = FAIL;
					} else {
						boxes[0] = LOSING_NOW;
					}
				}
				break;
			case RUNS_UNDER:
				namePart = "Under";
				valuePart = value + " runs";
				boxes = new char[1];
				if (gameData.gameStats.awayScore + gameData.gameStats.homeScore > value) {
					boxes[0] = FAIL;
				} else {
					if ("FINAL".equals(gameData.gameStats.liveStatus)) {
						boxes[0] = CHECK;
					} else {
						boxes[0] = WINNING_NOW;
					}
				}
				break;
			case FIRST_INNING:
				namePart = "First inning";
				valuePart = "" + FirstInningBet.ofOridinal((int)value);
				break;
			case SO_OVER:
				namePart = pitcher.getName();
				valuePart = (int) value + "+ SO's";
				boxes = new char[(int) value];
				pDat = gameData.getPitcherOfId(pitcher.getPlayerId());
				for (int i = 0; i < boxes.length; i++) {
					if (i < pDat.strikeouts) {
						boxes[i] = CHECK;
					} else {
						if (pDat.stillPlaying) {
							boxes[i] = EMPTY;
						} else {
							boxes[i] = FAIL;
						}
						
					}
				}
				
				break;
			case ONE_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[1];
				if (bDat.hits >= 1) {
					boxes[0] = CHECK;
				} else {
					if (bDat.stillPlaying) {
						boxes[0] = EMPTY;
					} else {
						boxes[0] = FAIL;
					}
				}
				break;
			case TWO_HIT:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[2];
				char nohit = bDat.stillPlaying ? EMPTY : FAIL;
				if (bDat.hits >= 2) {
					boxes[0] = CHECK;
					boxes[1] = CHECK;
				} else if (bDat.hits == 1) {
					boxes[0] = CHECK;
					boxes[1] = nohit;
				} else {
					boxes[0] = nohit;
					boxes[1] = nohit;
				}
				break;
			case TWO_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[2];
				char nobase = bDat.stillPlaying ? EMPTY : FAIL;
				if (bDat.totalBases >= 2) {
					boxes[0] = CHECK;
					boxes[1] = CHECK;
				} else if (bDat.totalBases == 1) {
					boxes[0] = CHECK;
					boxes[1] = nobase;
				} else {
					boxes[0] = nobase;
					boxes[1] = nobase;
				}
				break;
			case THREE_BASES:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[3];
				char no = bDat.stillPlaying ? EMPTY : FAIL;
				if (bDat.totalBases >= 3) {
					boxes[0] = CHECK;
					boxes[1] = CHECK;
					boxes[2] = CHECK;
				} else if (bDat.totalBases == 2) {
					boxes[0] = CHECK;
					boxes[1] = CHECK;
					boxes[2] = no;
				} else if (bDat.totalBases == 1) {
					boxes[0] = CHECK;
					boxes[1] = no;
					boxes[2] = no;
				} else {
					boxes[0] = no;
					boxes[1] = no;
					boxes[2] = no;
				}

				break;
			case HOME_RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[1];
				if (bDat.homers >= 1) {
					boxes[0] = CHECK;
				} else {
					if (bDat.stillPlaying) {
						boxes[0] = EMPTY;
					} else {
						boxes[0] = FAIL;
					}
				}
				break;
			case RBI:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[1];
				if (bDat.rbi >= 1) {
					boxes[0] = CHECK;
				} else {
					if (bDat.stillPlaying) {
						boxes[0] = EMPTY;
					} else {
						boxes[0] = FAIL;
					}
				}
				break;
			case RUN:
				namePart = batter.getName();
				valuePart = "" + type;
				bDat = gameData.getBatterOfId(batter.getPlayerId());
				boxes = new char[1];
				if (bDat.runs >= 1) {
					boxes[0] = CHECK;
				} else {
					if (bDat.stillPlaying) {
						boxes[0] = EMPTY;
					} else {
						boxes[0] = FAIL;
					}
				}
				break;
			default:
				namePart = "";
				valuePart = "";
		}

		sb.append(StringUtils.leftPad(namePart, 14));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		sb.append(createCheckboxes(boxes));
		
		System.out.println(App.centerText(sb.toString(), false, true));
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
				valuePart = "" + FirstInningBet.ofOridinal((int)value);
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

		sb.append(StringUtils.leftPad(namePart, 14));
		sb.append(" ");
		sb.append(StringUtils.rightPad(valuePart, 14));
		sb.append(createCheckboxes(boxes));
		
		System.out.println(App.leftJustifyText(sb.toString(), 5, true));
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
	
	
	
}
