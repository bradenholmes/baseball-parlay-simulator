package com.bbsim;

import java.util.List;
import java.util.TimerTask;

import com.bbsim.state.impl.MainState;

public class UpdateTask extends TimerTask
{
	List<CurrentGameData> gameData;
	MainState state;
	public UpdateTask(MainState state, List<CurrentGameData> gameData) {
		this.gameData = gameData;
		this.state = state;
	}
	
	@Override
	public void run() {
		if (state.isActive()) {
			System.out.println("Automatically updating games...");
			state.updateAllGames();
			state.update();
		}
		
	}

}
