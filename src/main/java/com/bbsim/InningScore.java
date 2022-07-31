package com.bbsim;

public class InningScore
{
	int homeRuns;
	int awayRuns;
	
	public InningScore(int home, int away) {
		this.homeRuns = home;
		this.awayRuns = away;
	}
	
	public int getHomeScore() {
		return homeRuns;
	}
	
	public int getAwayScore() {
		return awayRuns;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof InningScore)) {
			return false;
		}
		InningScore op = (InningScore) o;
		if (this.homeRuns == op.homeRuns && this.awayRuns == op.awayRuns) {
			return true;
		}
		
		return false;
	}
	
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = (result * PRIME) + this.homeRuns;
		result = (result * PRIME) + this.awayRuns;
		return result;
	}
}
