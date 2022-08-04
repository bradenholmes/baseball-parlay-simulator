package com.bbsim.state.impl;

import java.util.ArrayList;
import java.util.List;

import com.bbsim.ApiQuery.Game;
import com.bbsim.App;
import com.bbsim.Bet;
import com.bbsim.Parlay;
import com.bbsim.state.ScreenState;

public class ParlayPickerState extends ScreenState
{
	List<Parlay> parlays;
	
	@Override
	public void init(Object... params) {
		this.clearConsole();
		if (params.length != 1) {
			System.out.println("Wrong number of args passed to parlay picker state");
			return;
		}
		
		if (params[0] instanceof ArrayList<?>) {
			parlays = new ArrayList<>();
			for (Object p : ((ArrayList<?>) params[0])) {
				if (p instanceof Parlay) {
					parlays.add((Parlay) p);
				}
			}
		}
		
		
		System.out.println("Select a parlay: ");
		for (int i = 0; i < parlays.size(); i++) {
			System.out.println("    " + i + ".) " + parlays.get(i).getGame().toString());
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
		if ("help".equals(input)) {
			System.out.println("Enter a number to choose a game, or type 'back' to go back.");
		}
		else if ("back".equals(input)) {
			this.changeState(App.MAIN_STATE);
			return;
		} else {
			try {
				int number = Integer.parseInt(input);
				if (number >= 0 && number < parlays.size()) {
					this.changeState(App.PARLAY_BUILDER_STATE, parlays.get(number));
					return;
				} else {
					System.out.println("Game of index " + number + " does not exist!");
				}
				
				
			} catch (Exception e) {
				System.out.println("Unrecognized input!");
				e.printStackTrace();
			}
			
		}
	}

}
