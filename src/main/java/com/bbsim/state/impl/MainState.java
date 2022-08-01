package com.bbsim.state.impl;

import com.bbsim.App;
import com.bbsim.state.ScreenState;

public class MainState extends ScreenState
{

	@Override
	public void init(Object... params) {
		System.out.println("Start of main state");
	}

	@Override
	public void end() {
		System.out.println("End of main state");
	}

	@Override
	public void update() {
		//clearConsole();
		System.out.println("Update main state");
	}

	@Override
	public void handleInput(String input) {
		if ("next".equals(input)) {
			this.changeState(App.OTHER_STATE);
		} else if ("games".equals(input)){
			this.changeState(App.GAME_PICKER_STATE);
			
		} else {
			System.out.println("unknown input!");
		}
	}

}
