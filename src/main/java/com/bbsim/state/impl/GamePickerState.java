package com.bbsim.state.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bbsim.ApiQuery.Game;
import com.bbsim.App;
import com.bbsim.state.ScreenState;

public class GamePickerState extends ScreenState
{
	private List<Game> allGames;
	
	public GamePickerState(List<Game> allGames) {
		this.allGames = allGames;
	}

	@Override
	public void init(Object... params) {
		this.clearConsole();
		System.out.println("Select a game:   (enter number next to desired game)");
		for (int i = 0; i < allGames.size(); i++) {
			System.out.println(StringUtils.leftPad("  " + i + ".) ", 8) + allGames.get(i).toString());
		}
	}

	@Override
	public void end() {
		
	}

	@Override
	public void update() {
		
	}

	@Override
	public void handleInput(String input) {
		try {
			int number = this.getManager().handleIntegerInput(input, 0, allGames.size(), true);
			if (number == App.UNSET_INT) {
				this.changeState(App.MAIN_STATE);
				return;
			} else {
				Game g = allGames.get(number);
				/* FUCK THIS I KEEP LOSING MONEY
				if (this.getManager().askConfirmation("Would you like to run moneyline analysis?")) {
					boolean gotten = false;
					while (!gotten) {
						System.out.println("Enter odds for " + g.awayTeam);
						int awayOdds = this.getManager().getIntegerInput(-500, 500, false);
						System.out.println("Enter odds for " + g.homeTeam);
						int homeOdds = this.getManager().getIntegerInput(-500, 500, false);
						
						if (Math.signum(awayOdds) == Math.signum(homeOdds)) {
							System.out.println("These are both " + (Math.signum(awayOdds) == 1 ? "positive" : "negative") + "... let's try that again");
						} else {
							g.setMoneylineOdds(homeOdds, awayOdds);
							gotten = true;
						}
					}
				}
				*/
				
				this.changeState(App.SIMULATION_STATE, allGames.get(number));
				return;
			}
			
			
		} catch (Exception e) {
			System.out.println("Unrecognized input!");
		}
	}

}
