package com.bbsim;

import java.util.Random;

public class GameSimulation
{
	private static final float SCORE_FROM_SECOND_CHANCE_0_OUT = 0.39f;
	private static final float SCORE_FROM_SECOND_CHANCE_1_OUT = 0.51f;
	private static final float SCORE_FROM_SECOND_CHANCE_2_OUT = 0.76f;
	private static final float SCORE_FROM_FIRST_CHANCE = 0.40f;
	
	private Team homeTeam;
	private Team awayTeam;
	
	int inning;
	StateVar topBottom;
	StateVar gameStatus;
	int outs;
	
	Batter firstBase;
	Batter secondBase;
	Batter thirdBase;
	
	int homeLineupIdx;
	int awayLineupIdx;
	
	public GameSimulation(Team homeTeam, Team awayTeam) {
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		
		inning = 1;
		topBottom = StateVar.TOP;
		gameStatus = StateVar.REGULATION;
		outs = 0;
		
		firstBase = null;
		secondBase = null;
		thirdBase = null;
		
		homeLineupIdx = 0;
		awayLineupIdx = 0;
	}
	
	public Team getWinningTeam() {
		if (homeTeam.getRuns() == awayTeam.getRuns()) {
			return null;
		}
		if (homeTeam.getRuns() > awayTeam.getRuns()) {
			return homeTeam;
		} else {
			return awayTeam;
		}
	}
	
	/**
	 * Execute one simulation step
	 * @return false if the game should be concluded. true otherwise
	 */
	public boolean step(boolean print) {
		//Game end condition. Ignore ties + extras
		if (inning >= 9) {
			if (topBottom == StateVar.BOTTOM && (homeTeam.getRuns() > awayTeam.getRuns())) {
				return false;
			}
		}
		
		//inning changing
		if (outs >= 3) {
			outs = 0;
			firstBase = null;
			secondBase = null;
			thirdBase = null;
			if (topBottom == StateVar.TOP) {
				topBottom = StateVar.BOTTOM;
				if (gameStatus == StateVar.EXTRA_INNINGS) {
					//I don't want to store the 'last out' in order to determine who should start on 2nd.
					//Instead I'll just put any batter there, and I won't count runs scored past 9 innings.
					secondBase = homeTeam.getBatter(0);
				}
			} else {
				
				if (inning == 1) {
					homeTeam.addFirstInningRuns(homeTeam.getRuns());
					awayTeam.addFirstInningRuns(awayTeam.getRuns());
				}
				
				topBottom = StateVar.TOP;
				inning++;
				
				if (inning > 9) {
					gameStatus = StateVar.EXTRA_INNINGS;
					//See above comment
					secondBase = awayTeam.getBatter(0);
				}
				
				
			}
			return true;
		}
		
		Result r;
		Pitcher p;
		Batter b;
		//Simulate
		if (topBottom == StateVar.TOP) {
			p = homeTeam.getPitcher();
			b = awayTeam.getBatter(awayLineupIdx);
			

			trySteal();
			r = simulateAB(p, b);
			awayTeam.addRuns(handleResult(r, p, b));
			nextAwayBatter();
		} else {
			p = awayTeam.getPitcher();
			b = homeTeam.getBatter(homeLineupIdx);
			
			trySteal();
			r = simulateAB(p, b);
			homeTeam.addRuns(handleResult(r, p, b));
			nextHomeBatter();
		}
		
		if (print) {
			System.out.println("--------------------- Step -----------------------");
			System.out.println("Batter: " + b.getName() + "      result: " + r);
			if (firstBase != null) System.out.println("    First: " + (firstBase.getName()));
			if (secondBase != null) System.out.println("    Second: " + secondBase.getName());
			if (thirdBase != null) System.out.println("    Third: " + thirdBase.getName());
			System.out.println();
			System.out.println(awayTeam.getName() + ": " + awayTeam.getRuns() + "   " + homeTeam.getName() + ": " + homeTeam.getRuns());
			System.out.println(topBottom + " " + inning + "  outs: " + outs);
		}
		
		return true;
	}
	
	private Result simulateAB(Pitcher p, Batter b) {
		float kChance;
		float bbChance;
		float hitChance;
		if (p.shouldInclude() && b.shouldInclude()) {
			kChance = combineAvgs(p.getKChance(), b.getKChance(p.getHandedness()));
			bbChance = combineAvgs(p.getWalkChance(), b.getWalkChance(p.getHandedness()));
			hitChance = combineAvgs(p.getHitChance(), b.getHitChance(p.getHandedness()));
		} else if (p.shouldInclude()) {
			kChance = p.getKChance();
			bbChance = p.getWalkChance();
			hitChance = p.getHitChance();
		} else if (b.shouldInclude()) {
			kChance = b.getKChance(p.getHandedness());
			bbChance = b.getWalkChance(p.getHandedness());
			hitChance = b.getHitChance(p.getHandedness());
		} else {
			kChance = 0;
			bbChance = 0;
			hitChance = 0;
		}
		
		float val = getRandomFloat();
		if (val <= kChance) {
			return Result.SO;
		} else if (val <= kChance + bbChance) {
			return Result.BB;
		} else if (val <= kChance + bbChance + hitChance) {
			return b.guessHitOutcome(p.getHandedness());
		} else {
			return Result.OUT;
		}
	}
	
	private int handleResult(Result r, Pitcher p, Batter b) {
		int runsScored = 0;
		int hitIn = 0;
		switch (r) {
			case OUT:
				//GIDP case
				if (outs < 2 && firstBase != null && b.doesGIDP()) {
					firstBase = null;
					outs++;
				}
				//Sacrifice fly case
				else if (outs < 2 && thirdBase != null && b.doesFlyout()) {
					thirdBase.addRun();
					runsScored++;
					b.addRBI(1);
					thirdBase = null;
				}
				outs++;
				break;
			case SO:
				outs++;
				if (inning <= p.getAvgInningsPerGame()) {
					p.addSO();
				}
				break;
			case BB:
				int walkedIn = walk();
				b.addRBI(walkedIn);
				runsScored += walkedIn;
				firstBase = b;
				break;
			case SINGLE:
				b.addHit();
				b.addBases(1);
				hitIn = moveRunners(1);
				b.addRBI(hitIn);
				runsScored += hitIn;
				firstBase = b;
				break;
			case DOUBLE:
				b.addHit();
				b.addBases(2);
				hitIn = moveRunners(2);
				b.addRBI(hitIn);
				runsScored += hitIn;
				secondBase = b;
				break;
			case TRIPLE:
				b.addHit();
				b.addBases(3);
				hitIn = moveRunners(3);
				b.addRBI(hitIn);
				runsScored += hitIn;
				thirdBase = b;
				break;
			case HOMER:
				b.addHit();
				b.addBases(4);
				b.addHomer();
				hitIn = moveRunners(3);
				b.addRBI(hitIn);
				b.addRBI(1);
				runsScored += hitIn;
				runsScored += 1;
				break;
		}
		
		return runsScored;
	}
	
	
	private int moveRunners(int n) {
		int runsScored = 0;
		
		Batter onFirst = firstBase;
		Batter onSecond = secondBase;
		Batter onThird = thirdBase;
		
		if (n == 1) {
			if (onThird != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					thirdBase.addRun();
				}
				
				thirdBase = null;
			}
			if (onSecond != null) {
				float val = getRandomFloat();
				float score_pct = 0;
				if (outs == 0) {
					score_pct = SCORE_FROM_SECOND_CHANCE_0_OUT;
				} else if (outs == 1) {
					score_pct = SCORE_FROM_SECOND_CHANCE_1_OUT;
				} else if (outs == 2) {
					score_pct = SCORE_FROM_SECOND_CHANCE_2_OUT;
				}
				
				if (val <= score_pct) {
					if (gameStatus == StateVar.REGULATION) {
						secondBase.addRun();
					}
					runsScored++;
					secondBase = null;
				} else {
					thirdBase = onSecond;
					secondBase = null;
				}
				
			}
			if (onFirst != null) {
				secondBase = onFirst;
				firstBase = null;
			}
		} else if (n == 2) {
			if (onThird != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					thirdBase.addRun();
				}
				thirdBase = null;
			}
			if (onSecond != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					secondBase.addRun();
				}
				secondBase = null;
			}
			if (onFirst != null) {
				float val = getRandomFloat();
				if (val <= SCORE_FROM_FIRST_CHANCE) {
					if (gameStatus == StateVar.REGULATION) {
						firstBase.addRun();
					}
					runsScored++;
					firstBase = null;
				} else {
					thirdBase = onFirst;
					firstBase = null;
				}
				
			}
		} else if (n == 3) {
			if (onThird != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					thirdBase.addRun();
				}
				thirdBase = null;
			}
			if (onSecond != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					secondBase.addRun();
				}
				secondBase = null;
			}
			if (onFirst != null) {
				runsScored++;
				if (gameStatus == StateVar.REGULATION) {
					firstBase.addRun();
				}
				firstBase = null;
			}
		}
		
		return runsScored;
	}
	
	private int walk() {
		//Bases loaded case
		if (thirdBase != null && secondBase != null && firstBase != null) {
			thirdBase.addRun();
			thirdBase = secondBase;
			secondBase = firstBase;
			firstBase = null;
			return 1;
		}
		
		//1st + 2nd, 3rd empty
		if (secondBase != null && firstBase != null) {
			thirdBase = secondBase;
			secondBase = firstBase;
			firstBase = null;
			return 0;
		}
		
		//1st, 2nd empty
		if (firstBase != null) {
			secondBase = firstBase;
			firstBase = null;
			return 0;
		}
		
		return 0;
	}
	
	private void trySteal() {
		//Handle stealing
		if (firstBase != null && secondBase == null) {
			if (firstBase.doesAttemptSteal()) {
				if (firstBase.doesStealSuccessfully()) {
					secondBase = firstBase;
					firstBase = null;
				} else {
					firstBase = null;
					outs ++;
				}
			}
		}
	}
	
	private void nextHomeBatter() {
		homeLineupIdx++;
		if (homeLineupIdx == 9) {
			homeLineupIdx = 0;
		}
	}
	
	private void nextAwayBatter() {
		awayLineupIdx++;
		if (awayLineupIdx == 9) {
			awayLineupIdx = 0;
		}
	}
	
	private float getRandomFloat() {
		Random random = new Random();
		for (int i = 0; i < 37; i++) {
			random.nextFloat();
		}
		float val = random.nextFloat();
		
		if (val > 1.0f) {
			val = 1;
		}
		
		if (val < 0f) {
			val = 0;
		}
		
		
		return random.nextFloat();
	}
	
	private float combineAvgs(float avg1, float avg2) {
		return (avg1 + avg2) / 2;
	}

	

}
