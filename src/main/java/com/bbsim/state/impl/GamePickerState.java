package com.bbsim.state.impl;

import java.util.List;

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
		System.out.println("Select a game: ");
		for (int i = 0; i < allGames.size(); i++) {
			System.out.println("    " + i + ".) " + allGames.get(i).toString());
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
		} else {
			try {
				int number = Integer.parseInt(input);
				if (number >= 0 && number < allGames.size()) {
					this.changeState(App.SIMULATION_STATE, allGames.get(number));
				} else {
					System.out.println("Game of index " + number + " does not exist!");
				}
				
				
			} catch (Exception e) {
				System.out.println("Unrecognized input!");
			}
			
		}
	}

}
